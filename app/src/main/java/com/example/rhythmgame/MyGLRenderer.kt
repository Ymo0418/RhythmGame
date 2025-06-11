package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLES20
import android.util.Log
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.ComponentManager
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager
import com.example.rhythmgame.Manager.UIManager
import com.example.rhythmgame.Object.Camera
import com.example.rhythmgame.Object.JustRenderObject
import com.example.rhythmgame.Object.Player
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.UI.UIObject

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)

        Ready_Components()
        Ready_UI()

        ObjectManager.Add_Object(ObjectManager.LayerType.PLAYER, Player())
        ObjectManager.Add_Object(ObjectManager.LayerType.BACKGROUND, JustRenderObject("TextureCom_Field",
            floatArrayOf(5f,5f,5f), floatArrayOf(0f,0f,0f), floatArrayOf(0f,0f,0f), RenderManager.RenderGroup.NONBLEND))
        ObjectManager.Add_Object(ObjectManager.LayerType.CAMERA, Camera)

        SoundManager.Init(context)
        SoundManager.PlayBGM(context, R.raw.bpm100_first_beat)
    }

    //GLSurfaceView의 크기가 변경되거나 화면 방향이 전환될 때 호출
    //뷰포트 및 투영 매트릭스 등을 설정하는 데 사용
    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        ObjectManager.Update(0.016f)
        ObjectManager.LateUpdate(0.016f)
        RenderManager.Render()

        if(SoundManager.Get_ValidBeat())
            Log.d("Beat O", "O")
        else
            Log.e("Beat X", "X")
    }

    public fun OnTouchEvent(event: MotionEvent?) {
        val UIObjects = ObjectManager.Get_Objects(ObjectManager.LayerType.UI)
        for (ui in UIObjects) {
            if(ui is UIObject)
                ui.OnTouch(event)
        }
    }

    private fun Ready_Components() {
        ComponentManager.Register_Component("TransformCom", Comp_Transform())
        ComponentManager.Register_Component("VIBufferCom", Comp_VIBuffer())

        ComponentManager.Register_Component("ShaderCom_Plane", Comp_Shader(context.getString(R.string.VS_VtxPosTex)
                                                                        , context.getString(R.string.FS_VtxPosTex)))
        ComponentManager.Register_Component("ShaderCom_Anim", Comp_Shader(context.getString(R.string.VS_Anim)
                                                                        , context.getString(R.string.FS_VtxPosTex)))
        ComponentManager.Register_Component("ShaderCom_UI", Comp_Shader(context.getString(R.string.VS_UI)
                                                                        , context.getString(R.string.FS_UI)))

        ComponentManager.Register_Component("TextureCom_Player_Idle", Comp_Texture(context, R.drawable.player_idle))
        ComponentManager.Register_Component("TextureCom_Field", Comp_Texture(context, R.drawable.field))
        ComponentManager.Register_Component("TextureCom_Joystick", Comp_Texture(context, R.drawable.joystick2))
    }

    private fun Ready_UI() {
        val joystick = Joystick()
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, joystick)
        UIManager.SetJoystick(joystick)
    }
}