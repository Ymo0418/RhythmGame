package com.example.rhythmgame.Object

import android.opengl.GLES20
import android.opengl.Matrix
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Object.UI.UIObject
import kotlin.math.sqrt
import android.util.Log
import com.example.rhythmgame.Manager.UIManager

class Joystick : UIObject() {
    private val texCom     = Add_Component("TextureCom_Joystick")   as Comp_Texture
    private val texCom2    = Add_Component("TextureCom_Joystick2")  as Comp_Texture
    private val vibuffer   = Add_Component("VIBufferCom")           as Comp_VIBuffer
    private val shader     = Add_Component("ShaderCom_UI")          as Comp_Shader

    // 뷰 크기 (onSurfaceChanged 에서 세팅)
    private var viewWidth = 0
    private var viewHeight = 0

    var showing = true  // 기본 보임

    // 활성화 여부
    private var isActive = false

    // 조이스틱 베이스 픽셀 위치
    private var baseX = 300f
    private var baseY = 800f
    // 터치 현재 위치
    private var curX  = baseX
    private var curY  = baseY

    // 최대 드래그 반경 (픽셀 단위)
    private val maxDistance = 200f

    // 이미지 그리기용 임시 행렬 버퍼
    private val tmpM = FloatArray(16)

    data class Movement(var x: Float, var y: Float)
    private var movement = Movement(0f,0f)
    // 핸들(조이스틱2) 스케일 (더 작게)
    private val handleScaleX = 0.07f
    private val handleScaleY = 0.14f

    init {
        Components.addAll(listOf(vibuffer, shader, texCom, texCom2))
        // 첫번째 텍스처(베이스)의 스케일
        TransformCom.scale[0] = 0.1f
        TransformCom.scale[1] = 0.2f
    }

    fun onSurfaceSizeChanged(width: Int, height: Int) {
        viewWidth  = width
        viewHeight = height
    }

    private fun pixelToNdcX(px: Float): Float = px / viewWidth * 2f - 1f
    private fun pixelToNdcY(py: Float): Float = 1f - (py / viewHeight * 2f)

    override fun Update(fTimeDelta: Float) {
        // 이동량 계산
        val dx = curX - baseX
        val dy = baseY - curY
        val dist = sqrt(dx*dx + dy*dy)
        if (dist > 0f) {
            val dirX = dx / dist
            val dirY = dy / dist
            val clamped = dist.coerceAtMost(maxDistance)
            val speed = clamped / 100f
            movement.x = dirX * speed * fTimeDelta
            movement.y = dirY * speed * fTimeDelta
        } else {
            movement.x = 0f; movement.y = 0f
        }
    }

    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)
        RenderManager.Add_RenderObject(RenderManager.RenderGroup.BLEND, this)
    }

    override fun Render(): Boolean {
        if (UIManager.isGameOver) return true   // 게임오버면 그리지 말자

        shader.Use_Program()
        val worldLoc   = shader.Get_UniformAttribute("u_worldMatrix")
        val aPos       = shader.Get_Attribute("a_Position")
        val aTex       = shader.Get_Attribute("a_TexCoord")
        val texSampler = shader.Get_UniformAttribute("u_Texture")
        val alphaLoc   = shader.Get_UniformAttribute("u_Alpha")

        // 1) 베이스 이미지 그리기
        val ndcBaseX = pixelToNdcX(baseX)
        val ndcBaseY = pixelToNdcY(baseY)
        TransformCom.position[0] = ndcBaseX
        TransformCom.position[1] = ndcBaseY
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom.textureID[0])
        GLES20.glUniform1i(texSampler, 0)
        GLES20.glUniform1f(alphaLoc, if (isActive) 1f else 0.8f)
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)
        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)
        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // 2) 핸들 이미지 그리기 (항상 표시, 최대 드래그 제한)
        // raw 픽셀 좌표
        var handlePxX = baseX
        var handlePxY = baseY
        if (isActive) {
            val rawDx = curX - baseX
            val rawDy = curY - baseY
            val rawDist = sqrt(rawDx * rawDx + rawDy * rawDy)
            if (rawDist <= maxDistance) {
                handlePxX = curX
                handlePxY = curY
            } else {
                val ratio = maxDistance / rawDist
                handlePxX = baseX + rawDx * ratio
                handlePxY = baseY + rawDy * ratio
            }
        }
        val ndcHandleX = pixelToNdcX(handlePxX)
        val ndcHandleY = pixelToNdcY(handlePxY)
        Matrix.setIdentityM(tmpM, 0)
        Matrix.translateM(tmpM, 0, ndcHandleX, ndcHandleY, 0f)
        Matrix.scaleM(tmpM, 0, handleScaleX, handleScaleY, 1f)
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, tmpM, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom2.textureID[0])
        val handleAlpha = if (isActive) 1f else 0.7f
        GLES20.glUniform1f(alphaLoc, handleAlpha)
        vibuffer.vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)
        vibuffer.texCoordBuffer.position(0)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        return true
    }

    override fun OnTouch(event: MotionEvent?) {
        if (UIManager.isGameOver) return   // 게임오버면 그리지 말자
        event ?: return
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 터치 시작: 왼쪽 아래 1/4 영역 안이면 활성화
                if (event.x <= viewWidth / 2f && event.y >= viewHeight / 2f) {
                    isActive = true
                    curX = event.x
                    curY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // 활성 중일 때만 위치 업데이트 (영역 벗어나도 유지)
                if (isActive) {
                    curX = event.x
                    curY = event.y
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 터치 종료 시 비활성화 및 초기 위치 복귀
                isActive = false
                curX = baseX
                curY = baseY
            }
        }
    }

    fun GetMovement(): Movement = movement
}
