// app/src/main/java/com/example/rhythmgame/Object/Joystick.kt
package com.example.rhythmgame.Object

import android.hardware.input.InputManager
import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Base.Object
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer

class Joystick : Object() {
    private val texCom     = Add_Component("Texture_Joystick") as Comp_Texture
    private val vibuffer   = Add_Component("VIBufferCom")  as Comp_VIBuffer
    private val shader     = Add_Component("ShaderCom")     as Comp_Shader
    private val KnobTrans  = Add_Component("TransformCom")     as Comp_Transform

    private var isVisible = false
    private var baseX = 0f
    private var baseY = 0f
    private var knobX = 0f
    private var knobY = 0f

    init {
        Components.addAll(listOf(vibuffer, shader, texCom, KnobTrans))
        // 베이스 크기
        TransformCom.scale = floatArrayOf(0.3f, 0.3f, 1f)
    }

    /** 터치 시작 시 호출되어 베이스와 노브를 초기화 */
    fun show(x: Float, y: Float) {
        isVisible = true
        baseX = x; baseY = y
        knobX = x; knobY = y
    }

    /** 드래그할 때마다 호출되어 노브 위치만 업데이트 */
    fun move(x: Float, y: Float) {
        KnobTrans.position[0] = x
        KnobTrans.position[1] = y
    }

    /** 터치가 끝났을 때 호출되어 조이스틱을 숨김 */
    fun hide() {
        isVisible = false
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
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)
        drawQuad(aPos, aTex)

        return true
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom.textureID[0])
        GLES20.glUniform1i(texSampler, 0)
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, KnobTrans.SRP, 0)
        drawQuad(aPos, aTex)

        return true
    }

    private fun drawQuad(aPos: Int, aTex: Int) {
        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)

        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}
