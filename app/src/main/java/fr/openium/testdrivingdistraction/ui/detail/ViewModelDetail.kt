package fr.openium.testdrivingdistraction.ui.detail

import fr.openium.testdrivingdistraction.base.viewModel.AbstractViewModel
import fr.openium.testdrivingdistraction.model.Trip
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelDetail(
    private val tripRepository: TripRepository
) : AbstractViewModel() {

    fun getSpecificTrip(beginDate: String) =
        tripRepository.getTrip(beginDate)

    fun deleteTrip(trip: Trip) {
        tripRepository.deleteTrip(trip)
    }
}