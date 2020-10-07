package fr.openium.testdrivingdistraction.ui.detail

import androidx.lifecycle.ViewModel
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelDetail(
    private val tripRepository: TripRepository
) : ViewModel() {

    fun getSpecificTrip(beginDate: String) =
        tripRepository.getTrip(beginDate)
}