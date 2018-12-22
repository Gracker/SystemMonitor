package com.android.monitor.view

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.android.monitor.R
import com.android.monitor.service.FloatingViewService
import com.android.monitor.util.FPSUtils

class FloatingView : FrameLayout {
    companion object {
        var FPS_INTERVAL = 1000L
    }
    private val TAG = "FloatingView"
    private val MSG_GET_FPS = 0
    private val MES_STOP = 1
    private lateinit var mFpsText: TextView
    private lateinit var mainHandler: Handler
    private lateinit var mContext: Context
    private lateinit var mView: View
    private var mTouchStartX: Int = 0
    private var mTouchStartY:Int = 0
    private var mHasMoved:Boolean = false

    private lateinit var mParams: WindowManager.LayoutParams
    private lateinit var mWindowManager: FloatingManager
    private lateinit var mFpsUtils: FPSUtils
    private lateinit var handlerThread: HandlerThread
    private lateinit var workHandler: Handler
    private var working = false
    private val mOnTouchListener = OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchStartX = mParams.x - event.rawX.toInt()
                mTouchStartY = mParams.y - event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                mParams.x = event.rawX.toInt() + mTouchStartX
                mParams.y = event.rawY.toInt() + mTouchStartY
                mWindowManager.updateView(mView, mParams)
                mHasMoved = true
            }
            MotionEvent.ACTION_UP -> {
                if (mHasMoved) {
                    mHasMoved = false
                } else {
                    //TODO update front color.

                }
            }
        }
        true
    }

     constructor(context: Context):super(context) {
        mContext = context.applicationContext
        val mLayoutInflater = LayoutInflater.from(context)
        mView = mLayoutInflater.inflate(R.layout.floating_view, null)
        mWindowManager = FloatingManager.getInstance(mContext)
        mFpsText = mView.findViewById(R.id.fps_text) as TextView
        mFpsUtils = FPSUtils()

        handlerThread = HandlerThread("getFPS")
        handlerThread.start()
        mainHandler = Handler()


        workHandler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_GET_FPS -> {
                        working = true
                        while (working) {
                            try {
                                Thread.sleep(FPS_INTERVAL)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            //mFpsUtils.updateContent()
                            mainHandler.post { mFpsText.text = String.format("fps = %s", mFpsUtils.getmFps()) }
                        }
                    }
                    MES_STOP -> working = false
                }
            }
        }

         mView.setOnTouchListener(mOnTouchListener)
    }

    fun update(type:String, action:String) {
        if (type.equals(FloatingViewService.FPS)) {
            if (action.equals(FloatingViewService.SHOW)) {
                mFpsText.visibility = View.VISIBLE
            } else {
                mFpsText.visibility = View.GONE
            }
            show()
        }
    }

    fun show() {
        mParams = WindowManager.LayoutParams()
        mParams.gravity = Gravity.TOP or Gravity.LEFT
        mParams.x = 0
        mParams.y = 100
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }

        mParams.format = PixelFormat.TRANSPARENT
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        mParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        mParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        mWindowManager.addView(mView, mParams)

        val msg = Message.obtain()
        msg.what = MSG_GET_FPS
        workHandler.sendMessage(msg)
    }

    fun hide() {
        val msg = Message.obtain()
        msg.what = MES_STOP
        workHandler.sendMessage(msg)

        mWindowManager.removeView(mView)
    }

}