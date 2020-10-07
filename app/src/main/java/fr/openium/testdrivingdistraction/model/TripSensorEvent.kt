package fr.openium.testdrivingdistraction.model

import fr.openium.testdrivingdistraction.R
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class TripSensorEvent(
    var date: String? = null,
    var type: String? = null,
    var longitude: Double? = null,
    var latitude: Double? = null
) : RealmModel {

    enum class Type(val iconId: Int) {
        PICK_UP(R.drawable.ic_event_pick_up),
        HANG_UP(R.drawable.ic_event_hang_up),
        HAND_USAGE(R.drawable.ic_event_hand_usage),
        UNKNOWN(R.drawable.ic_event_unknown);

        companion object {

            fun fromString(value: String?): Type =
                values().find { it.name.equals(value, true) } ?: UNKNOWN
        }
    }
}