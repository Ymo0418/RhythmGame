// app/src/main/java/com/example/rhythmgame/Object/GLButton.kt
package com.example.rhythmgame.Object

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Base.Object
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer

/**
 * OpenGL로 그려지는 “스킬 버튼” 오브젝트
 * 화면 우측 하단에 고정된 크기로 표시됩니다.
 */
class XButton(private val context: Context) : Object() {
    private val texCom   = Add_Component("Texture_Button") as Comp_Texture
    private val vibuffer = Add_Component("VIBufferCom")  as Comp_VIBuffer
    private val shader   = Add_Component("ShaderCom")     as Comp_Shader
    private val btnTrans = Add_Component("TransformCom")  as Comp_Transform

    init {
        Components.addAll(listOf(vibuffer, shader, texCom, btnTrans))

            //이미지 위치
        // [1] 버튼 크기 NDC 기준 0.2 × 0.2
        btnTrans.scale = floatArrayOf(0.2f, 0.2f, 1f)
        val halfW = btnTrans.scale[0] * 0.5f
        val halfH = btnTrans.scale[1] * 0.5f

        // 2) dp → px → NDC margin 계산 (20dp)
        val dpMargin = 20f
        val metrics  = context.resources.displayMetrics
        val pxMargin = dpMargin * metrics.density                 // 20dp → px
        val ndcMarginX = pxMargin * 2f / metrics.widthPixels      // px → NDC
        val ndcMarginY = pxMargin * 2f / metrics.heightPixels

        // 3) 위치 설정: 화면 우측 아래에서 반크기+ndcMargin 만큼 떨어진 곳
        btnTrans.position[0] = 1f - halfW - ndcMarginX
        btnTrans.position[1] = -1f + halfH + ndcMarginY
    }


    fun resize(screenW: Int, screenH: Int) {
        val metrics  = context.resources.displayMetrics
        val sizePx60 = metrics.density * 80f           // 60dp → px
        val ndcW     = sizePx60 * 2f / screenW.toFloat()
        val ndcH     = sizePx60 * 2f / screenH.toFloat()
        btnTrans.scale = floatArrayOf(ndcW, ndcH, 1f)
    }

    override fun Render(): Boolean {
        shader.Use_Program()
        val worldLoc    = shader.Get_UniformAttribute("u_worldMatrix")
        val aPos = shader.Get_Attribute("a_Position")
        val aTex = shader.Get_Attribute("a_TexCoord")
        val texSampler = shader.Get_UniformAttribute("u_Texture")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom.textureID[0])
        GLES20.glUniform1i(texSampler, 0)
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, btnTrans.SRP, 0)
        drawQuad(aPos, aTex)

        return true
    }

    /**
     * vibuffer와 aPos/aTex 속성만으로
     * 사각형 하나를 그려주는 헬퍼 메서드
     */
    private fun drawQuad(aPos: Int, aTex: Int) {

        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)

        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }


    /**
     * @return 터치 좌표(nx,ny)가 이 버튼 영역 안에 있으면 true
     */
    fun isInside(nx: Float, ny: Float): Boolean {
        // btnTrans는 private이지만 이 메서드 안에선 접근 가능합니다
        val cx = btnTrans.position[0]
        val cy = btnTrans.position[1]
        val halfW = btnTrans.scale[0] * 0.5f
        val halfH = btnTrans.scale[1] * 0.5f
        return (nx >= cx - halfW && nx <= cx + halfW
                && ny >= cy - halfH && ny <= cy + halfH)
    }
}
