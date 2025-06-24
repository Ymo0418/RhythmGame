package com.example.rhythmgame.Object

import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Component.Comp_Collider
import com.example.rhythmgame.Manager.CollisionManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager
import com.example.rhythmgame.Manager.UIManager

class Player: RhythmObject() {
    public var currentFrame = 3
    var bFlip = false
    var bMove = false
    public var hp = 4
    var fInvincible = 0f
    var fFlicker = 0f
    var bInvisible = false

    private lateinit var IdleTextureCom: Comp_Texture
    private lateinit var WalkTextureCom: Comp_Texture
    private lateinit var BufferCom: Comp_VIBuffer
    private lateinit var ShaderCom: Comp_Shader
    private lateinit var ColliderCom: Comp_Collider

    init {
        TransformCom.scale = floatArrayOf(1.12f, 0.84f, 1f)
        IdleTextureCom = Add_Component("TextureCom_Player_Idle") as Comp_Texture
        WalkTextureCom = Add_Component("TextureCom_Player_Walk") as Comp_Texture
        BufferCom = Add_Component("VIBufferCom") as Comp_VIBuffer
        ShaderCom = Add_Component("ShaderCom_Anim") as Comp_Shader
        ColliderCom = Add_Component("ColliderCom") as Comp_Collider
        ColliderCom.SetColliderInfo(TransformCom, 1, 0, 0.6f, 0.8f, 0f, 0.1f)
        CollisionManager.RegisterCollider(CollisionManager.ColliderGroup.PLAYER, ColliderCom)

        Components.add(IdleTextureCom)
        Components.add(WalkTextureCom)
        Components.add(BufferCom)
        Components.add(ShaderCom)
        Components.add(ColliderCom)
    }

    override fun Update(fTimeDelta: Float) {
        val joystickMove = UIManager.GetJoystickMovement()

        TransformCom.position[0] += joystickMove.x
        TransformCom.position[1] += joystickMove.y

        if(joystickMove.x == 0f &&
            joystickMove.y == 0f)
            bMove = false
        else
            bMove = true

        if(joystickMove.x > 0f) {
            TransformCom.rotation[1] = 0f
        }
        else if(joystickMove.x < 0f) {
            TransformCom.rotation[1] = 180f
        }

        currentFrame = (beatRatio * if(bMove) 8f else 7f).toInt()

        super.Update(fTimeDelta)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        if(fInvincible <= 0f) {

            if(ColliderCom.isCollide) {
                hp -= ColliderCom.collideInfo
                fInvincible = 2f
                fFlicker = 0f
                bInvisible = true
            }

            RenderManager.Add_RenderObject(RenderManager.RenderGroup.NONBLEND, this)
        }
        else {
            fInvincible -= fTimeDelta
            fFlicker += fTimeDelta

            if(fFlicker > 0.032f) {
                fFlicker = 0f
                bInvisible = !bInvisible
            }

            if(bInvisible) {
                RenderManager.Add_RenderObject(RenderManager.RenderGroup.NONBLEND, this)
            }

        }

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
        val texScale = floatArrayOf(1.0f / if(bMove) 8f else 7f, 1.0f)
        val texOffset = floatArrayOf(currentFrame.toFloat(), 0f)
        GLES20.glUniform2fv(scaleLoc, 1, texScale, 0)
        GLES20.glUniform2fv(offsetLoc, 1, texOffset, 0)

        // 텍스처 바인딩
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, if(bMove) WalkTextureCom.textureID[0] else IdleTextureCom.textureID[0])
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