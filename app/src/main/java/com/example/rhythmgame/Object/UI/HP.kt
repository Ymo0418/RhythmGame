package com.example.rhythmgame.Object.UI

import android.content.Context
import android.opengl.GLES20
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.UIManager

class HP: UIObject() {
    private val texCom    = Add_Component("Texture_HP") as Comp_Texture
    private val vibuffer  = Add_Component("VIBufferCom")        as Comp_VIBuffer
    private val shader    = Add_Component("ShaderCom_UI")       as Comp_Shader

    // 화면 크기
    private var viewW = 0f
    private var viewH = 0f

    // sprite sheet 가로/세로 분할 수
    private val rows = 5
    private val cols = 1

    private var currentFrame = rows - 1  // 기본: 맨 위



    /** 입력1이 들어오면 호출하여 인덱스를 감소시킵니다. 최저 0까지. */
    fun decreaseFrame() {
        if (currentFrame > 1) {
            currentFrame--
        } else {
            // 이미 0인 상태에서 추가 감소 시 → Game Over
            UIManager.isGameOver = true
        }
    }
//                    UIManager.hp?.decreaseFrame()

    /** 입력2가 들어오면 호출하여 인덱스를 증가시킵니다. 최대 rows-1까지. */
    fun increaseFrame() {
        currentFrame = (currentFrame + 1).coerceAtMost(rows - 1)
    }
//                    UIManager.hp?.increaseFrame()

    fun onSurfaceSizeChanged(w:Int,h:Int) {
        viewW = w.toFloat()
        viewH = h.toFloat()
    }

    init {
        Components.addAll(listOf(vibuffer, shader, texCom))
        TransformCom.scale = floatArrayOf(0.3f, 0.15f, 1f)
        TransformCom.position[0] = -0.7f
        TransformCom.position[1] = 0.8f
    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)
        // HP 변화에 따라 currentFrame을 설정하세요.
        // 예: 100% → frame=0, 80%→1, ... 0%→4
    }

    override fun LateUpdate(dt: Float) {
        super.LateUpdate(dt)
        RenderManager.Add_RenderObject(RenderManager.RenderGroup.BLEND, this)
    }

    override fun Render(): Boolean {
        shader.Use_Program()
        val uWorld   = shader.Get_UniformAttribute("u_worldMatrix")
        val aPos     = shader.Get_Attribute("a_Position")
        val aTex     = shader.Get_Attribute("a_TexCoord")
        val uTex     = shader.Get_UniformAttribute("u_Texture")
        val uAlpha   = shader.Get_UniformAttribute("u_Alpha")

        // 텍스처 바인딩
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(uTex, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom.textureID[0])
        GLES20.glUniform1f(uAlpha, 1f)

        // 월드 매트릭스 업로드
        GLES20.glUniformMatrix4fv(uWorld, 1, false, TransformCom.SRP, 0)

        // --- UV 계산: 맨 위 프레임만 (V 좌표 반전) ---
        val frameW = 1f / cols
        val frameH = 1f / rows
        // rows=5, 인덱스 0이 아래쪽이라면, 맨 위는 index=4
        val frameIndex = currentFrame
        // OpenGL은 (0,0) 하단이므로 V 좌표 반전 필요
        val v0 = 1f - (frameIndex + 1) * frameH   // 아래 UV
        val v1 = 1f - frameIndex * frameH         // 위 UV

        val newUV = floatArrayOf(
            // (u, v) 순서대로
            0f, v0,
            0f, v1,
            1f, v0,
            1f, v1
        )
        vibuffer.texCoordBuffer.apply {
            clear()
            put(newUV)
            position(0)
        }

        // 버텍스·텍스처 좌표 바인딩
        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos,3,GLES20.GL_FLOAT,false,0,vibuffer.vertexBuffer)
        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex,2,GLES20.GL_FLOAT,false,0,vibuffer.texCoordBuffer)

        // 그리기
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)
        return true
    }



    override fun OnTouch(event: MotionEvent?) {
        // HPBar는 터치 없음
    }
}

