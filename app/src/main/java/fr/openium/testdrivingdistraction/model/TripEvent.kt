package fr.openium.testdrivingdistraction.model

import fr.openium.testdrivingdistraction.R
import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class TripEvent(
    var date: String? = null,
    var type: String? = null,
    var longitude: Double? = null,
    var latitude: Double? = null,
    var isFake: Boolean = false
) : RealmModel {

    enum class Type(val iconId: Int) {
        SCREEN_ON(R.drawable.ic_event_screen_on),
        SCREEN_OFF(R.drawable.ic_event_screen_off),
        SCREEN_UNLOCK(R.drawable.ic_event_screen_unlock),
        RECEIVE_CALL(R.drawable.ic_event_receive_call),
        HOOK_CALL(R.drawable.ic_event_hook_call),
        NOT_HOOK_CALL(R.drawable.ic_event_not_hook),
        SEND_CALL(R.drawable.ic_event_send_call),
        RECEIVE_SMS(R.drawable.ic_event_sms_receive),
        USB_ATTACHED(R.drawable.ic_event_usb_attached),
        USB_DETACHED(R.drawable.ic_event_usb_attached),
        UNKNOWN(R.drawable.ic_event_unknown);

        companion object {

            fun fromString(value: String?): Type =
                values().find { it.name.equals(value, true) } ?: UNKNOWN
        }
    }
}