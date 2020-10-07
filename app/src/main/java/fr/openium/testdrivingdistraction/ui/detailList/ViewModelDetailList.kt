package fr.openium.testdrivingdistraction.ui.detailList

import androidx.lifecycle.ViewModel
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelDetailList(
    private val tripRepository: TripRepository
) : ViewModel() {

    fun getAllTrips() =
        tripRepository.getAllTrips()
}