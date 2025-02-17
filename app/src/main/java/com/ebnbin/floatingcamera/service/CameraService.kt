package com.ebnbin.floatingcamera.service

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ebnbin.floatingcamera.R
import com.ebnbin.floatingcamera.receiver.StopCameraServiceBroadcastReceiver
import com.ebnbin.floatingcamera.util.*
import com.ebnbin.floatingcamera.view.CameraLayout
import com.ebnbin.floatingcamera.widget.CameraAppWidgetProvider

/**
 * 相机服务.
 */
class CameraService : Service(), LocalBroadcastHelper.Receiver {
    private lateinit var cameraLayout: CameraLayout

    override fun onReceive(context: Context, intent: Intent, action: String) {
        when (action) {
            ACTION_POST_STOP -> cameraLayout.finish()
        }
    }

    override fun onCreate() {
        super.onCreate()

        LocalBroadcastHelper.register(this, ACTION_POST_STOP)

        isRunning = true
        LocalBroadcastHelper.send(ACTION_CAMERA_SERVICE_IS_RUNNING, Intent().putExtra(KEY_IS_RUNNING, isRunning))

        RotationHelper.registerAndEnable(this)

        notificationManager

        val pendingIntent = PendingIntent.getBroadcast(this, 0,
                Intent(this, StopCameraServiceBroadcastReceiver::class.java), 0)
        val notification = NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(res.getString(R.string.app_name))
                .setContentText(res.getString(R.string.stop_camera_service))
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)

        val recordAudioPermission = if (PreferenceHelper.isPhoto())
            arrayOf() else
            arrayOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionHelper.isPermissionsDenied(
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.CAMERA, *recordAudioPermission,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            stop()
            return
        }

        cameraLayout = CameraLayout(this)

        val params = WindowManager.LayoutParams()
        @Suppress("DEPRECATION")
        params.type = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_PHONE else
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        params.format = PixelFormat.TRANSLUCENT
        params.gravity = Gravity.START or Gravity.TOP

        windowManager.addView(cameraLayout, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fromWidget = intent?.getStringExtra(CameraAppWidgetProvider.KEY_FROM)?.equals("widget") == true
//        DevHelper.event("on start command", mapOf("from widget" to fromWidget))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (::cameraLayout.isInitialized) {
            windowManager.removeView(cameraLayout)
        }

        stopForeground(true)

        RotationHelper.unregister(this)

        isRunning = false
        LocalBroadcastHelper.send(ACTION_CAMERA_SERVICE_IS_RUNNING, Intent().putExtra(KEY_IS_RUNNING, isRunning))

        LocalBroadcastHelper.unregister(this)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    companion object {
        const val ACTION_CAMERA_SERVICE_IS_RUNNING = "camera_service_is_running"
        private const val ACTION_POST_STOP = "post_stop"
        const val KEY_IS_RUNNING = "is_running"

        var isRunning = false
            private set

        fun start(context: Context = app) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                context.startService(Intent(context, CameraService::class.java))
            } else {
                context.startForegroundService(Intent(context, CameraService::class.java))
            }
        }

        fun stop(context: Context = app) {
            context.stopService(Intent(context, CameraService::class.java))
        }

        fun postStop() {
            LocalBroadcastHelper.send(ACTION_POST_STOP)
        }
    }
}
