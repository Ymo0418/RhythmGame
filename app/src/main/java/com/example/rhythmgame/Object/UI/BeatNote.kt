package com.example.rhythmgame.Object.UI

import android.content.Context
import android.opengl.GLES20
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager
import com.example.rhythmgame.Manager.UIManager
import com.example.rhythmgame.Object.Camera.Add_Component
import com.example.rhythmgame.Object.Camera.TransformCom
import com.example.rhythmgame.Object.Player
import com.example.rhythmgame.Object.RhythmObject

class BeatNote: RhythmObject() {
    private val texCom    = Add_Component("Texture_note") as Comp_Texture
    private val vibuffer  = Add_Component("VIBufferCom")        as Comp_VIBuffer
    private val shader    = Add_Component("ShaderCom_UI")       as Comp_Shader

    // 화면 크기
    private var viewW = 0f
    private var viewH = 0f

    private val rows = 1
    private val cols = 3

    private var currentFrame = cols - 1  // 기본: 맨 위


    fun onSurfaceSizeChanged(w:Int,h:Int) {
        viewW = w.toFloat()
        viewH = h.toFloat()
    }

    init {
        Components.addAll(listOf(vibuffer, shader, texCom))
        TransformCom.scale = floatArrayOf(0.15f, 0.3f, 1f)
        TransformCom.position[0] = 0f
        TransformCom.position[1] = -0.7f
    }

    // 이전 프레임의 beatValid 상태 저장
    private var prevBeatValid = false

    // 이펙트용
    private var inEffect      = false
    private var effectTimer   = 0f
    private val effectDuration = 0.6f

    // 스케일 기준
    private val baseScaleX = 0.15f
    private val baseScaleY = 0.30f
    private val maxScaleX  = 0.30f
    private val maxScaleY  = 0.45f

    // 알파 기준
    private val defaultAlpha = 0.3f
    private val fullAlpha    = 1f
    private var currentAlpha = defaultAlpha

    // 반복 타이머

    fun playHitEffect() {
        inEffect    = true
        effectTimer = 0f
        // 즉시 선명하고, 커지기 시작
    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)

        // 1) 사운드 매니저 업데이트 → beatRatio, beatWindow 기반으로 GetBeatValid() 계산
        SoundManager.Update(fTimeDelta)

        // 2) 비트 유효 구간(GetBeatValid())의 리프라이징 엣지(rising edge) 체크
        val beatValid = SoundManager.GetBeatValid()
        if (beatValid && !prevBeatValid) {
            // 이전 프레임엔 false였다가, 지금 true가 된 순간에만 이펙트 발동
            playHitEffect()
        }
        prevBeatValid = beatValid


        // 3) 히트 이펙트 애니메이션 처리
        if (inEffect) {
            effectTimer += fTimeDelta
            val t = effectTimer / effectDuration

            when {
                t < 0.2f -> {
                    // 커지고 선명해지는 구간
                    val s = t / 0.2f
                    TransformCom.scale[0] = baseScaleX * (1 - s) + maxScaleX * s
                    TransformCom.scale[1] = baseScaleY * (1 - s) + maxScaleY * s
                    currentAlpha = fullAlpha
                }
                t < 1.0f -> {
                    // 다시 작아지고 흐려지는 구간
                    val s = (t - 0.2f) / 0.8f
                    TransformCom.scale[0] = maxScaleX * (1 - s) + baseScaleX * s
                    TransformCom.scale[1] = maxScaleY * (1 - s) + baseScaleY * s
                    currentAlpha = fullAlpha * (1 - s) + defaultAlpha * s
                }
                else -> {
                    // 이펙트 종료 후 원상 복귀
                    inEffect = false
                    TransformCom.scale[0] = baseScaleX
                    TransformCom.scale[1] = baseScaleY
                    currentAlpha = defaultAlpha
                }
            }
        }

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

// 1열 × 3행 이 아니라 → 3열 × 1행
        val cols       = 3
        val rows       = 1
        val frameW     = 1f / cols   // = 1/3
        val frameH     = 1f / rows   // = 1

// currentFrame = 0(맨 왼쪽),1(가운데),2(맨 오른쪽)
        val frameIndex = currentFrame

// u만 잘라주고 v는 전체(0~1)
        val u0 = frameIndex * frameW    // 0, 1/3, 2/3
        val u1 = u0 + frameW            // 1/3, 2/3, 1
        val v0 = 0f
        val v1 = 1f

        val newUV = floatArrayOf(
            // (u, v) 순서대로
            u0, v0,  // 좌하
            u0, v1,  // 좌상
            u1, v0,  // 우하
            u1, v1   // 우상
        )
        vibuffer.texCoordBuffer.apply {
            clear()
            put(newUV)
            position(0)
        }


        val alpha = if (true) 0.3f else 0.7f
        GLES20.glUniform1f(uAlpha, currentAlpha)

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


}

