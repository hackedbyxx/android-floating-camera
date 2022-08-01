package com.xx.floatingcamera.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xx.floatingcamera.service.CameraService

class StopCameraServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        DevHelper.event("stop camera service broadcast")
        CameraService.stop()
    }
}
