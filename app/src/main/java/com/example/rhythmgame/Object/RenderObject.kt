package com.example.rhythmgame.Object

import android.opengl.GLES20
import com.example.rhythmgame.Base.Object
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer

class RenderObject : Object() {
    lateinit var TextureCom: Comp_Texture
    lateinit var BufferCom: Comp_VIBuffer
    lateinit var ShaderCom: Comp_Shader

    init {
        TextureCom = Add_Component("Texture_Image") as Comp_Texture
        BufferCom = Add_Component("VIBufferCom") as Comp_VIBuffer
        ShaderCom = Add_Component("ShaderCom") as Comp_Shader

        Components.add(TextureCom)
        Components.add(BufferCom)
        Components.add(ShaderCom)
    }

    override fun Render(): Boolean {
        ShaderCom.Use_Program()
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val posLoc      = ShaderCom.Get_Attribute("a_Position")
        val texLoc      = ShaderCom.Get_Attribute("a_TexCoord")
        val worldLoc    = ShaderCom.Get_UniformAttribute("u_worldMatrix")
        val samplerLoc  = ShaderCom.Get_UniformAttribute("u_Texture")

        // 정점 데이터 연결
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, BufferCom.vertexBuffer)

        // 텍스처 좌표 연결
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, BufferCom.texCoordBuffer)

        // 월드 행렬 연결
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)

        // 텍스처 바인딩
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, TextureCom.textureID[0])
        GLES20.glUniform1i(samplerLoc, 0)

        // 사각형 그리기 (Triangle Strip)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)

        return true
    }
}