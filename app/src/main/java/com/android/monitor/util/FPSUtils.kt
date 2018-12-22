package com.android.monitor.util

import android.os.IBinder
import android.os.Parcel


class FPSUtils {
    companion object {
        fun canUseSurfaceFlinger():Boolean {

            return true
        }
    }

    private lateinit var mFlinger : IBinder
    private var mLastPageFlipCount: Int = 0
    private var mLastUpdateTime: Long = 0
    private var mFps = 0f

    constructor() {
        mFlinger = LocalServiceManager.getService("SurfaceFlinger") as IBinder
    }

    fun getmFps(): Float {
        updateContent()
        return mFps
    }

    fun updateContent(): Boolean {
        try {
            if (mFlinger != null) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                data.writeInterfaceToken("android.ui.ISurfaceComposer")
                mFlinger.transact(1013, data, reply, 0)
                val pageFlipCount = reply.readInt()

                val now = System.nanoTime()
                val frames = pageFlipCount - mLastPageFlipCount
                val duration = now - mLastUpdateTime
                mFps = (frames * 1e9 / duration).toFloat()
                mLastPageFlipCount = pageFlipCount
                mLastUpdateTime = now
                reply.recycle()
                data.recycle()
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }
}