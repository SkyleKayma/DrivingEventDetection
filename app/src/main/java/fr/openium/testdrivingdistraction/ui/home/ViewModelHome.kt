package fr.openium.testdrivingdistraction.ui.home

import fr.openium.testdrivingdistraction.base.viewModel.AbstractViewModel
import fr.openium.testdrivingdistraction.model.TripSensorEvent
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelHome(
    private val tripRepository: TripRepository
) : AbstractViewModel() {

    fun isRecording(): Boolean =
        tripRepository.isRecording()

    fun addSensorHangUpEvent() {
        tripRepository.addSensorEvent(TripSensorEvent.Type.HANG_UP)
    }

    fun addSensorHangUsageEvent() {
        tripRepository.addSensorEvent(TripSensorEvent.Type.HAND_USAGE)
    }

    fun addSensorPickUpEvent() {
        tripRepository.addSensorEvent(TripSensorEvent.Type.PICK_UP)
    }

    fun hasRecordedSomeLocations(): Boolean =
        tripRepository.hasRecordedSomeLocations()

    fun deletePendingRecord() {
        tripRepository.deletePendingRecord()
    }
}