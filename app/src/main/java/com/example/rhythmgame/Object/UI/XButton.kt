package com.example.rhythmgame.Object.UI

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.util.Log
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager
import com.example.rhythmgame.Manager.UIManager
import com.example.rhythmgame.Object.Monster.Skill
import kotlin.random.Random

//UIObject 상속받기
class XButton(private val context: Context): UIObject() {
    private val texCom     = Add_Component("Texture_xButton")   as Comp_Texture
    private val vibuffer   = Add_Component("VIBufferCom")           as Comp_VIBuffer
    private val shader     = Add_Component("ShaderCom_UI")          as Comp_Shader

    private var isPressed = false
    private var cooldown = 3f
    private var curCooldown = 0f

    // 버튼 터치 영역 (픽셀 기준)
    private val touchLeftPx: Float
    private val touchRightPx: Float
    private val touchTopPx: Float
    private val touchBottomPx: Float

    // 화면 크기 저장용
    private var viewWidth  = 0f
    private var viewHeight = 0f

    init {
        Components.addAll(listOf(vibuffer, shader, texCom))
        val dm       = context.resources.displayMetrics
        val w        = dm.widthPixels.toFloat()
        val h        = dm.heightPixels.toFloat()
        val mPx      = 20f * dm.density
        val bPx      = 60f * dm.density

        touchLeftPx   = w - mPx - bPx
        touchRightPx  = w - mPx
        touchTopPx    = h - mPx - bPx
        touchBottomPx = h - mPx

        TransformCom.scale[0] = 0.07f
        TransformCom.scale[1] = 0.14f
    }
    override fun Update(fTimeDelta: Float) {
        TransformCom.position[0] = 0.875f
        TransformCom.position[1] = -0.725f
        super.Update(fTimeDelta)
    }
    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)
        RenderManager.Add_RenderObject(RenderManager.RenderGroup.BLEND, this)

    }
    override fun Render(): Boolean {
        shader.Use_Program()
        val worldLoc    = shader.Get_UniformAttribute("u_worldMatrix")
        val aPos = shader.Get_Attribute("a_Position")
        val aTex = shader.Get_Attribute("a_TexCoord")
        val texSampler = shader.Get_UniformAttribute("u_Texture")
        val alphaLoc   = shader.Get_UniformAttribute("u_Alpha")

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCom.textureID[0])
        GLES20.glUniform1i(texSampler, 0)
        GLES20.glUniformMatrix4fv(worldLoc, 1, false, TransformCom.SRP, 0)

        val alpha = if (isPressed) 0.3f else 0.7f
        GLES20.glUniform1f(alphaLoc, alpha)

        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)

        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        return true
    }

    //이 함수 override하면 입력받을수있음
    override fun OnTouch(event: MotionEvent?) {
        event ?: return
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Down 시 영역 안이면 눌림 상태로
                if (event.x in touchLeftPx..touchRightPx
                    && event.y in touchTopPx..touchBottomPx) {

                    isPressed = true

                    if(SoundManager.GetBeatValid())
                    {
                        val Monsters = ObjectManager.Get_Objects(ObjectManager.LayerType.MONSTER)
                        if(Monsters.isNotEmpty()) {
                            val random = Random.nextInt(0, Monsters.size)

                            val Skill = Skill(Monsters[random].GetTransformComp())
                            ObjectManager.Add_Object(ObjectManager.LayerType.SKILL, Skill)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isPressed) {
                    isPressed = false
                }
            }
        }
    }

}