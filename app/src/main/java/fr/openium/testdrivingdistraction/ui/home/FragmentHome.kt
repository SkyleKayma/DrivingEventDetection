package fr.openium.testdrivingdistraction.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.kotlintools.ext.snackbar
import fr.openium.rxtools.ext.fromIOToMain
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.ext.getColorStateListFromColor
import fr.openium.testdrivingdistraction.ext.navigate
import fr.openium.testdrivingdistraction.service.SensorAndLocationTrackingService
import fr.openium.testdrivingdistraction.ui.home.dialog.DialogNoLocationPermission
import fr.openium.testdrivingdistraction.utils.PermissionsUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class FragmentHome : AbstractFragment(R.layout.fragment_home) {

    private var actualRecordState = RecordingState.STOPPED

    private val permissionsUtils by inject<PermissionsUtils>()
    private val model: ViewModelHome by viewModel()

    private var startServiceTimer: Disposable? = null
    private var stopServiceTimer: Disposable? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()

        // Get actual running state
        actualRecordState = if (isRecording()) {
            RecordingState.STARTED
        } else {
            model.deletePendingRecord()
            RecordingState.STOPPED
        }

        setDisplay()
        setListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_home_list -> {
                navigate(FragmentHomeDirections.actionFragmentHomeToFragmentDetailList())
                true
            }
            R.id.menu_home_settings -> {
                navigate(FragmentHomeDirections.actionFragmentHomeToFragmentSettings())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onResume() {
        super.onResume()
        setPermissionState()
    }

    // --- Display methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarHome as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setDisplay() {
        setStartStopRecordingButtonDisplay()
        setAddSomeFakeEventsButtonDisplay()
        setMarkerButtonsDisplay()
    }

    private fun setStartStopRecordingButtonDisplay() {
        when (actualRecordState) {
            RecordingState.STARTED -> {
                buttonHomeStartTrip.backgroundTintList = getColorStateListFromColor(R.color.colorRed)
                (buttonHomeStartTrip as MaterialButton).setIconResource(R.drawable.ic_stop)
                buttonHomeStartTrip.text = getString(R.string.home_trip_stop_recording)
            }
            RecordingState.STOPPED -> {
                buttonHomeStartTrip.backgroundTintList = getColorStateListFromColor(R.color.colorGreen)
                (buttonHomeStartTrip as MaterialButton).setIconResource(R.drawable.ic_play)
                buttonHomeStartTrip.text = getString(R.string.home_trip_start_recording)
            }
            RecordingState.STARTING -> {
                buttonHomeStartTrip.backgroundTintList = getColorStateListFromColor(R.color.colorPrimaryDark)
                (buttonHomeStartTrip as MaterialButton).setIconResource(R.drawable.ic_loading)
                buttonHomeStartTrip.text = getString(R.string.home_trip_starting_recording)
            }
            RecordingState.STOPPING -> {
                buttonHomeStartTrip.backgroundTintList = getColorStateListFromColor(R.color.colorPrimaryDark)
                (buttonHomeStartTrip as MaterialButton).setIconResource(R.drawable.ic_loading)
                buttonHomeStartTrip.text = getString(R.string.home_trip_stopping_recording)
            }
        }
    }

    private fun setAddSomeFakeEventsButtonDisplay() {
        buttonHomeAddFakeEvents.backgroundTintList =
            if (isRecording()) getColorStateListFromColor(R.color.colorPrimaryDark) else getColorStateListFromColor(R.color.colorGrey)
    }

    private fun setMarkerButtonsDisplay() {
        if (isRecording()) {
            buttonHomeMarkerHangUp.backgroundTintList = getColorStateListFromColor(R.color.colorPrimaryDark)
            buttonHomeMarkerHandUsage.backgroundTintList = getColorStateListFromColor(R.color.colorPrimaryDark)
            buttonHomeMarkerPickUp.backgroundTintList = getColorStateListFromColor(R.color.colorPrimaryDark)
        } else {
            buttonHomeMarkerHangUp.backgroundTintList = getColorStateListFromColor(R.color.colorGrey)
            buttonHomeMarkerHandUsage.backgroundTintList = getColorStateListFromColor(R.color.colorGrey)
            buttonHomeMarkerPickUp.backgroundTintList = getColorStateListFromColor(R.color.colorGrey)
        }
    }

    private fun setPermissionState() {
        if (permissionsUtils.areAllPermissionsGranted()) {
            textViewHomePermissions.text = getString(R.string.home_permissions_granted)
            textViewHomePermissions.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check, 0, R.drawable.ic_arrow_right, 0)
            TextViewCompat.setCompoundDrawableTintList(textViewHomePermissions, getColorStateListFromColor(R.color.colorGreen))
        } else {
            textViewHomePermissions.text = getString(R.string.home_permissions_missing)
            textViewHomePermissions.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_warning, 0, R.drawable.ic_arrow_right, 0)
            TextViewCompat.setCompoundDrawableTintList(textViewHomePermissions, getColorStateListFromColor(R.color.colorOrange))
        }
    }

    // --- Job methods
    // ---------------------------------------------------

    private fun setListeners() {
        buttonHomeStartTrip.setOnClickListener {
            if ((!permissionsUtils.isLocationPermissionGranted() || !permissionsUtils.isBackgroundLocationPermissionGranted()) && !isRecording()) {
                DialogNoLocationPermission().apply {
                    setListener(object : DialogNoLocationPermission.OnPositiveButtonClickedListener {
                        override fun onPositiveButtonClicked() {
                            changeRecordState()
                        }
                    })
                }.show(childFragmentManager, "DialogNoLocationPermission")
            } else {
                changeRecordState()
            }
        }

        textViewHomePermissions.setOnClickListener {
            navigate(FragmentHomeDirections.actionFragmentHomeToFragmentPermissions())
        }

        buttonHomeAddFakeEvents.setOnClickListener {
            if (isRecording()) {
                navigate(FragmentHomeDirections.actionFragmentHomeToFragmentFakeEvents())
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeMarkerHangUp.setOnClickListener {
            if (isRecording()) {
                model.addSensorHangUpEvent()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeMarkerHandUsage.setOnClickListener {
            if (isRecording()) {
                model.addSensorHangUsageEvent()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }

        buttonHomeMarkerPickUp.setOnClickListener {
            if (isRecording()) {
                model.addSensorPickUpEvent()
            } else snackbar(getString(R.string.generic_error_start_record_first), Snackbar.LENGTH_SHORT)
        }
    }

    private fun changeRecordState() {
        when (actualRecordState) {
            RecordingState.STARTED -> {
                actualRecordState = RecordingState.STOPPING

                setStopServiceTimer()

                stopService()
                setDisplay()
            }
            RecordingState.STARTING -> {
                snackbar(getString(R.string.home_error_service_starting), Snackbar.LENGTH_SHORT)
            }
            RecordingState.STOPPING -> {
                snackbar(getString(R.string.home_error_service_stopping), Snackbar.LENGTH_SHORT)
            }
            RecordingState.STOPPED -> {
                actualRecordState = RecordingState.STARTING

                startService()
                setDisplay()

                setStartServiceTimer()
            }
        }
    }

    private fun setStartServiceTimer() {
        startServiceTimer?.dispose()
        startServiceTimer = null

        startServiceTimer = Observable.timer(100, TimeUnit.MILLISECONDS)
            .fromIOToMain()
            .subscribe({
                if (isRecording()) {
                    if (canTrackData()) {
                        actualRecordState = RecordingState.STARTED
                        setDisplay()
                    } else setStartServiceTimer()
                } else setStartServiceTimer()
            }, { Log.e(TAG, "Error waiting for service to start $it") }).addTo(disposables)
    }

    private fun setStopServiceTimer() {
        stopServiceTimer?.dispose()
        stopServiceTimer = null

        stopServiceTimer = Observable.timer(100, TimeUnit.MILLISECONDS)
            .fromIOToMain()
            .subscribe({
                if (!isRecording()) {
                    actualRecordState = RecordingState.STOPPED
                    setDisplay()
                } else setStopServiceTimer()
            }, { Log.e(TAG, "Error waiting for service to stop $it") }).addTo(disposables)
    }

    private fun isRecording(): Boolean =
        model.isRecording() && SensorAndLocationTrackingService.isRunning

    // --- Service related methods
    // ---------------------------------------------------

    private fun startService() {
        val serviceIntent = Intent(requireContext(), SensorAndLocationTrackingService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(requireContext(), SensorAndLocationTrackingService::class.java)
        activity?.stopService(serviceIntent)
    }

    private fun canTrackData(): Boolean =
        if (permissionsUtils.isLocationPermissionGranted() && permissionsUtils.isBackgroundLocationPermissionGranted()) {
            model.hasRecordedSomeLocations()
        } else true

    // --- Other methods
    // ---------------------------------------------------

    enum class RecordingState {
        STARTING,
        STARTED,
        STOPPING,
        STOPPED
    }

    companion object {
        private const val TAG = "FragmentHome"
    }
}
