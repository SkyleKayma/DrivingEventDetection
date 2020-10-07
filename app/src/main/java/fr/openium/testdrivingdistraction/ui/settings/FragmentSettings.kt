package fr.openium.testdrivingdistraction.ui.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.tbruyelle.rxpermissions2.RxPermissions
import fr.openium.kotlintools.ext.*
import fr.openium.testdrivingdistraction.R
import fr.openium.testdrivingdistraction.base.fragment.AbstractFragment
import fr.openium.testdrivingdistraction.utils.PermissionsUtils
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject


class FragmentSettings : AbstractFragment(R.layout.fragment_settings) {

    private val permissionsUtils by inject<PermissionsUtils>()

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

    // --- Methods
    // ---------------------------------------------------

    private fun setToolbar() {
        val toolbar = toolbarSettings as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setDisplay() {
        if (permissionsUtils.isReadCallLogPermissionGranted()) {
            buttonSettingsPermissionsAskReadCallLog.hide()
            imageViewSettingsPermissionsReadCallLog.show()
        } else {
            imageViewSettingsPermissionsReadCallLog.hide()
            buttonSettingsPermissionsAskReadCallLog.show()
        }

        if (permissionsUtils.isReadPhoneStatePermissionGranted()) {
            buttonSettingsPermissionsAskReadPhoneState.hide()
            imageViewSettingsPermissionsReadPhoneState.show()
        } else {
            imageViewSettingsPermissionsReadPhoneState.hide()
            buttonSettingsPermissionsAskReadPhoneState.show()
        }

        if (permissionsUtils.isReceiveSMSPermissionGranted()) {
            buttonSettingsPermissionsAskReceiveSMS.hide()
            imageViewSettingsPermissionsReceiveSMS.show()
        } else {
            imageViewSettingsPermissionsReceiveSMS.hide()
            buttonSettingsPermissionsAskReceiveSMS.show()
        }

        if (permissionsUtils.isLocationPermissionGranted() && permissionsUtils.isBackgroundLocationPermissionGranted()) {
            buttonSettingsPermissionsAskLocation.hide()
            imageViewSettingsPermissionsLocation.show()
        } else {
            imageViewSettingsPermissionsLocation.hide()
            buttonSettingsPermissionsAskLocation.show()
        }
    }

    private fun setListeners() {
        buttonSettingsPermissionsAskReadCallLog.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.READ_CALL_LOG)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            buttonSettingsPermissionsAskReadCallLog.hideWithAnimationCompat()
                            imageViewSettingsPermissionsReadCallLog.showWithAnimationCompat()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToSettings()
                        }
                    }
                }.addTo(disposables)
        }

        buttonSettingsPermissionsAskReadPhoneState.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.READ_PHONE_STATE)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            buttonSettingsPermissionsAskReadPhoneState.hideWithAnimationCompat()
                            imageViewSettingsPermissionsReadPhoneState.showWithAnimationCompat()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToSettings()
                        }
                    }
                }.addTo(disposables)
        }

        buttonSettingsPermissionsAskReceiveSMS.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.RECEIVE_SMS)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            buttonSettingsPermissionsAskReceiveSMS.hideWithAnimationCompat()
                            imageViewSettingsPermissionsReceiveSMS.showWithAnimationCompat()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToSettings()
                        }
                    }
                }.addTo(disposables)
        }

        buttonSettingsPermissionsAskLocation.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            if (permissionsUtils.isBackgroundLocationPermissionGranted()) {
                                buttonSettingsPermissionsAskLocation.hideWithAnimationCompat()
                                imageViewSettingsPermissionsLocation.showWithAnimationCompat()
                            } else askForBackgroundLocationPermission()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToSettings()
                        }
                    }
                }.addTo(disposables)
        }
    }

    private fun askForBackgroundLocationPermission() {
        RxPermissions(this)
            .requestEach(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            .subscribe { permission ->
                when {
                    permission.granted -> {
                        buttonSettingsPermissionsAskLocation.hideWithAnimationCompat()
                        imageViewSettingsPermissionsLocation.showWithAnimationCompat()
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // Nothing special to do, permission will be asked again
                    }
                    else -> {
                        goToSettings()
                    }
                }
            }.addTo(disposables)
    }

    private fun goToSettings() {
        requireActivity().startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + requireActivity().packageName)
            ).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }
}
