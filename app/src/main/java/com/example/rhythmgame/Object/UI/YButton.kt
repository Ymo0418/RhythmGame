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
import com.example.rhythmgame.Manager.RenderManager

//UIObject 상속받기
class YButton(private val context: Context): UIObject() {
    private val texCom     = Add_Component("Texture_yButton")   as Comp_Texture
    private val vibuffer   = Add_Component("VIBufferCom")           as Comp_VIBuffer
    private val shader     = Add_Component("ShaderCom_UI")          as Comp_Shader

    private var isPressed = false

    // 버튼 터치 영역 (픽셀 기준)
    private val touchLeftPx: Float
    private val touchRightPx: Float
    private val touchTopPx: Float
    private val touchBottomPx: Float

    init {
        Components.addAll(listOf(vibuffer, shader, texCom))
        val dm       = context.resources.displayMetrics
        val w        = dm.widthPixels.toFloat()
        val h        = dm.heightPixels.toFloat()
        val mPx      = 20f * dm.density
        val bPx      = 60f * dm.density

        val xRight = w - mPx
        val xLeft  = xRight - bPx

        // 2) Y 버튼은 X 버튼의 왼쪽에 mPx 간격만큼 띄워서,
        //    그 지점을 우측 경계로 하고 bPx 크기만큼 좌측으로 확장
        touchRightPx  = xLeft - mPx
        touchLeftPx   = touchRightPx - bPx

        // 세로(상/하)는 X 버튼과 동일하게 아래에서 mPx 위, 위에서 bPx 만큼 내려온 지점
        touchBottomPx = h - mPx
        touchTopPx    = touchBottomPx - bPx

        TransformCom.scale[0] = 0.07f
        TransformCom.scale[1] = 0.14f
    }

    override fun Update(fTimeDelta: Float) {
        TransformCom.position[0] = 0.7f
        TransformCom.position[1] = -0.7f
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

        GLES20.glUniform1f(alphaLoc, if (isPressed) 0.5f else 1.0f)

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
                if (event.x in touchLeftPx..touchRightPx && event.y in touchTopPx..touchBottomPx) {
                    isPressed = true
                    Log.e("YButton", "버튼 누름 시작")
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isPressed) {
                    isPressed = false
                    Log.e("XButton", "버튼 해제")
                }
            }
        }
    }

}