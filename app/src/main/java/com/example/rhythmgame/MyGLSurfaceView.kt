// MyGLSurfaceView.kt
package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.TestMario
import com.example.rhythmgame.Manager.ObjectManager
import kotlin.math.hypot

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyGLRenderer

    // [1] 드래그 초기점 저장용
    private var initialX = 0f
    private var initialY = 0f

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
        // [2] 화면 좌표 → OpenGL 정규화(-1..+1)
        val nx = (event.x / width.toFloat()) * 2f - 1f
        val ny = -((event.y / height.toFloat()) * 2f - 1f)

        // [3] Joystick 객체 가져오기
        val joystick = ObjectManager
            .Get_Objects(ObjectManager.LayerType.UI)
            .filterIsInstance<Joystick>()
            .firstOrNull() ?: return true

        // [4] Mario 객체 가져오기
        val mario = ObjectManager
            .Get_Objects(ObjectManager.LayerType.PLAYER)
            .filterIsInstance<TestMario>()
            .firstOrNull()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // [5] 터치 시작:
                //      조이스틱 베이스 표시,
                //      드래그 초기점 설정,
                //      Mario 방향·속도 초기화
                joystick.show(nx, ny)
                initialX = nx
                initialY = ny

                renderer.moveDirX = 0f
                renderer.moveDirY = 0f
                renderer.moveSpeed = 0f
            }

            MotionEvent.ACTION_MOVE -> {
                // [6] 드래그 중: 조이스틱 노브만 이동
                joystick.move(nx, ny)

                // [7] 드래그 벡터 계산: (nx,ny) 대비 초기 클릭점
                val dx = nx - initialX
                val dy = ny - initialY
                val distance = hypot(dx.toDouble(), dy.toDouble()).toFloat()

                if (distance > 0f) {
                    // [8] 방향 단위벡터(normalize)
                    val dirX = dx / distance
                    val dirY = dy / distance

                    // [9] 속도 = 드래그 벡터 크기(distance),
                    //      필요하다면 maxSpeed로 제한
                    val speed = distance.coerceAtMost(renderer.maxSpeed)

                    // [10] renderer에 방향과 속도 전달
                    renderer.moveDirX = dirX
                    renderer.moveDirY = dirY
                    renderer.moveSpeed = speed
                } else {
                    renderer.moveDirX = 0f
                    renderer.moveDirY = 0f
                    renderer.moveSpeed = 0f
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                // [11] 터치 종료:
                //       조이스틱 숨김, Mario 정지
                joystick.hide()
                renderer.moveDirX = 0f
                renderer.moveDirY = 0f
                renderer.moveSpeed = 0f
            }
        }
        return true
    }
}
