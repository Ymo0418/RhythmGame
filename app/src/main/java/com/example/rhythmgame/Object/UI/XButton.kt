package com.example.rhythmgame.Object.UI

import android.util.Log
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.RenderManager

//UIObject 상속받기
class XButton: UIObject() {
    private val texCom     = Add_Component("TextureCom_Joystick")   as Comp_Texture
    private val vibuffer   = Add_Component("VIBufferCom")           as Comp_VIBuffer
    private val shader     = Add_Component("ShaderCom_UI")          as Comp_Shader

    init {
        Components.addAll(listOf(vibuffer, shader, texCom))
        TransformCom.scale = floatArrayOf(0.1f, 0.1f, 1f)
    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)
    }
    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)
        RenderManager.Add_RenderObject(RenderManager.RenderGroup.UI, this)
    }
    override fun Render(): Boolean {
        return super.Render()
    }

    //이 함수 override하면 입력받을수있음
    override fun OnTouch(event: MotionEvent?) {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("d", "dddddd")
            }
        }
    }
}