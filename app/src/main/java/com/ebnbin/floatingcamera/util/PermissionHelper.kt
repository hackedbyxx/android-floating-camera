package com.ebnbin.floatingcamera.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 * 权限帮助类.
 */
object PermissionHelper {
    @RequiresApi(Build.VERSION_CODES.M)
    fun isPermissionsGranted(vararg permissions: String) = permissions.none {
        when (it) {
            Manifest.permission.SYSTEM_ALERT_WINDOW -> !Settings.canDrawOverlays(app)
            else -> ContextCompat.checkSelfPermission(app, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isPermissionsDenied(vararg permissions: String) = !isPermissionsGranted(*permissions)
}
