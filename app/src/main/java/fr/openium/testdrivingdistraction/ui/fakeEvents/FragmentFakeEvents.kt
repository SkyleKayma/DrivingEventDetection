package fr.openium.testdrivingdistraction.ui.fakeEvents

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.kotlintools.ext.hideWithAnimationCompat
import fr.openium.kotlintools.ext.showWithAnimationCompat
import fr.openium.kotlintools.ext.snackbar
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.model.TripEvent
import kotlinx.android.synthetic.main.fragment_fake_events.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentFakeEvents : AbstractFragment(R.layout.fragment_fake_events) {

    private val model: ViewModelFakeEvents by viewModel()

    // --- Life cycle
    // ---------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setListeners()
    }

    override fun onResume() {
        super.onResume()

        if (!isRecording()) {
            findNavController().navigateUp()
        } else setDisplay()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarFakeEvents as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setDisplay() {
        setFakeScreenOnOffButtonDisplay()
        setFakeScreenUnlockLockButtonDisplay()
        setFakeCallStatusButtonDisplay()
    }

    private fun setListeners() {
        buttonHomeFakeScreenOnOffToggle.setOnClickListener {
            if (isRecording()) {
                val lastOnOffFakeEvent = model.getLastOnOffFakeEvent()
                when (lastOnOffFakeEvent?.type) {
                    TripEvent.Type.SCREEN_ON.name ->
                        model.addScreenOffEvent()
                    else ->
                        model.addScreenOnEvent()
                }
                setFakeScreenOnOffButtonDisplay()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeFakeScreenUnlockLockToggle.setOnClickListener {
            if (isRecording()) {
                val lastUnlockLockFakeEvent = model.getLastOnOffFakeEvent()
                when (lastUnlockLockFakeEvent?.type) {
                    TripEvent.Type.SCREEN_UNLOCK.name ->
                        model.addScreenLockEvent()
                    else ->
                        model.addScreenUnlockEvent()
                }
                setFakeScreenUnlockLockButtonDisplay()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeFakeReceiveCall.setOnClickListener {
            if (isRecording()) {
                model.addCallReceivedEvent()
                setFakeCallStatusButtonDisplay()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeFakeHookCall.setOnClickListener {
            if (isRecording()) {
                model.addCallHookedEvent()
                setFakeCallStatusButtonDisplay()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeFakeNotHookCall.setOnClickListener {
            if (isRecording()) {
                model.addCallNotHookedEvent()
                setFakeCallStatusButtonDisplay()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeFakeSendCall.setOnClickListener {
            if (isRecording()) {
                model.addCallSentEvent()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeFakeReceiveSMS.setOnClickListener {
            if (isRecording()) {
                model.addSMSReceivedEvent()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }
    }

    private fun setFakeScreenOnOffButtonDisplay() {
        val lastOnOffFakeEvent = model.getLastOnOffFakeEvent()
        buttonHomeFakeScreenOnOffToggle.text = when (lastOnOffFakeEvent?.type) {
            TripEvent.Type.SCREEN_ON.name -> getString(R.string.fake_events_fake_screen_state_toggle_on)
            else -> getString(R.string.fake_events_fake_screen_state_toggle_off)
        }
    }

    private fun setFakeScreenUnlockLockButtonDisplay() {
        val lastUnlockLockFakeEvent = model.getLastUnlockLockFakeEvent()
        buttonHomeFakeScreenUnlockLockToggle.text = when (lastUnlockLockFakeEvent?.type) {
            TripEvent.Type.SCREEN_UNLOCK.name -> getString(R.string.fake_events_fake_screen_toogle_unlock_lock_unlock)
            else -> getString(R.string.fake_events_fake_screen_toogle_unlock_lock_lock)
        }
    }

    private fun setFakeCallStatusButtonDisplay() {
        val lastCallFakeEvent = model.getLastCallFakeEvent()
        when (lastCallFakeEvent?.type) {
            TripEvent.Type.RECEIVE_CALL.name -> {
                buttonHomeFakeReceiveCall.hideWithAnimationCompat(150)
                buttonHomeFakeHookCall.showWithAnimationCompat(150)
                buttonHomeFakeNotHookCall.showWithAnimationCompat(150)
            }
            else -> {
                buttonHomeFakeHookCall.hideWithAnimationCompat(150)
                buttonHomeFakeNotHookCall.hideWithAnimationCompat(150)
                buttonHomeFakeReceiveCall.showWithAnimationCompat(150)
            }
        }
    }

    private fun isRecording(): Boolean =
        model.isRecording()
}
