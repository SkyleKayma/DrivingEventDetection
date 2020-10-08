package fr.openium.testdrivingdistraction.ui.permissions

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
import kotlinx.android.synthetic.main.fragment_permissions.*
import org.koin.android.ext.android.inject


class FragmentPermissions : AbstractFragment(R.layout.fragment_permissions) {

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
        val toolbar = toolbarPermissions as Toolbar
        appCompatActivity.setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, findNavController())
    }

    private fun setDisplay() {
        if (permissionsUtils.isReadCallLogPermissionGranted()) {
            buttonPermissionsAskReadCallLog.hide()
            imageViewPermissionsReadCallLog.show()
        } else {
            imageViewPermissionsReadCallLog.hide()
            buttonPermissionsAskReadCallLog.show()
        }

        if (permissionsUtils.isReadPhoneStatePermissionGranted()) {
            buttonPermissionsAskReadPhoneState.hide()
            imageViewPermissionsReadPhoneState.show()
        } else {
            imageViewPermissionsReadPhoneState.hide()
            buttonPermissionsAskReadPhoneState.show()
        }

        if (permissionsUtils.isReceiveSMSPermissionGranted()) {
            buttonPermissionsAskReceiveSMS.hide()
            imageViewPermissionsReceiveSMS.show()
        } else {
            imageViewPermissionsReceiveSMS.hide()
            buttonPermissionsAskReceiveSMS.show()
        }

        if (permissionsUtils.isLocationPermissionGranted() && permissionsUtils.isBackgroundLocationPermissionGranted()) {
            buttonPermissionsAskLocation.hide()
            imageViewPermissionsLocation.show()
        } else {
            imageViewPermissionsLocation.hide()
            buttonPermissionsAskLocation.show()
        }
    }

    private fun setListeners() {
        buttonPermissionsAskReadCallLog.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.READ_CALL_LOG)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            buttonPermissionsAskReadCallLog.hideWithAnimationCompat()
                            imageViewPermissionsReadCallLog.showWithAnimationCompat()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToAppSettings()
                        }
                    }
                }.addTo(disposables)
        }

        buttonPermissionsAskReadPhoneState.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.READ_PHONE_STATE)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            buttonPermissionsAskReadPhoneState.hideWithAnimationCompat()
                            imageViewPermissionsReadPhoneState.showWithAnimationCompat()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToAppSettings()
                        }
                    }
                }.addTo(disposables)
        }

        buttonPermissionsAskReceiveSMS.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.RECEIVE_SMS)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            buttonPermissionsAskReceiveSMS.hideWithAnimationCompat()
                            imageViewPermissionsReceiveSMS.showWithAnimationCompat()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToAppSettings()
                        }
                    }
                }.addTo(disposables)
        }

        buttonPermissionsAskLocation.setOnClickListener {
            RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { permission ->
                    when {
                        permission.granted -> {
                            if (permissionsUtils.isBackgroundLocationPermissionGranted()) {
                                buttonPermissionsAskLocation.hideWithAnimationCompat()
                                imageViewPermissionsLocation.showWithAnimationCompat()
                            } else askForBackgroundLocationPermission()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // Nothing special to do, permission will be asked again
                        }
                        else -> {
                            goToAppSettings()
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
                        buttonPermissionsAskLocation.hideWithAnimationCompat()
                        imageViewPermissionsLocation.showWithAnimationCompat()
                    }
                    permission.shouldShowRequestPermissionRationale -> {
                        // Nothing special to do, permission will be asked again
                    }
                    else -> {
                        goToAppSettings()
                    }
                }
            }.addTo(disposables)
    }

    private fun goToAppSettings() {
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
