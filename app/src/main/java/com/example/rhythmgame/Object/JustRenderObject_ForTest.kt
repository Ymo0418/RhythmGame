
package com.example.rhythmgame.Object

import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Base.GameObject
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager

class JustRenderObject(tex: String, scale: FloatArray, rot: FloatArray, pos: FloatArray, rg: RenderManager.RenderGroup) : GameObject() {
    private lateinit var rg: RenderManager.RenderGroup

    private lateinit var BufferCom: Comp_VIBuffer
    private lateinit var ShaderCom: Comp_Shader
    private lateinit var TextureCom: Comp_Texture

    init {
        this.rg = rg
        TransformCom.scale = scale
        TransformCom.rotation = rot
        TransformCom.position = pos

        TextureCom = Add_Component(tex) as Comp_Texture
        BufferCom = Add_Component("VIBufferCom") as Comp_VIBuffer
        ShaderCom = Add_Component("ShaderCom_Plane") as Comp_Shader

        Components.add(TextureCom)
        Components.add(BufferCom)
        Components.add(ShaderCom)

        TransformCom.scale[0] = 8f
        TransformCom.scale[1] = 8f

    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)

        RenderManager.Add_RenderObject(rg, this)
    }

    override fun Render(): Boolean {
        ShaderCom.Use_Program()

        val posLoc      = ShaderCom.Get_Attribute("a_Position")
        val texLoc      = ShaderCom.Get_Attribute("a_TexCoord")
        val worldLoc    = ShaderCom.Get_UniformAttribute("u_worldMatrix")
        val samplerLoc  = ShaderCom.Get_UniformAttribute("u_Texture")
        val vpLoc       = ShaderCom.Get_UniformAttribute("u_vpMatrix")

        // 정점 데이터 연결
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, BufferCom.vertexBuffer)

        // 텍스처 좌표 연결
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, BufferCom.texCoordBuffer)

        // 행렬 연결
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)
        GLES20.glUniformMatrix4fv(vpLoc, 1, false, Camera.Get_ViewProj(), 0)

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