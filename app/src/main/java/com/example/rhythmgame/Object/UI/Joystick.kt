package com.example.rhythmgame.Object

import android.opengl.GLES20
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Object.UI.UIObject
import kotlin.math.sqrt

import android.util.Log

class Joystick : UIObject() {
    private val texCom2   = Add_Component("TextureCom_Joystick2")   as Comp_Texture
    private val texCom     = Add_Component("TextureCom_Joystick")   as Comp_Texture
    private val vibuffer   = Add_Component("VIBufferCom")           as Comp_VIBuffer
    private val shader     = Add_Component("ShaderCom_UI")          as Comp_Shader

    // 뷰 크기 (onSurfaceChanged 에서 세팅)
    private var viewWidth = 0
    private var viewHeight = 0

    // 활성화 여부
    private var isActive = false

    // 임시 행렬 버퍼
    private val tmpM = FloatArray(16)  // 임시 행렬 버퍼

    private var baseX = 300f
    private var baseY = 800f
    private var curX = 300f
    private var curY = 800f

    data class Movement(var x: Float, var y: Float)
    private var movement = Movement(0f,0f)

    init {
        Components.addAll(listOf(vibuffer, shader, texCom, texCom2))
        TransformCom.scale = floatArrayOf(0.1f, 0.1f, 1f)

        TransformCom.scale[0] = 0.1f
        TransformCom.scale[1] = 0.2f
    }

    fun onSurfaceSizeChanged(width: Int, height: Int) {
        viewWidth  = width
        viewHeight = height
    }

    fun pixelToNdcX(px: Float, viewW: Float): Float =
        px / viewW * 2f - 1f

    fun pixelToNdcY(py: Float, viewH: Float): Float =
        1f - (py / viewH * 2f)

    override fun Update(fTimeDelta: Float) {
        val dx = curX - baseX
        val dy = baseY - curY   //안드로이드 좌표계가 아래로 갈수록 -라서 반대로 해놓음

        val distance = sqrt(dx * dx + dy * dy)

        // 1) 픽셀 → NDC 터치 중심점 찾아서 이미지에 전달용
        val ndcBaseX = pixelToNdcX(baseX, viewWidth.toFloat())
        val ndcBaseY = pixelToNdcY(baseY, viewHeight.toFloat())

        // 2) TransformCom.position 에 반영
        TransformCom.position[0] = ndcBaseX
        TransformCom.position[1] = ndcBaseY

        if (distance > 0f) {
            // 방향 단위벡터(normalize)
            val dirX = dx / distance
            val dirY = dy / distance

            // 길이 제한
            val clampedDistance = distance.coerceAtMost(100f)
            val speed = clampedDistance / 100f

            movement.x = dirX * speed * fTimeDelta
            movement.y = dirY * speed * fTimeDelta
        }
        else {
            movement.x = 0f
            movement.y = 0f
        }
    }

    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)

        RenderManager.Add_RenderObject(RenderManager.RenderGroup.BLEND, this)
    }

    override fun Render(): Boolean {
        shader.Use_Program()
        val worldLoc    = shader.Get_UniformAttribute("u_worldMatrix")
        val aPos = shader.Get_Attribute("a_Position")
        val aTex = shader.Get_Attribute("a_TexCoord")
        val texSampler = shader.Get_UniformAttribute("u_Texture")
        val alphaLoc   = shader.Get_UniformAttribute("u_Alpha")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom.textureID[0])
        GLES20.glUniform1i(texSampler, 0)
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)

        // 평소(비활성)엔 반투명, 활성(isActive)시엔 완전 불투명
        val alpha = if (isActive) 1.0f else 0.5f
        GLES20.glUniform1f(alphaLoc, alpha)

        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)

        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        return true
    }

    override fun OnTouch(event: MotionEvent?) {
        //옳은 터치인지 체크

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // 왼쪽 아래 1/4 영역인지 체크
                if (event.x <= viewWidth / 2f && event.y >= viewHeight / 2f) {
                    isActive = true
                    curX = event.x
                    curY = event.y
                } else {
                    isActive = false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isActive) {
                    curX = event.x
                    curY = event.y
                }
            }
            MotionEvent.ACTION_UP, null -> {
                if (isActive) {
                    curX = baseX
                    curY = baseY
                    isActive = false
                }
            }
        }
    }

    //플레이어가 UIManger에서 GetMovement호출하면 거기서 이거 호출해서
    //조이스틱의 움직임을 플레이어로 주게 된다
    public fun GetMovement(): Movement {
        return movement
    }
}
