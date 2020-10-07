package fr.openium.testdrivingdistraction.model

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class TripLocation(
    var date: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : RealmModel