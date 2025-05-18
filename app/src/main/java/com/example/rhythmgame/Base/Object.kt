package com.example.rhythmgame.Base

import android.graphics.Shader
import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Component.Component
import com.example.rhythmgame.Manager.ComponentManager
import java.nio.Buffer

class Object : Base() {
    val Components: MutableList<Component> = mutableListOf()
    lateinit var TransformCom: Comp_Transform
    lateinit var TextureCom: Comp_Texture
    lateinit var BufferCom: Comp_VIBuffer
    lateinit var ShaderCom: Comp_Shader

    init {
        TransformCom = Add_Component("TransformCom") as Comp_Transform
        TextureCom = Add_Component("Texture_Image") as Comp_Texture
        BufferCom = Add_Component("VIBufferCom") as Comp_VIBuffer
        ShaderCom = Add_Component("ShaderCom") as Comp_Shader
    }

    override fun Update(fTimeDelta: Float) {
        for(comp in Components) {
            comp.Update(fTimeDelta)
        }
    }
    override fun LateUpdate(fTimeDelta: Float) {
        for(comp in Components) {
            comp.LateUpdate(fTimeDelta)
        }
    }
    override fun Render(): Boolean {
        GLES20.glUseProgram(ShaderCom.programHandle)

        val posLoc = GLES20.glGetAttribLocation(ShaderCom.programHandle, "a_Position")
        val texLoc = GLES20.glGetAttribLocation(ShaderCom.programHandle, "a_TexCoord")
        val samplerLoc = GLES20.glGetUniformLocation(ShaderCom.programHandle, "u_Texture")

        // 정점 데이터 연결
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, BufferCom.vertexBuffer)

        // 텍스처 좌표 연결
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, BufferCom.texCoordBuffer)

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

    fun Add_Component(tag: String) : Component {
        val comp = ComponentManager.Clone_Component(tag)
        if(comp == null) {
            Log.e("Component", "Could not find component $tag:")
            throw RuntimeException("Find component failed")
        }

        return comp
    }

}