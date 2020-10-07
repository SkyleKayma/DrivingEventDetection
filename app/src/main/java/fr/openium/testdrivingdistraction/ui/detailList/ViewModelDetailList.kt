package fr.openium.testdrivingdistraction.ui.detailList

import fr.openium.testdrivingdistraction.base.viewModel.AbstractViewModel
import fr.openium.testdrivingdistraction.model.Trip
import fr.openium.testdrivingdistraction.repository.TripRepository
import io.reactivex.Flowable

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelDetailList(
    private val tripRepository: TripRepository
) : AbstractViewModel() {

    fun getAllTripsObs(): Flowable<MutableList<Trip>> =
        tripRepository.getAllTripsObs(realm)

    fun insertNewTrip(trip: Trip) {
        tripRepository.insertNewTrip(trip)
    }
}