package fr.openium.testdrivingdistraction.model

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class TripSensorEvent(
    var date: String? = null,
    var type: String? = null,
    var longitude: Double? = null,
    var latitude: Double? = null
) : RealmModel {

    enum class Type {
        PICK_UP,
        HANG_UP,
        HAND_USAGE
    }
}