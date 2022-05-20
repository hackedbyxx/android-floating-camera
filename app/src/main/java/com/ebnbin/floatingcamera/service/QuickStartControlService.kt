package com.ebnbin.floatingcamera.service

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
        if (!CameraService.isRunning) {
            state = Tile.STATE_INACTIVE
            refresh()
        }
    }
    var state: Int = Tile.STATE_INACTIVE
    fun refresh() {
        qsTile.state = state
        qsTile.updateTile()
    }

    override fun onClick() {
//        Log.i(TAG, "onClick")
        if (CameraService.isRunning) {
            state = Tile.STATE_INACTIVE
            CameraService.postStop()
        } else {
            state = Tile.STATE_ACTIVE
            CameraService.start()
        }
        refresh()
    }

    companion object {
        private const val TAG = "QsControlService"
    }
}