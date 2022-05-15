package com.ebnbin.floatingcamera.preference

import android.content.Context
import androidx.preference.Preference
import com.ebnbin.floatingcamera.R

/**
 * 底部偏好.
 */
class FooterPreference(context: Context) : Preference(context) {
    init {
        isEnabled = false
        layoutResource = R.layout.footer_preference
    }
}
