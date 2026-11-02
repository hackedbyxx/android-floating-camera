package com.xx.floatingcamera.fragment.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.xx.floatingcamera.R
import com.xx.floatingcamera.fragment.camera.CameraPagerAdapter
import com.xx.floatingcamera.fragment.permission.PermissionFragment
import com.xx.floatingcamera.fragment.preference.CameraPreferenceFragment
import com.xx.floatingcamera.service.CameraService
import com.xx.floatingcamera.util.LocalBroadcastHelper
import com.xx.floatingcamera.util.PreferenceHelper
import com.xx.floatingcamera.util.extension.get
import com.xx.floatingcamera.util.extension.put
import com.xx.floatingcamera.util.getColorAttr
import com.xx.floatingcamera.util.sp
import kotlinx.android.synthetic.main.main_fragment.*

/**
 * 首页.
 */
class MainFragment :
        Fragment(),
        ViewPager.OnPageChangeListener,
        AdapterView.OnItemSelectedListener,
        PermissionFragment.Callback,
        LocalBroadcastHelper.Receiver,
        SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onReceive(context: Context, intent: Intent, action: String) {
        when (action) {
            CameraService.ACTION_CAMERA_SERVICE_IS_RUNNING -> invalidateCameraServiceIsRunning()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastHelper.register(this, CameraService.ACTION_CAMERA_SERVICE_IS_RUNNING)

        sp.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        tabLayout.setupWithViewPager(viewPager)

        val adapter = CameraPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(this)
        viewPager.offscreenPageLimit = adapter.count
        viewPager.setPageMarginDrawable(ColorDrawable(getColorAttr(context, R.attr.colorPlaceholder)))

        cameraFab.setOnClickListener {
            cameraFab.isEnabled = false

            val recordAudioPermission = if (PreferenceHelper.isPhoto())
                arrayOf() else
                arrayOf(Manifest.permission.RECORD_AUDIO)
            PermissionFragment.request(childFragmentManager, REQUEST_CODE_PERMISSION,
                    Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.CAMERA, *recordAudioPermission,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        profileSpinner.onItemSelectedListener = this
        invalidateProfileSpinner()

        if (savedInstanceState != null) return

        viewPager.currentItem = sp.get(KEY_PAGE, DEF_VALUE_PAGE)
    }

    override fun onPermissionsResult(requestCode: Int, granted: Boolean) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                cameraFab.isEnabled = true

                if (!granted) return

                if (CameraService.isRunning)
                    CameraService.postStop() else
                    CameraService.start()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        invalidateCameraServiceIsRunning()
        invalidateProfileSpinner()
    }

    private fun invalidateCameraServiceIsRunning() {
        cameraFab.isEnabled = true
        cameraFab.setImageResource(if (CameraService.isRunning) R.drawable.stop else R.drawable.camera)
    }

    //*****************************************************************************************************************
    // 视频配置选择器.

    private var updatingProfileSpinner = false

    private fun videoProfileKey() = if (CameraPreferenceFragment.isFront)
        CameraPreferenceFragment.KEY_FRONT_VIDEO_PROFILE else
        CameraPreferenceFragment.KEY_BACK_VIDEO_PROFILE

    /**
     * 依据当前摄像头和视频/照片模式更新视频配置选择器.
     */
    private fun invalidateProfileSpinner() {
        val context = context ?: return

        updatingProfileSpinner = true

        if (PreferenceHelper.isPhoto()) {
            profileSpinner.visibility = View.INVISIBLE
        } else {
            profileSpinner.visibility = View.VISIBLE

            val device = PreferenceHelper.device()
            val summaries = device.videoProfileSummaries
            profileSpinner.adapter = ArrayAdapter(context,
                    android.R.layout.simple_spinner_dropdown_item, summaries)

            val index = sp.get(videoProfileKey(), DEF_VALUE_VIDEO_PROFILE).toIntOrNull() ?: 0
            if (index in 0 until summaries.size) {
                profileSpinner.setSelection(index)
            }
        }

        updatingProfileSpinner = false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (updatingProfileSpinner) return

        sp.put(videoProfileKey(), position.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    //*****************************************************************************************************************

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            CameraPreferenceFragment.KEY_IS_FRONT,
            CameraPreferenceFragment.KEY_BACK_IS_PHOTO,
            CameraPreferenceFragment.KEY_FRONT_IS_PHOTO,
            CameraPreferenceFragment.KEY_BACK_VIDEO_PROFILE,
            CameraPreferenceFragment.KEY_FRONT_VIDEO_PROFILE -> invalidateProfileSpinner()
        }
    }

    //*****************************************************************************************************************

    override fun onDestroyView() {
        viewPager.removeOnPageChangeListener(this)

        super.onDestroyView()
    }

    override fun onDestroy() {
        sp.unregisterOnSharedPreferenceChangeListener(this)

        LocalBroadcastHelper.unregister(this)

        super.onDestroy()
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        sp.put(KEY_PAGE, position)
    }

    companion object {
        const val KEY_PAGE = "page"
        private const val DEF_VALUE_PAGE = 1

        private const val DEF_VALUE_VIDEO_PROFILE = "0"

        private const val REQUEST_CODE_PERMISSION = 0x1
    }
}
