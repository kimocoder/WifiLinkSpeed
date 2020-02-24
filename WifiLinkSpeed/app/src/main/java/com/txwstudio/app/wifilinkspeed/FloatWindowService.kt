package com.txwstudio.app.wifilinkspeed

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast


class FloatWindowService : Service() {

    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private var floatLayout: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "Service onCreate", Toast.LENGTH_SHORT).show()

        initWindowManager()
        initLayout()
        layoutOnTouch()

        windowManager?.addView(floatLayout, params)
    }


    /**
     * Setup WindowManager
     * TYPE_SYSTEM_OVERLAY deprecated in API level 26 (Oreo).
     * */
    private fun initWindowManager() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            params?.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }

        params?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params?.format = PixelFormat.TRANSLUCENT

        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.CENTER
    }

    /**
     * Setup layout, get from service_float_window.xml
     * */
    private fun initLayout() {
        floatLayout = LayoutInflater.from(application).inflate(R.layout.service_float_window, null)
    }


    //TODO(performOnClick)
    @SuppressLint("ClickableViewAccessibility")
    private fun layoutOnTouch() {
        var touchedX: Float? = null
        var touchedY: Float? = null
        var originalX: Int? = null
        var originalY: Int? = null

        floatLayout?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        originalX = params?.x
                        originalY = params?.y

                        touchedX = event.rawX
                        touchedY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params?.x = (originalX!! + event.rawX - touchedX!!).toInt()
                        params?.y = (originalY!! + event.rawY - touchedY!!).toInt()

                        windowManager?.updateViewLayout(floatLayout, params)
                        return true
                    }
                }
                return false
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        windowManager?.removeView(floatLayout)
        Toast.makeText(this, "Service onDestroy", Toast.LENGTH_SHORT).show()
    }
}