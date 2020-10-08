package fr.openium.testdrivingdistraction.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.jakewharton.rxbinding3.widget.checkedChanges
import fr.openium.kotlintools.ext.appCompatActivity
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.show
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.enum.SensorRate
import fr.openium.testdrivingdistraction.utils.PreferencesUtils
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FragmentSettings : AbstractFragment(R.layout.fragment_settings) {

    private val model: ViewModelSettings by viewModel()
    private val preferencesUtils: PreferencesUtils by inject()

    // --- Life cycle
    // ---------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        setDisplay()
    }

    private fun setDisplay() {
        if (isRecording()) {
            textViewSettingsWarningRecording.show()

            spinnerSettingsAccelerometerRate.isEnabled = false
            spinnerSettingsGyroscopeRate.isEnabled = false

            switchEnabledAccelerometerRecording.isEnabled = false
            switchEnabledGyroscopeRecording.isEnabled = false
            switchEnabledLocationRecording.isEnabled = false
            switchEnabledEventsRecording.isEnabled = false
        } else {
            textViewSettingsWarningRecording.gone()

            spinnerSettingsAccelerometerRate.isEnabled = true
            spinnerSettingsGyroscopeRate.isEnabled = true

            switchEnabledAccelerometerRecording.isEnabled = true
            switchEnabledGyroscopeRecording.isEnabled = true
            switchEnabledLocationRecording.isEnabled = true
            switchEnabledEventsRecording.isEnabled = true
        }
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarSettings as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setListeners() {
        setSpinnerAccelerometer()
        setSpinnerGyroscope()
        setSwitchAccelerometer()
        setSwitchGyroscope()
        setSwitchLocation()
        setSwitchEvents()
    }

    private fun setSpinnerAccelerometer() {
        val adapterAccelerometer = object : ArrayAdapter<String>(
            requireContext(), R.layout.spinner_item_sort, getSpinnerItemList()
        ) {}

        adapterAccelerometer.setDropDownViewResource(R.layout.spinner_item_dropdown_sort)

        spinnerSettingsAccelerometerRate.adapter = adapterAccelerometer
        spinnerSettingsAccelerometerRate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    preferencesUtils.sensorAccelerometerRate = position
                }
            }

        spinnerSettingsAccelerometerRate.setSelection(preferencesUtils.sensorAccelerometerRate)
    }

    private fun setSpinnerGyroscope() {
        val adapterGyroscope = object : ArrayAdapter<String>(
            requireContext(), R.layout.spinner_item_sort, getSpinnerItemList()
        ) {}

        adapterGyroscope.setDropDownViewResource(R.layout.spinner_item_dropdown_sort)

        spinnerSettingsGyroscopeRate.adapter = adapterGyroscope
        spinnerSettingsGyroscopeRate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    preferencesUtils.sensorGyroscopeRate = position
                }
            }

        spinnerSettingsGyroscopeRate.setSelection(preferencesUtils.sensorGyroscopeRate)
    }

    fun getSpinnerItemList(): List<String> =
        SensorRate.values().map {
            "${it.name.toLowerCase().capitalize()} ${getString(it.rateTextId)}"
        }

    private fun setSwitchAccelerometer() {
        switchEnabledAccelerometerRecording.isChecked = preferencesUtils.accelerometerRecordEnabled
        switchEnabledAccelerometerRecording.checkedChanges()
            .subscribe({
                preferencesUtils.accelerometerRecordEnabled = it
            }, {
                Log.e(TAG, "Error checking accelerometer switch")
            }).addTo(disposables)
    }

    private fun setSwitchGyroscope() {
        switchEnabledGyroscopeRecording.isChecked = preferencesUtils.gyroscopeRecordEnabled
        switchEnabledGyroscopeRecording.checkedChanges()
            .subscribe({
                preferencesUtils.gyroscopeRecordEnabled = it
            }, {
                Log.e(TAG, "Error checking gyroscope switch")
            }).addTo(disposables)
    }

    private fun setSwitchLocation() {
        switchEnabledLocationRecording.isChecked = preferencesUtils.locationRecordEnabled
        switchEnabledLocationRecording.checkedChanges()
            .subscribe({
                preferencesUtils.locationRecordEnabled = it
            }, {
                Log.e(TAG, "Error checking location switch")
            }).addTo(disposables)
    }

    private fun setSwitchEvents() {
        switchEnabledEventsRecording.isChecked = preferencesUtils.eventsRecordEnabled
        switchEnabledEventsRecording.checkedChanges()
            .subscribe({
                preferencesUtils.eventsRecordEnabled = it
            }, {
                Log.e(TAG, "Error checking events switch")
            }).addTo(disposables)
    }

    private fun isRecording() =
        model.isRecording()

    // --- Other Methods
    // ---------------------------------------------------

    companion object {
        private const val TAG = "FragmentSettings"
    }
}
