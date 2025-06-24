package com.example.rhythmgame.Object.Monster

import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Base.GameObject
import com.example.rhythmgame.Component.Comp_Collider
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.CollisionManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager
import com.example.rhythmgame.Object.Camera

class Skill(target: Comp_Transform): GameObject() {
    protected lateinit var BufferCom: Comp_VIBuffer
    protected lateinit var ShaderCom: Comp_Shader
    protected lateinit var ColliderCom: Comp_Collider
    protected lateinit var TextureCom: Comp_Texture
    protected var duration = 0f
    protected var curDuration = 0f
    private var currentFrame = 0
    private var spriteSize = 16f
    private var bDone = false

    init {
        duration = 0.4f
        TransformCom.position = target.position.copyOf()
        TransformCom.scale = floatArrayOf(0.5f, 0.5f, 1f)
        BufferCom = Add_Component("VIBufferCom") as Comp_VIBuffer
        ShaderCom = Add_Component("ShaderCom_Anim") as Comp_Shader
        ColliderCom = Add_Component("ColliderCom") as Comp_Collider
        TextureCom = Add_Component("TextureCom_Holy") as Comp_Texture

        ColliderCom.SetColliderInfo(TransformCom, 1, 1, 0.5f, 0.3f)

        Components.add(BufferCom)
        Components.add(ShaderCom)
        Components.add(ColliderCom)
        Components.add(TextureCom)

        SoundManager.PlaySFX("Holy", 1f)
    }

    override fun Update(fTimeDelta: Float) {

        currentFrame = (spriteSize * curDuration / duration).toInt() - 1

        if(curDuration >= duration)
            isDead = true

        curDuration += fTimeDelta

        super.Update(fTimeDelta)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        if(ColliderCom.isCollide) {
            bDone = true
        }

        if(!bDone)
            CollisionManager.RegisterCollider(CollisionManager.ColliderGroup.SKILL, ColliderCom)

        RenderManager.Add_RenderObject(RenderManager.RenderGroup.NONBLEND, this)

        super.LateUpdate(fTimeDelta)
    }

    override fun Render(): Boolean {
        super.Render()

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
        val texScale = floatArrayOf(1.0f / spriteSize, 1.0f)
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