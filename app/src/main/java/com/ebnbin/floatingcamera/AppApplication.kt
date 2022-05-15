package com.ebnbin.floatingcamera

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.ebnbin.floatingcamera.fragment.camera.CameraFragment
import com.ebnbin.floatingcamera.util.*
import com.ebnbin.floatingcamera.util.extension.get
import com.ebnbin.floatingcamera.util.extension.put

/**
 * 应用 [Application] 类.
 */
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        singleton = this

        if (sp.get(KEY_VERSION_CODE, 0) < BuildConfig.VERSION_CODE) {
            sp.put(KEY_VERSION_CODE, BuildConfig.VERSION_CODE)
            sp.put(CameraFragment.KEY_PAGE, null)
        }

        if (v26()) {
            notificationManager.createNotificationChannel(NotificationChannel("default", "Default",
                    NotificationManager.IMPORTANCE_DEFAULT))
        }

        PreferenceHelper
    }

    companion object {
        private var singleton: AppApplication? = null

        val instance get() = singleton ?: throw BaseRuntimeException()

        private const val KEY_VERSION_CODE = "version_code"
    }
}
