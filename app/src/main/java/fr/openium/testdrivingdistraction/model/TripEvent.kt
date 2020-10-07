package fr.openium.testdrivingdistraction.model

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

    enum class Type {
        SCREEN_ON,
        SCREEN_OFF,
        SCREEN_UNLOCK,
        SCREEN_LOCK,
        RECEIVE_CALL,
        HOOK_CALL,
        NOT_HOOK_CALL,
        SEND_CALL,
        RECEIVE_SMS
    }
}