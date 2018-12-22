package com.android.monitor.view

import android.content.Context
import android.view.View
import android.view.WindowManager

internal class FloatingManager private constructor(mContext: Context) {
    private val mWindowManager: WindowManager

    init {
        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun addView(view: View, params: WindowManager.LayoutParams): Boolean {
        try {
            mWindowManager.addView(view, params)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun removeView(view: View): Boolean {
        try {
            mWindowManager.removeView(view)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun updateView(view: View, params: WindowManager.LayoutParams): Boolean {
        try {
            mWindowManager.updateViewLayout(view, params)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    companion object {

        @Volatile
        var instance: FloatingManager? = null

        fun getInstance(context: Context): FloatingManager {
            if (instance == null) {
                synchronized(FloatingManager::class) {
                    if (instance == null) {
                        instance = FloatingManager(context)
                    }
                }
            }
            return instance!!
        }
    }
}