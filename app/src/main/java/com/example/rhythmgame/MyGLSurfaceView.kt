// app/src/main/java/com/example/rhythmgame/MyGLSurfaceView.kt
package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.TestMario
import com.example.rhythmgame.Manager.ObjectManager

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer
    private var lastNx = 0f
    private var lastNy = 0f

    init {
        setEGLContextClientVersion(2)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 1) GL 좌표 변환
        val nx = (event.x     / width.toFloat())  * 2f - 1f
        val ny = -((event.y  / height.toFloat()) * 2f - 1f)

        // 2) Joystick 인스턴스 가져오기
        val joystick = ObjectManager
            .Get_Objects(ObjectManager.LayerType.UI)
            .filterIsInstance<Joystick>()
            .firstOrNull() ?: return true

        // 3) TestMario 인스턴스 가져오기
        val mario = ObjectManager
            .Get_Objects(ObjectManager.LayerType.PLAYER)
            .filterIsInstance<TestMario>()
            .firstOrNull()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 터치한 위치에 조이스틱 고정
                joystick.show(nx, ny)
                // 기준점 초기화
                lastNx = nx
                lastNy = ny
            }
            MotionEvent.ACTION_MOVE -> {
                // 드래그 시 Δ만큼 마리오 이동
                val dx = nx - lastNx
                val dy = ny - lastNy
                lastNx = nx
                lastNy = ny

                mario?.let {
                    it.TransformCom.position[0] += dx
                    it.TransformCom.position[1] += dy
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                // 손 떼면 조이스틱 사라짐
                joystick.hide()

            }
        }
        return true
    }
}
