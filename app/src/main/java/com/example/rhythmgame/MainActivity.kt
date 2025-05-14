package com.example.rhythmgame

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rhythmgame.MyGLRenderer
import com.example.rhythmgame.R

class MainActivity : AppCompatActivity() {

    private lateinit var glView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glSurfaceView)
        // OpenGL ES 2.0 컨텍스트 요청
        glView.setEGLContextClientVersion(2)
        // 렌더러 설정
        glView.setRenderer(MyGLRenderer(this))
        // 필요에 따라 렌더 모드 선택 (연속 렌더링 or 요청 시만 렌더링)
        glView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }
}
