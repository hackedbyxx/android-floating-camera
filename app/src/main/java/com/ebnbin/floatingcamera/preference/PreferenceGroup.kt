package com.xx.floatingcamera.preference

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceManager
import com.xx.floatingcamera.R
import com.xx.floatingcamera.util.extension.readBoolean
import com.xx.floatingcamera.util.extension.writeBoolean

/**
 * [androidx.preference.PreferenceGroup].
 *
 * 用于管理 [Preference], 没有可显示的内容, 可以嵌套添加.
 *
 * [PreferenceCategory] 作为普通 [Preference] 使用, 不用于添加子 [Preference].
 */
open class PreferenceGroup(context: Context) : androidx.preference.PreferenceGroup(context, null) {
    init {
        // androidx.preference 1.1+ 的 PreferenceGroupAdapter 对不可见的 group 不再递归其子项
        // (support 27.1.1 会递归所有 group, 仅跳过 group 自身的行), 保持框架可见并
        // 使用零高度布局隐藏 group 自身的行, 以维持原有显示效果.
        isVisible = true
        layoutResource = R.layout.preference_group_transparent
    }

    private var initPreferences: Array<out Preference?>? = null
    private var initIsGroupVisible: (() -> Boolean)? = null

    fun initPreferences(vararg initPreferences: Preference?) {
        this.initPreferences = initPreferences
    }

    fun initIsGroupVisible(initIsGroupVisible: () -> Boolean) {
        this.initIsGroupVisible = initIsGroupVisible
    }

    /**
     * 保存所有子 [Preference], 用于恢复可见性.
     */
    private val preferences = arrayListOf<Preference>()

    /**
     * 通过添加和移除的方式设置子 [Preference] 的可见性.
     */
    var isGroupVisible = DEF_IS_GROUP_VISIBLE
        set(value) {
            if (field == value) return
            field = value

            if (field) {
                preferences.forEach { super.addPreference(it) }
                preferences.clear()
            } else {
                for (index in 0 until preferenceCount) {
                    preferences.add(getPreference(index))
                }
                super.removeAll()
            }
        }

    private var isFirstAttachedToHierarchy = true

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager?) {
        super.onAttachedToHierarchy(preferenceManager)

        if (!isFirstAttachedToHierarchy) return

        isFirstAttachedToHierarchy = false

        initPreferences?.forEach {
            if (it != null) addPreferenceToGroup(it)
        }
        if (initIsGroupVisible != null) isGroupVisible = initIsGroupVisible!!()
    }

    //*****************************************************************************************************************

    fun addPreferenceToGroup(preference: Preference): Boolean {
        return if (isGroupVisible) {
            super.addPreference(preference)
        } else {
            preferences.add(preference)
        }
    }

    fun removePreferenceFromGroup(preference: Preference): Boolean {
        return if (isGroupVisible) {
            super.removePreference(preference)
        } else {
            preferences.remove(preference)
        }
    }

    fun removeAllFromGroup() {
        if (isGroupVisible) {
            super.removeAll()
        } else {
            preferences.clear()
        }
    }

    @Deprecated("使用 addPreferenceToGroup 代替.", ReplaceWith("addPreferenceToGroup"))
    override fun addPreference(preference: Preference?) = super.addPreference(preference)

    @Deprecated("使用 removePreferenceFromGroup 代替.", ReplaceWith("removePreferenceFromGroup"))
    override fun removePreference(preference: Preference?) = super.removePreference(preference)

    @Deprecated("使用 removeAllFromGroup 代替.", ReplaceWith("removeAllFromGroup"))
    override fun removeAll() = super.removeAll()

    //*****************************************************************************************************************
    // Instance state.

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) return super.onRestoreInstanceState(state)

        super.onRestoreInstanceState(state.superState)
        isGroupVisible = state.isGroupVisible
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) return superState

        return SavedState(superState, isGroupVisible)
    }

    private class SavedState : BaseSavedState {
        val isGroupVisible: Boolean

        constructor(source: Parcel?) : super(source) {
            isGroupVisible = source?.readBoolean() ?: DEF_IS_GROUP_VISIBLE
        }
        constructor(superState: Parcelable?, isGroupVisible: Boolean) : super(superState) {
            this.isGroupVisible = isGroupVisible
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)

            parcel.writeBoolean(isGroupVisible)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)

            override fun newArray(size: Int) = arrayOfNulls<SavedState>(size)
        }
    }

    //*****************************************************************************************************************

    companion object {
        /**
         * [isGroupVisible] 默认值.
         */
        private const val DEF_IS_GROUP_VISIBLE = true
    }
}
