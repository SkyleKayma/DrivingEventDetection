package fr.openium.testdrivingdistraction.ui.fakeEvents

import fr.openium.testdrivingdistraction.base.viewModel.AbstractViewModel
import fr.openium.testdrivingdistraction.model.TripEvent
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelFakeEvents(
    private val tripRepository: TripRepository
) : AbstractViewModel() {

    fun addScreenOnEvent() {
        tripRepository.addEvent(TripEvent.Type.SCREEN_ON, true)
    }

    fun addScreenOffEvent() {
        tripRepository.addEvent(TripEvent.Type.SCREEN_OFF, true)
    }

    fun addScreenUnlockEvent() {
        tripRepository.addEvent(TripEvent.Type.SCREEN_UNLOCK, true)
    }

    fun addCallReceivedEvent() {
        tripRepository.addEvent(TripEvent.Type.RECEIVE_CALL, true)
    }

    fun addCallHookedEvent() {
        tripRepository.addEvent(TripEvent.Type.HOOK_CALL, true)
    }

    fun addCallNotHookedEvent() {
        tripRepository.addEvent(TripEvent.Type.NOT_HOOK_CALL, true)
    }

    fun addCallSentEvent() {
        tripRepository.addEvent(TripEvent.Type.SEND_CALL, true)
    }

    fun addSMSReceivedEvent() {
        tripRepository.addEvent(TripEvent.Type.RECEIVE_SMS, true)
    }

    fun addUSBAttachedEvent() {
        tripRepository.addEvent(TripEvent.Type.USB_ATTACHED, true)
    }

    fun addUSBDetachedEvent() {
        tripRepository.addEvent(TripEvent.Type.USB_DETACHED, true)
    }

    fun isRecording(): Boolean =
        tripRepository.isRecording()

    fun getLastOnOffFakeEvent(): TripEvent? =
        tripRepository.getLastEventOfType(
            listOf(TripEvent.Type.SCREEN_ON, TripEvent.Type.SCREEN_OFF),
            onlyFake = true
        )

    fun getLastCallFakeEvent(): TripEvent? =
        tripRepository.getLastEventOfType(
            listOf(TripEvent.Type.RECEIVE_CALL, TripEvent.Type.HOOK_CALL, TripEvent.Type.NOT_HOOK_CALL),
            onlyFake = true
        )
}