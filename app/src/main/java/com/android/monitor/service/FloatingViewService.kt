package com.android.monitor.service

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log
import com.android.monitor.view.FloatingView

class FloatingViewService : Service() {
    companion object {
        public final val ACTION = "action"
        public final val SHOW = "show"
        public final val HIDE = "hide"

        public final val TYPE = "type"
        public final val FPS = "fps"
        public final val CPU = "cpu"
    }
    private lateinit var mFloatingView:FloatingView

    override fun onCreate() {
        super.onCreate()
        mFloatingView = FloatingView(applicationContext)
        Log.d("joseph","onStartCommand:init")
    }

    override fun onBind(intent:Intent):IBinder? {
        return null
    }

    /*
    override fun onStartCommand(intent:Intent ,flags:Int, startId:Int):Int {
        if (intent != null) {
            val action = intent.action
            if (SHOW.equals(action)) {
                mFloatingView.show()
                Log.d("joseph","onStartCommand:Show")
            } else if (HIDE.equals(action)) {
                mFloatingView.hide()
            }
        }
        Log.d("joseph","onStartCommand:Show"+intent)
        return super.onStartCommand(intent, flags, startId)
    }*/


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val type = intent.getStringExtra(TYPE) as String
            val action = intent.getStringExtra(ACTION) as String
            mFloatingView.update(type, action)
        }
        return super.onStartCommand(intent, flags, startId)
    }
}