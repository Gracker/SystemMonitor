package com.android.monitor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.android.monitor.util.LocalServiceManager
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.widget.Toast
import com.android.monitor.util.FPSUtils
import com.android.monitor.service.FloatingViewService
import android.content.Intent
import android.util.Log
import com.android.monitor.view.FloatingView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init(){

        switch_fps.setOnClickListener {
            if(switch_fps.isChecked) {
                var intent = Intent()
                intent.setClass(this, FloatingViewService::class.java)
                intent.putExtra(FloatingViewService.ACTION,FloatingViewService.SHOW)
                intent.putExtra(FloatingViewService.TYPE, FloatingViewService.FPS)
                startService(intent)
            } else {
                var intent = Intent()
                intent.setClass(this, FloatingViewService::class.java)
                intent.putExtra(FloatingViewService.ACTION,FloatingViewService.HIDE)
                intent.putExtra(FloatingViewService.TYPE, FloatingViewService.FPS)
                startService(intent)
            }
        }
    }
    override fun onResume() {
        super.onResume()
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
