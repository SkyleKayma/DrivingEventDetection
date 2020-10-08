package fr.openium.testdrivingdistraction.utils

import android.content.Context
import fr.openium.kotlintools.ext.getBooleanPref
import fr.openium.kotlintools.ext.getIntPref
import fr.openium.kotlintools.ext.setBooleanPref
import fr.openium.kotlintools.ext.setIntPref
import fr.openium.testdrivingdistraction.enum.SensorRate

class PreferencesUtils(private val context: Context) {

    // Sort & Filters

    var sensorAccelerometerRate: Int
        get() = context.getIntPref(PREF_SENSOR_ACCELEROMETER_RATE_SELECTED, SensorRate.NORMAL.ordinal)
        set(rate) = context.setIntPref(PREF_SENSOR_ACCELEROMETER_RATE_SELECTED, rate)

    var sensorGyroscopeRate: Int
        get() = context.getIntPref(PREF_SENSOR_GYROSCOPE_RATE_SELECTED, SensorRate.NORMAL.ordinal)
        set(rate) = context.setIntPref(PREF_SENSOR_GYROSCOPE_RATE_SELECTED, rate)

    var accelerometerRecordEnabled: Boolean
        get() = context.getBooleanPref(PREF_ACCELEROMETER_RECORD_ENABLED, true)
        set(isEnabled) = context.setBooleanPref(PREF_ACCELEROMETER_RECORD_ENABLED, isEnabled)

    var gyroscopeRecordEnabled: Boolean
        get() = context.getBooleanPref(PREF_GYROSCOPE_RECORD_ENABLED, true)
        set(isEnabled) = context.setBooleanPref(PREF_GYROSCOPE_RECORD_ENABLED, isEnabled)

    var locationRecordEnabled: Boolean
        get() = context.getBooleanPref(PREF_LOCATION_RECORD_ENABLED, true)
        set(isEnabled) = context.setBooleanPref(PREF_LOCATION_RECORD_ENABLED, isEnabled)

    var eventsRecordEnabled: Boolean
        get() = context.getBooleanPref(PREF_EVENTS_RECORD_ENABLED, true)
        set(isEnabled) = context.setBooleanPref(PREF_EVENTS_RECORD_ENABLED, isEnabled)

    companion object {
        // Sort
        private const val PREF_SENSOR_ACCELEROMETER_RATE_SELECTED = "PREF_SENSOR_ACCELEROMETER_RATE_SELECTED"
        private const val PREF_SENSOR_GYROSCOPE_RATE_SELECTED = "PREF_SENSOR_GYROSCOPE_RATE_SELECTED"
        private const val PREF_ACCELEROMETER_RECORD_ENABLED = "PREF_ACCELEROMETER_RECORD_ENABLED"
        private const val PREF_GYROSCOPE_RECORD_ENABLED = "PREF_GYROSCOPE_RECORD_ENABLED"
        private const val PREF_LOCATION_RECORD_ENABLED = "PREF_LOCATION_RECORD_ENABLED"
        private const val PREF_EVENTS_RECORD_ENABLED = "PREF_EVENTS_RECORD_ENABLED"
    }
}