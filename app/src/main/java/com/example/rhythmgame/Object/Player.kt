package com.example.rhythmgame.Object

import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Base.GameObject
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.UIManager

class Player: GameObject() {
    val bpm = 70f
    val frameCount = 7
    val totalDuration = 60f / bpm
    val frameDuration = totalDuration / frameCount
    public var currentFrame = 3
    var accum = 0f

    private lateinit var TextureCom: Comp_Texture
    private lateinit var BufferCom: Comp_VIBuffer
    private lateinit var ShaderCom: Comp_Shader

    init {
        TextureCom = Add_Component("TextureCom_Player_Idle") as Comp_Texture
        BufferCom = Add_Component("VIBufferCom") as Comp_VIBuffer
        ShaderCom = Add_Component("ShaderCom_Anim") as Comp_Shader

        Components.add(TextureCom)
        Components.add(BufferCom)
        Components.add(ShaderCom)

        TransformCom.position[0] = 3f
        TransformCom.position[1] = 3f
    }

    override fun Update(fTimeDelta: Float) {

        TransformCom.position[0] += UIManager.GetMovement().x
        TransformCom.position[1] += UIManager.GetMovement().y

        accum += fTimeDelta
        if (accum >= frameDuration) {
            accum -= frameDuration
            currentFrame = (currentFrame + 1) % frameCount
        }

        super.Update(fTimeDelta)

    }


    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)

        RenderManager.Add_RenderObject(RenderManager.RenderGroup.NONBLEND, this)
    }

    override fun Render(): Boolean {
        ShaderCom.Use_Program()

        val posLoc      = ShaderCom.Get_Attribute("a_Position")
        val texLoc      = ShaderCom.Get_Attribute("a_TexCoord")
        val worldLoc    = ShaderCom.Get_UniformAttribute("u_worldMatrix")
        val samplerLoc  = ShaderCom.Get_UniformAttribute("u_Texture")
        val vpLoc       = ShaderCom.Get_UniformAttribute("u_vpMatrix")

        val scaleLoc    = ShaderCom.Get_UniformAttribute("u_FrameScale")
        val offsetLoc   = ShaderCom.Get_UniformAttribute("u_FrameOffset")

        // 정점 데이터 연결
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, BufferCom.vertexBuffer)

        // 텍스처 좌표 연결
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, BufferCom.texCoordBuffer)

        // 행렬 연결
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)
        GLES20.glUniformMatrix4fv(vpLoc, 1, false, Camera.Get_ViewProj(), 0)

        // 애니메이션 정보 연결
        val texScale = floatArrayOf(1.0f / 7.0f, 1.0f)
        val texOffset = floatArrayOf(currentFrame.toFloat(), 0f)
        GLES20.glUniform2fv(scaleLoc, 1, texScale, 0)
        GLES20.glUniform2fv(offsetLoc, 1, texOffset, 0)

        // 텍스처 바인딩
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureCom.textureID[0])
        GLES20.glUniform1i(samplerLoc, 0)

        // 사각형 그리기 (Triangle Strip)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        val err = GLES20.glGetError()
        if (err != GLES20.GL_NO_ERROR) {
            Log.e("GL", "glDrawArrays error: $err")
        }
        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)

        return true
    }
}