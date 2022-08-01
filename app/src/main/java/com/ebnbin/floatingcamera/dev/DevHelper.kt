//package com.xx.floatingcamera.dev
//
//import com.crashlytics.android.answers.CustomEvent
//import com.xx.floatingcamera.util.answers
//
//object DevHelper {
//    fun event(name: String, attributes: Map<String, *>? = null) {
//        val event = CustomEvent(name)
//        attributes?.forEach {
//            event.putCustomAttribute(it.key, it.value.toString())
//        }
//        answers.logCustom(event)
//    }
//}
