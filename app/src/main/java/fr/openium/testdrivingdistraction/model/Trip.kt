package fr.openium.testdrivingdistraction.model

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Trip(
    @PrimaryKey var beginDate: String = "",
    var endDate: String? = null,
    var accelerometer: RealmList<TripAccelerometer> = RealmList(),
    var gyroscope: RealmList<TripGyroscope> = RealmList(),
    var events: RealmList<TripEvent> = RealmList(),
    var sensorEvents: RealmList<TripSensorEvent> = RealmList(),
    var locations: RealmList<TripLocation> = RealmList()
) : RealmModel