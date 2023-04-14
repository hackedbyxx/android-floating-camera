package com.xx.floatingcamera.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi

/**
 * 快捷开关启动
 */
@RequiresApi(api = Build.VERSION_CODES.N)
class QuickStartControlService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        if (CameraService.isRunning) {
            refresh(Tile.STATE_ACTIVE)
        } else {
            refresh(Tile.STATE_INACTIVE)
        }
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    var state: Int = Tile.STATE_INACTIVE

    fun refresh(state:Int) {
        qsTile.state = state
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick();
//        Log.i(TAG, "onClick")
        if (CameraService.isRunning) {
            refresh(Tile.STATE_INACTIVE)
            CameraService.postStop()
        } else {
            refresh(Tile.STATE_ACTIVE)
            CameraService.start()
        }
    }

    companion object {
        private const val TAG = "QsControlService"
    }
}