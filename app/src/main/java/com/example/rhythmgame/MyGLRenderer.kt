package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLES20
import android.view.MotionEvent
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_Collider
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.CollisionManager
import com.example.rhythmgame.Manager.ComponentManager
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager
import com.example.rhythmgame.Manager.SpawnManager
import com.example.rhythmgame.Manager.UIManager
import com.example.rhythmgame.Manager.UIManager.joystick
import com.example.rhythmgame.Object.Camera
import com.example.rhythmgame.Object.JustRenderObject
import com.example.rhythmgame.Object.Player
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.Monster.Rat
import com.example.rhythmgame.Object.Monster.Skill
import com.example.rhythmgame.Object.UI.HPBar
import com.example.rhythmgame.Object.UI.UIObject
import com.example.rhythmgame.Object.UI.XButton
import com.example.rhythmgame.Object.UI.YButton

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)

        Ready_Components()
        Ready_UI()
        Ready_Level()

        SoundManager.Init(context)
        SoundManager.PlayBGM(context, R.raw.stage_1)

        SoundManager.LoadSE(context, "Holy", R.raw.holy)
    }

    //GLSurfaceView의 크기가 변경되거나 화면 방향이 전환될 때 호출
    //뷰포트 및 투영 매트릭스 등을 설정하는 데 사용
    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        joystick?.onSurfaceSizeChanged(width, height)  //

    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {

        if (UIManager.isGameOver) {
            // 검은색 클리어만 하고 리턴
            GLES20.glClearColor(0f, 0f, 0f, 0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            return
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        SoundManager.Update(0.016f)
        UIManager.CheckTouch()
        SpawnManager.Update(0.016f)

        CollisionManager.Update(0.016f)
        ObjectManager.Update(0.016f)
        ObjectManager.LateUpdate(0.016f)
        CollisionManager.LateUpdate(0.016f)
        RenderManager.Render()
    }

    public fun OnTouchEvent(event: MotionEvent?) {
        UIManager.TouchEvent(event)
    }

    private fun Ready_Components() {
        ComponentManager.Register_Component("TransformCom", Comp_Transform())
        ComponentManager.Register_Component("ColliderCom", Comp_Collider())

        ComponentManager.Register_Component("VIBufferCom", Comp_VIBuffer())

        ComponentManager.Register_Component("ShaderCom_Plane", Comp_Shader(context.getString(R.string.VS_VtxPosTex)
                                                                        , context.getString(R.string.FS_VtxPosTex)))
        ComponentManager.Register_Component("ShaderCom_Anim", Comp_Shader(context.getString(R.string.VS_Anim)
                                                                        , context.getString(R.string.FS_VtxPosTex)))
        ComponentManager.Register_Component("ShaderCom_UI", Comp_Shader(context.getString(R.string.VS_UI)
                                                                        , context.getString(R.string.FS_UI)))

        ComponentManager.Register_Component("TextureCom_Slash", Comp_Texture(context, R.drawable.slashskill))
        ComponentManager.Register_Component("TextureCom_Holy", Comp_Texture(context, R.drawable.holyskill))
        ComponentManager.Register_Component("TextureCom_Rat_Idle", Comp_Texture(context, R.drawable.rat_idle))
        ComponentManager.Register_Component("TextureCom_Rat_Run", Comp_Texture(context, R.drawable.rat_run))
        ComponentManager.Register_Component("TextureCom_Player_Idle", Comp_Texture(context, R.drawable.player_idle))
        ComponentManager.Register_Component("TextureCom_Player_Walk", Comp_Texture(context, R.drawable.player_walk))
        ComponentManager.Register_Component("TextureCom_Field", Comp_Texture(context, R.drawable.field))
        ComponentManager.Register_Component("TextureCom_Joystick", Comp_Texture(context, R.drawable.joystickmain))
        ComponentManager.Register_Component("TextureCom_Joystick2", Comp_Texture(context, R.drawable.joystick2))

        ComponentManager.Register_Component("Texture_xButton", Comp_Texture(context, R.drawable.x_button))
        ComponentManager.Register_Component("Texture_yButton", Comp_Texture(context, R.drawable.y_button))
        ComponentManager.Register_Component("Texture_HP", Comp_Texture(context, R.drawable.hp_img))
    }

    private fun Ready_UI() {
        //UI추가하면 여기서 만들어야함

        val joystick = Joystick()
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, joystick)
        UIManager.SetJoystick(joystick)

        //객체 만들고
        val xButton = XButton(context)
        //오브젝트매니저에 넣고
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, xButton)
        //다른 오브젝트가 이 UI의 값을 사용할수있도록 매니저에 등록
        UIManager.SetXButton(xButton)

        val yButton = YButton(context)
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, yButton)
        UIManager.SetXButton(yButton)

        val hp = HPBar()
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, hp)
        UIManager.SetHPBar(hp)
    }

    private fun Ready_Level() {
        ObjectManager.Add_Object(ObjectManager.LayerType.CAMERA, Camera)
        val Player = Player()
        ObjectManager.Add_Object(ObjectManager.LayerType.PLAYER, Player)
        ObjectManager.Add_Object(ObjectManager.LayerType.MONSTER, Rat(Player.GetTransformComp()))
        ObjectManager.Add_Object(ObjectManager.LayerType.BACKGROUND, JustRenderObject("TextureCom_Field",
            floatArrayOf(5f,5f,5f), floatArrayOf(0f,0f,0f), floatArrayOf(0f,0f,0f), RenderManager.RenderGroup.NONBLEND))

    }
}