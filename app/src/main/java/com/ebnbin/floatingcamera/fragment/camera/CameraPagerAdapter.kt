package com.xx.floatingcamera.fragment.camera

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xx.floatingcamera.R
import com.xx.floatingcamera.fragment.preference.CameraPreferenceFragment
import com.xx.floatingcamera.fragment.preference.OtherPreferenceFragment
import com.xx.floatingcamera.fragment.preference.WindowPreferenceFragment
import com.xx.floatingcamera.util.res

class CameraPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) = ITEMS[position].createFragment()

    override fun getCount() = ITEMS.size

    override fun getPageTitle(position: Int): String = res.getString(ITEMS[position].titleStringRes)

    companion object {
        private val ITEMS = arrayOf(
                Item({ WindowPreferenceFragment() }, R.string.window_title),
                Item({ CameraPreferenceFragment() }, R.string.camera_title),
                Item({ OtherPreferenceFragment() }, R.string.other_title))

        private class Item(val createFragment: () -> Fragment, @StringRes val titleStringRes: Int)
    }
}
