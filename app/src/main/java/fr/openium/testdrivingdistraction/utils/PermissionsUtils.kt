package fr.openium.testdrivingdistraction.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class PermissionsUtils(private val context: Context) {

    private val permissionList = arrayListOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    fun isLocationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isBackgroundLocationPermissionGranted(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true

    fun isReadCallLogPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED

    fun isReadPhoneStatePermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

    fun isReceiveSMSPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED

    fun areAllPermissionsGranted(): Boolean {
        permissionList.forEach {
            if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }
}