package fr.openium.testdrivingdistraction.ui.settings

import fr.openium.testdrivingdistraction.base.viewModel.AbstractViewModel
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelSettings(
    private val tripRepository: TripRepository
) : AbstractViewModel() {

    fun isRecording(): Boolean =
        tripRepository.isRecording()
}