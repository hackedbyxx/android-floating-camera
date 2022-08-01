package com.xx.floatingcamera

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.xx.floatingcamera.fragment.camera.CameraFragment
import com.xx.floatingcamera.util.*
import com.xx.floatingcamera.util.extension.get
import com.xx.floatingcamera.util.extension.put

/**
 * 应用 [Application] 类.
 */
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        singleton = this

//        val crashlytics = Crashlytics.Builder()
//                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
//                .build()
//        val answers = Answers()
//        Fabric.with(this, crashlytics, answers)

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
