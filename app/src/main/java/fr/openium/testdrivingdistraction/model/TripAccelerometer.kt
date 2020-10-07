package fr.openium.testdrivingdistraction.model

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class TripAccelerometer(
    var date: String? = null,
    var x: Double? = null,
    var y: Double? = null,
    var z: Double? = null
) : RealmModel