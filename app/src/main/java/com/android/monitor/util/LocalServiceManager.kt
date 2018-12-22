package com.android.monitor.util

import android.annotation.SuppressLint
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class LocalServiceManager {
    companion object {
        @SuppressLint("PrivateApi")
        fun getService(servicename: String): Any? {
            val `object` = Any()
            var getService: Method? = null
            try {
                getService = Class.forName("android.os.ServiceManager").getMethod("getService", String::class.java)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            var obj: Any? = null
            try {
                obj = getService!!.invoke(`object`, servicename)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

            return obj
        }
    }
}