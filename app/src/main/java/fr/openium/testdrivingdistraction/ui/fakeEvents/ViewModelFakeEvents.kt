package fr.openium.testdrivingdistraction.ui.fakeEvents

import androidx.lifecycle.ViewModel
import fr.openium.testdrivingdistraction.model.TripEvent
import fr.openium.testdrivingdistraction.repository.TripRepository

/**
 * Created by lgodart on 18/04/2018.
 */
class ViewModelFakeEvents(
    private val tripRepository: TripRepository
) : ViewModel() {

    fun addScreenOnEvent() =
        tripRepository.addEvent(TripEvent.Type.SCREEN_ON, true)

    fun addScreenOffEvent() =
        tripRepository.addEvent(TripEvent.Type.SCREEN_OFF, true)

    fun addScreenLockEvent() =
        tripRepository.addEvent(TripEvent.Type.SCREEN_LOCK, true)

    fun addScreenUnlockEvent() =
        tripRepository.addEvent(TripEvent.Type.SCREEN_UNLOCK, true)

    fun addCallReceivedEvent() =
        tripRepository.addEvent(TripEvent.Type.RECEIVE_CALL, true)

    fun addCallHookedEvent() =
        tripRepository.addEvent(TripEvent.Type.HOOK_CALL, true)

    fun addCallNotHookedEvent() =
        tripRepository.addEvent(TripEvent.Type.NOT_HOOK_CALL, true)

    fun addCallSentEvent() =
        tripRepository.addEvent(TripEvent.Type.SEND_CALL, true)

    fun addSMSReceivedEvent() =
        tripRepository.addEvent(TripEvent.Type.RECEIVE_SMS, true)

    fun isRecording(): Boolean =
        tripRepository.isRecording()

    fun getLastOnOffFakeEvent(): TripEvent? =
        tripRepository.getLastEventOfType(
            listOf(TripEvent.Type.SCREEN_ON, TripEvent.Type.SCREEN_OFF),
            onlyFake = true
        )

    fun getLastUnlockLockFakeEvent(): TripEvent? =
        tripRepository.getLastEventOfType(
            listOf(TripEvent.Type.SCREEN_UNLOCK, TripEvent.Type.SCREEN_LOCK),
            onlyFake = true
        )

    fun getLastCallFakeEvent(): TripEvent? =
        tripRepository.getLastEventOfType(
            listOf(TripEvent.Type.RECEIVE_CALL, TripEvent.Type.HOOK_CALL, TripEvent.Type.NOT_HOOK_CALL),
            onlyFake = true
        )
}