// MyGLSurfaceView.kt
package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer

    init {
        // OpenGL ES 2.0 컨텍스트 사용 선언
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8,8,8,8,16,0)

        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        renderer.OnTouchEvent(event)
        return true
    }
}
