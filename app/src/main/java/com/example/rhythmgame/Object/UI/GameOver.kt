package com.example.rhythmgame.Object.UI

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Object.UI.UIObject
import android.app.Activity

class GameOver(private val context: Context) : UIObject() {
    // 셰이더·버퍼·텍스처
    private val shader    = Add_Component("ShaderCom_UI")  as Comp_Shader
    private val vibuffer  = Add_Component("VIBufferCom")   as Comp_VIBuffer
    private val texLogo   = Add_Component("Texture_GameOver")  as Comp_Texture
    private val texCont   = Add_Component("Texture_Continue")  as Comp_Texture
    private val texExit   = Add_Component("Texture_Exit_Game") as Comp_Texture

    // 버튼 NDC 위치·크기
    private val size   = 0.5f
    private val posLogo = floatArrayOf(0f,  0.5f)
    private val posCont = floatArrayOf(0f,  0f)
    private val posExit = floatArrayOf(0f, -0.5f)

    // 이벤트 콜백
    var onContinue: (() -> Unit)? = null
    var onExit:     (() -> Unit)? = null

    private var screenWidth = 0
    private var screenHeight = 0



    private val btnW: Float   // 버튼 너비(px)
    private val btnH: Float   // 버튼 높이(px)
    //continue
    private val contTouchLeft: Float
    private val contTouchRight: Float
    private val contTouchTop: Float
    private val contTouchBottom: Float
    //exit
    private val exitTouchLeft: Float
    private val exitTouchRight: Float
    private val exitTouchTop: Float
    private val exitTouchBottom: Float

    init {
        // 화면 크기 필요 (onSurfaceSizeChanged에서 계산해도 무방)
        val dm = context.resources.displayMetrics
        val w = dm.widthPixels.toFloat()
        val h = dm.heightPixels.toFloat()

        btnW = 0.3f * w // 원래 0.3 NDC × 화면 절반 → 0.3 × w 로 조정 (원하면 bPx 고정 크기도 가능)
        btnH = 0.3f * h

        // Continue 버튼: 화면 중앙 기준
        contTouchLeft   = (w - btnW) / 2f
        contTouchRight  = (w + btnW) / 2f
        contTouchTop    = (h - btnH) / 2f
        contTouchBottom = (h + btnH) / 2f

        // Exit 버튼: 중앙에서 아래로 120dp만큼 띄움
        val offsetDp = 120f
        val offsetPx = offsetDp * dm.density

        exitTouchLeft   = contTouchLeft
        exitTouchRight  = contTouchRight
        exitTouchTop    = contTouchTop + offsetPx
        exitTouchBottom = contTouchBottom + offsetPx
    }



    fun onSurfaceSizeChanged(w: Int, h: Int) {
        screenWidth  = w
        screenHeight = h
    }


    // 한 번만 보이도록
    private var showing = false

    /** 외부에서 호출하면 게임오버 화면이 뜹니다 */
    fun show() {
        showing = true
    }

    fun hide() {
        showing = false
    }

    fun isShowing(): Boolean {
        return showing
    }

    override fun Update(dt: Float) {
        if (!showing) return
        // ② elapsed를 dt만큼 누적, 최대 duration
        elapsed = (elapsed + dt).coerceAtMost(duration)
    }
    override fun LateUpdate(dt: Float) {
        super.LateUpdate(dt)
        if (showing) RenderManager.Add_RenderObject(RenderManager.RenderGroup.BLEND, this)
    }

    // ① 페이드 인용 변수
    private var elapsed   = 0f       // 누적된 시간
    private val duration  = 1f       // 1초 동안 페이드 인

    override fun Render(): Boolean {
        if (!showing) return false
        shader.Use_Program()
        val uW    = shader.Get_UniformAttribute("u_worldMatrix")
        val uTex  = shader.Get_UniformAttribute("u_Texture")
        val uA    = shader.Get_UniformAttribute("u_Alpha")      // we’ll keep alpha=1
        val aP    = shader.Get_Attribute("a_Position")
        val aT    = shader.Get_Attribute("a_TexCoord")

        // (0) 버퍼 애트리뷰트 위치 바인딩
        vibuffer.vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aP)
        GLES20.glVertexAttribPointer(aP, 3, GLES20.GL_FLOAT, false, 0, vibuffer.vertexBuffer)
        vibuffer.texCoordBuffer.position(0)
        GLES20.glEnableVertexAttribArray(aT)
        GLES20.glVertexAttribPointer(aT, 2, GLES20.GL_FLOAT, false, 0, vibuffer.texCoordBuffer)


        // (1) GameOver 로고
        TransformCom.position[0] = posLogo[0]
        TransformCom.position[1] = posLogo[1]
        TransformCom.scale[0]    = size
        TransformCom.scale[1]    = size
        TransformCom.LateUpdate(0f)
        GLES20.glUniformMatrix4fv(uW, 1, false, TransformCom.SRP, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(uTex, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texLogo.textureID[0])
        GLES20.glUniform1f(uA, 1f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // (2) Continue 버튼
        TransformCom.position[0] = posCont[0]
        TransformCom.position[1] = posCont[1] - 0.1f
        TransformCom.scale[0]    = 0.3f
        TransformCom.scale[1]    = 0.3f
        TransformCom.LateUpdate(0f)
        GLES20.glUniformMatrix4fv(uW, 1, false, TransformCom.SRP, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCont.textureID[0])
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // (3) Exit 버튼
        TransformCom.position[0] = posExit[0]
        TransformCom.position[1] = posExit[1] - 0.1f
        TransformCom.scale[0]    = 0.3f
        TransformCom.scale[1]    = 0.3f
        TransformCom.LateUpdate(0f)
        GLES20.glUniformMatrix4fv(uW, 1, false, TransformCom.SRP, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texExit.textureID[0])
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        return true
    }



    override fun OnTouch(event: MotionEvent?) {
        event ?: return
        if (!showing) return

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                when {
                    // Continue 버튼
                    event.x in contTouchLeft..contTouchRight &&
                            event.y in contTouchTop..contTouchBottom -> {
                        Log.e("GameOver", "Continue 버튼 누름")
                        onContinue?.invoke()     // 여기에 콜백 호출!
                    }
                    // Exit 버튼
                    event.x in exitTouchLeft..exitTouchRight &&
                            event.y in exitTouchTop..exitTouchBottom -> {
                        Log.e("GameOver", "Exit 버튼 누름")
                        onExit?.invoke()         // 여기에 콜백 호출!
                    }
                    else -> {
                        Log.e("GameOver", "버튼이 아닌 곳을 터치함 (x=${event.x}, y=${event.y})")
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.e("GameOver", "버튼 해제 또는 터치 취소")
            }
        }
    }

}
