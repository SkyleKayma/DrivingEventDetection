package fr.openium.testdrivingdistraction.repository

import android.location.Location
import fr.openium.testdrivingdistraction.model.*
import fr.openium.testdrivingdistraction.utils.DateUtils
import io.realm.Realm
import io.realm.kotlin.where

/**
 * Created by lgodart on 18/04/2018.
 */
class TripRepository(private val dateUtils: DateUtils) {

    // --- Start / Stop record

    fun startTripRecording() {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                realm.insertOrUpdate(
                    Trip(beginDate = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL))
                )
            }
        }
    }

    fun stopTripRecording() {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                getCurrentRecordingTrip()?.let {
                    it.endDate = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL)
                }
            }
        }
    }

    // --- Main function

    private fun getCurrentRecordingTrip(): Trip? =
        Realm.getDefaultInstance().use { realm ->
            realm.where<Trip>().isNull(Trip::endDate.name).findFirst()
        }

    fun isRecording(): Boolean =
        getCurrentRecordingTrip() != null

    // --- Additional features

    fun addEvent(eventType: TripEvent.Type, isFake: Boolean = false) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                val lastKnownLocation = getLastKnownLocation()

                getCurrentRecordingTrip()?.events?.add(
                    TripEvent(
                        date = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL),
                        type = eventType.name,
                        longitude = lastKnownLocation?.longitude ?: 0.0,
                        latitude = lastKnownLocation?.latitude ?: 0.0,
                        isFake
                    )
                )
            }
        }
    }

    fun getLastEventOfType(eventTypeList: List<TripEvent.Type>, onlyFake: Boolean): TripEvent? =
        Realm.getDefaultInstance().use { realm ->
            getCurrentRecordingTrip()?.events?.filter {
                it.type in eventTypeList.map { it.name }
                        && if (onlyFake) it.isFake else true
            }?.lastOrNull()?.let {
                realm.copyFromRealm(it)
            }
        }

    // Location

    fun addLastKnownLocation(location: Location) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                getCurrentRecordingTrip()?.locations?.add(
                    TripLocation(
                        date = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL),
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            }
        }
    }

    private fun getLastKnownLocation(): TripLocation? =
        Realm.getDefaultInstance().use { realm ->
            getCurrentRecordingTrip()?.locations?.lastOrNull()
        }

    // Accelerometer

    fun addAccelerometerValue(x: Float, y: Float, z: Float) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                getCurrentRecordingTrip()?.accelerometer?.add(
                    TripAccelerometer(
                        date = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL),
                        x = x.toDouble(),
                        y = y.toDouble(),
                        z = z.toDouble()
                    )
                )
            }
        }
    }

    // Gyroscope

    fun addGyroscopeValue(x: Float, y: Float, z: Float) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                getCurrentRecordingTrip()?.gyroscope?.add(
                    TripGyroscope(
                        date = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL),
                        x = x.toDouble(),
                        y = y.toDouble(),
                        z = z.toDouble()
                    )
                )
            }
        }
    }

    // Sensor Events

    fun addSensorEvent(eventType: TripSensorEvent.Type) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                val lastKnownLocation = getLastKnownLocation()

                getCurrentRecordingTrip()?.sensorEvents?.add(
                    TripSensorEvent(
                        date = dateUtils.format(System.currentTimeMillis(), DateUtils.Format.DATE_FULL),
                        type = eventType.name,
                        longitude = lastKnownLocation?.longitude ?: 0.0,
                        latitude = lastKnownLocation?.latitude ?: 0.0
                    )
                )
            }
        }
    }

    fun getAllTrips(): MutableList<Trip> =
        Realm.getDefaultInstance().use { realm ->
            realm.copyFromRealm(realm.where(Trip::class.java).findAll())
        }

    fun getTrip(beginDate: String) =
        Realm.getDefaultInstance().use { realm ->
            realm.where(Trip::class.java).equalTo(Trip::beginDate.name, beginDate).findFirst()?.let {
                realm.copyFromRealm(it)
            }
        }
}