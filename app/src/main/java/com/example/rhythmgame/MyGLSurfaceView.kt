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

        // 연속 렌더링 모드 (프레임마다 onDrawFrame 호출)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val normalizedX = (event.x / width.toFloat()) * 2f - 1f
                val normalizedY = -((event.y / height.toFloat()) * 2f - 1f)

                renderer.setTouchPosition(normalizedX, normalizedY)
            }
        }

        return true
    }
}
