package fr.openium.testdrivingdistraction.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.enum.SensorRate
import fr.openium.testdrivingdistraction.model.TripEvent
import fr.openium.testdrivingdistraction.repository.TripRepository
import fr.openium.testdrivingdistraction.ui.home.FragmentHome
import fr.openium.testdrivingdistraction.utils.PermissionsUtils
import fr.openium.testdrivingdistraction.utils.PreferencesUtils
import org.koin.android.ext.android.inject


class SensorAndLocationTrackingService : Service(), LocationListener {

    private val tripRepository: TripRepository by inject()
    private val permissionsUtils: PermissionsUtils by inject()
    private val preferencesUtils: PreferencesUtils by inject()

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager

    private var lastPhoneState: String? = TelephonyManager.EXTRA_STATE_IDLE

    // Sensor Listeners

    private var accelerometerListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if (canTrackData()) {
                val x = sensorEvent.values[0]
                val y = sensorEvent.values[1]
                val z = sensorEvent.values[2]
                tripRepository.addAccelerometerValue(x, y, z)

//                Log.d(TAG, "New accelerometer values received x = $x y = $y z = $z")
            }
        }
    }

    private var gyroscopeListener: SensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if (canTrackData()) {
                val x = sensorEvent.values[0]
                val y = sensorEvent.values[1]
                val z = sensorEvent.values[2]
                tripRepository.addGyroscopeValue(x, y, z)

//                Log.d(TAG, "New gyroscope values received x = $x y = $y z = $z")
            }
        }
    }

    // Broadcast Listeners

    private val phoneStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (canTrackData()) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                if (lastPhoneState == TelephonyManager.EXTRA_STATE_IDLE && state == TelephonyManager.EXTRA_STATE_RINGING) {
                    // A new call just arrived
                    tripRepository.addEvent(TripEvent.Type.RECEIVE_CALL)
                } else if (lastPhoneState == TelephonyManager.EXTRA_STATE_RINGING && state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    // I just accepted a call
                    tripRepository.addEvent(TripEvent.Type.HOOK_CALL)
                } else if (lastPhoneState == TelephonyManager.EXTRA_STATE_RINGING && state == TelephonyManager.EXTRA_STATE_IDLE) {
                    // I just canceled a call
                    tripRepository.addEvent(TripEvent.Type.NOT_HOOK_CALL)
                } else if (lastPhoneState == TelephonyManager.EXTRA_STATE_IDLE && state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    // I just started my call
                    tripRepository.addEvent(TripEvent.Type.SEND_CALL)
                }

                lastPhoneState = state
            }
        }
    }

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (canTrackData()) {
                tripRepository.addEvent(TripEvent.Type.RECEIVE_SMS)
            }
        }
    }

    private val screenOnReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (canTrackData()) {
                tripRepository.addEvent(TripEvent.Type.SCREEN_ON)
            }
        }
    }

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (canTrackData()) {
                tripRepository.addEvent(TripEvent.Type.SCREEN_OFF)
            }
        }
    }

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (canTrackData()) {
                tripRepository.addEvent(TripEvent.Type.SCREEN_UNLOCK)
            }
        }
    }

    // --- Life cycle
    // ---------------------------------------------------

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, getNotification())
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? =
        null

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Add sensor listeners
        registerLocationListener()
        registerAccelerometerListener()
        registerGyroscopeListener()

        // Add broadcast event listeners
        if (preferencesUtils.eventsRecordEnabled) {
            applicationContext.registerReceiver(phoneStateReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
            applicationContext.registerReceiver(smsReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            applicationContext.registerReceiver(screenOnReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
            applicationContext.registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
            applicationContext.registerReceiver(unlockReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
        }

        // Start record
        tripRepository.startTripRecording()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove sensor listeners
        unregisterLocationListener()
        unregisterAccelerometerListener()
        unregisterGyroscopeListener()

        // Remove broadcast event listeners
        if (preferencesUtils.eventsRecordEnabled) {
            applicationContext.unregisterReceiver(phoneStateReceiver)
            applicationContext.unregisterReceiver(smsReceiver)
            applicationContext.unregisterReceiver(screenOnReceiver)
            applicationContext.unregisterReceiver(screenOffReceiver)
            applicationContext.unregisterReceiver(unlockReceiver)
        }

        // Stop record
        tripRepository.stopTripRecording()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun registerLocationListener() {
        try {
            if (preferencesUtils.locationRecordEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MILLISECONDS_INTERVAL,
                    METERS_DISTANCE,
                    this
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "No permission given", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Network provider does not exist", e)
        }
    }

    private fun unregisterLocationListener() {
        try {
            if (preferencesUtils.locationRecordEnabled) {
                locationManager.removeUpdates(this)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to remove location listeners")
        }
    }

    private fun registerAccelerometerListener() {
        if (preferencesUtils.accelerometerRecordEnabled) {
            sensorManager.registerListener(
                accelerometerListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorRate.values().first { it.ordinal == preferencesUtils.sensorAccelerometerRate }.getRealSensorRate()
            )

//        Log.d(TAG, "Accelerometer rate used ${SensorRate.values().first { it.ordinal == preferencesUtils.sensorAccelerometerRate }}")
        }
    }

    private fun unregisterAccelerometerListener() {
        if (preferencesUtils.accelerometerRecordEnabled) {
            sensorManager.unregisterListener(accelerometerListener)
        }
    }

    private fun registerGyroscopeListener() {
        if (preferencesUtils.gyroscopeRecordEnabled) {
            sensorManager.registerListener(
                gyroscopeListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorRate.values().first { it.ordinal == preferencesUtils.sensorGyroscopeRate }.getRealSensorRate()
            )

//        Log.d(TAG, "Gyroscope rate used ${SensorRate.values().first { it.ordinal == preferencesUtils.sensorGyroscopeRate }}")
        }
    }

    private fun unregisterGyroscopeListener() {
        if (preferencesUtils.gyroscopeRecordEnabled) {
            sensorManager.unregisterListener(gyroscopeListener)
        }
    }

    private fun getNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, FragmentHome::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${getString(R.string.app_name)} Service")
            .setContentText("Listening for location changes...")
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            if (manager?.getNotificationChannel(CHANNEL_ID) == null) {
                val serviceChannel =
                    NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
                manager?.createNotificationChannel(serviceChannel)
            }
        }
    }

    private fun canTrackData(): Boolean =
        if (permissionsUtils.isLocationPermissionGranted() && permissionsUtils.isBackgroundLocationPermissionGranted()) {
            tripRepository.hasRecordedSomeLocations()
        } else true

    // --- Other methods
    // ---------------------------------------------------

    override fun onLocationChanged(location: Location) {
        tripRepository.addLastKnownLocation(location)
//        Log.d(TAG, "New location received lat = ${location.latitude} long = ${location.longitude}")
    }

    companion object {
        const val TAG = "LocationTrackingService"

        const val CHANNEL_ID = "ForegroundServiceChannel"

        const val MILLISECONDS_INTERVAL = 1000L
        const val METERS_DISTANCE = 1f
    }
}