package com.example.rhythmgame


import android.content.Context
import android.opengl.GLSurfaceView

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer

    init {
        // OpenGL ES 2.0 컨텍스트 사용 선언
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer(context)
        setRenderer(renderer)

        // 연속 렌더링 모드 (프레임마다 onDrawFrame 호출)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
