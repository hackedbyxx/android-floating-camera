package com.xx.floatingcamera.preference

import android.content.Context
import android.support.v7.preference.Preference
import com.xx.floatingcamera.R

/**
 * 底部偏好.
 */
class FooterPreference(context: Context) : Preference(context) {
    init {
        isEnabled = false
        layoutResource = R.layout.footer_preference
    }
}
