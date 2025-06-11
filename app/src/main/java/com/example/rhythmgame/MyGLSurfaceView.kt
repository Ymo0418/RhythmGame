// MyGLSurfaceView.kt
package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.TestMario
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Object.XButton
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
// 2) UI 레이어에서 XButton·Joystick 꺼내기
        val uiLayer  = ObjectManager.Get_Objects(ObjectManager.LayerType.UI)
        val xBtn     = uiLayer.filterIsInstance<XButton>().firstOrNull()
        val joystick = uiLayer.filterIsInstance<Joystick>().firstOrNull() ?: return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 3) 버튼 클릭 우선
                if (xBtn?.isInside(nx, ny) == true) {
                    Log.d("XButton","XButton 클릭!")
                    return true
                }
                // 4) 왼쪽 아래 1/4영역(nx<=0 && ny<=0)일 때만 조이스틱 시작
                if (nx <= 0f && ny <= 0f) {
                    joystick.show(nx, ny)
                    initialX = nx
                    initialY = ny
                    renderer.moveDirX = 0f
                    renderer.moveDirY = 0f
                    renderer.moveSpeed = 0f
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                // 5) 왼쪽 아래 1/4영역일 때만 드래그 처리

                if (nx <= 0f && ny <= 0f) {
                    joystick.move(nx, ny)
                    Log.d("XButton","드ㄱ래그 됨")
                    val dx = nx - initialX
                    val dy = ny - initialY
                    val dist = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                    if (dist > 0f) {
                        val dirX  = dx / dist
                        val dirY  = dy / dist
                        val speed = dist.coerceAtMost(renderer.maxSpeed)
                        renderer.moveDirX  = dirX
                        renderer.moveDirY  = dirY
                        renderer.moveSpeed = speed
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                // 6) 왼쪽 아래였든 아니든, 조이스틱 숨기고 정지
                joystick.hide()
                renderer.moveDirX  = 0f
                renderer.moveDirY  = 0f
                renderer.moveSpeed = 0f
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
