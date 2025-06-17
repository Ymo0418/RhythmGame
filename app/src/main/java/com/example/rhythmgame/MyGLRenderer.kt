package com.example.rhythmgame

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLES20
import android.util.Log
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
import com.example.rhythmgame.Manager.UIManager
import com.example.rhythmgame.Manager.UIManager.joystick
import com.example.rhythmgame.Object.Camera
import com.example.rhythmgame.Object.JustRenderObject
import com.example.rhythmgame.Object.Player
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.UI.GameOver
import com.example.rhythmgame.Object.Monster
import com.example.rhythmgame.Object.UI.HP
import com.example.rhythmgame.Object.UI.UIObject
import com.example.rhythmgame.Object.UI.XButton
import com.example.rhythmgame.Object.UI.YButton
import javax.microedition.khronos.opengles.GL10

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
    }


    //GLSurfaceView의 크기가 변경되거나 화면 방향이 전환될 때 호출
    //뷰포트 및 투영 매트릭스 등을 설정하는 데 사용
    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)


        UIManager.joystick?.onSurfaceSizeChanged(width, height)
        UIManager.hp?.onSurfaceSizeChanged(width, height)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // 정상 게임 루프

        if(!UIManager.GetGameOverShowing() && UIManager.isHpDead()) {
            UIManager.SetGameOverShowing()
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        SoundManager.Update(0.016f)
        CollisionManager.ResetCollideInfo() //이전프레임 충돌정보 초기화

        ObjectManager.Update(0.016f)
        CollisionManager.Update(0.016f)

        ObjectManager.LateUpdate(0.016f)
        RenderManager.Render()

    }

    public fun OnTouchEvent(event: MotionEvent?) {

        // GameOverUI만 터치 처리 (다른 UI는 무시)
//        if (UIManager.isHpDead()) {
//            Log.e("OnTouchEvent", "gameOverUI error")
//
//            gameOverUI.OnTouch(event)
//            return
//        }

        val UIObjects = ObjectManager.Get_Objects(ObjectManager.LayerType.UI)
        for (ui in UIObjects) {
            if(ui is UIObject)
                ui.OnTouch(event)
        }
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

        ComponentManager.Register_Component("TextureCom_Player_Idle", Comp_Texture(context, R.drawable.player_idle))
        ComponentManager.Register_Component("TextureCom_Player_Walk", Comp_Texture(context, R.drawable.player_walk))
        ComponentManager.Register_Component("TextureCom_Field", Comp_Texture(context, R.drawable.field))
        ComponentManager.Register_Component("TextureCom_Joystick", Comp_Texture(context, R.drawable.joystickmain))
        ComponentManager.Register_Component("TextureCom_Joystick2", Comp_Texture(context, R.drawable.joystick2))

        ComponentManager.Register_Component("Texture_xButton", Comp_Texture(context, R.drawable.x_button))
        ComponentManager.Register_Component("Texture_yButton", Comp_Texture(context, R.drawable.y_button))
        ComponentManager.Register_Component("Texture_HP", Comp_Texture(context, R.drawable.hp_img))
        ComponentManager.Register_Component("Texture_GameOver", Comp_Texture(context, R.drawable.gameover_img))
        ComponentManager.Register_Component("Texture_Continue", Comp_Texture(context, R.drawable.continue_img))
        ComponentManager.Register_Component("Texture_Exit_Game", Comp_Texture(context, R.drawable.exit_img))
    }

    fun restartGame() {
        // HP, UI, 플레이어 등 상태 초기화
        UIManager.isGameOver = false
        UIManager.gameOverTimer = -1f
        UIManager.hp?.reset()
        UIManager.Gameover?.hide()
        // 필요하다면 플레이어나 몬스터 등도 초기화
        // 예: Player/Monster의 위치와 상태도 리셋 필요시 추가
        // ObjectManager에서 각 Layer 객체들을 모두 초기화/재생성하는 식으로 구현 가능

        // 예: Player 재생성 (더 세밀한 리셋이 필요하면 Player에 reset() 추가)
        // ObjectManager.Add_Object(ObjectManager.LayerType.PLAYER, Player())
        // ...
    }


    private fun Ready_UI() {
        //UI추가하면 여기서 만들어야함

        val joystick = Joystick()
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, joystick)
        UIManager.SetJoystick(joystick)

        val joystick2 = Joystick()
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, joystick2)
        UIManager.SetJoystick2(joystick2)

        //객체 만들고
        val xButton = XButton(context)
        //오브젝트매니저에 넣고
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, xButton)
        //다른 오브젝트가 이 UI의 값을 사용할수있도록 매니저에 등록
        UIManager.SetXButton(xButton)

        val yButton = YButton(context)
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, yButton)
        UIManager.SetXButton(yButton)

        val hp = HP()
        //오브젝트매니저에 넣고
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, hp)
        //다른 오브젝트가 이 UI의 값을 사용할수있도록 매니저에 등록
        UIManager.SetHP(hp)


//        val gameOver = GameOver(context)
//        ObjectManager.Add_Object(ObjectManager.LayerType.UI, gameOver)
//        UIManager.SetGameOver(gameOver)
        val gameOverUI = GameOver(context)
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, gameOverUI)
        UIManager.SetGameOver(gameOverUI)


        gameOverUI.onContinue = {
            restartGame()
        }
        gameOverUI.onExit = {
            // context는 Activity에서 전달된 것이므로 캐스팅
            if (context is Activity) {
                (context as Activity).finish()
            }
            // 혹은 강제 종료는 System.exit(0)도 가능하나 finish() 권장
        }

    }

    private fun Ready_Level() {
        ObjectManager.Add_Object(ObjectManager.LayerType.CAMERA, Camera)
        ObjectManager.Add_Object(ObjectManager.LayerType.PLAYER, Player())
        ObjectManager.Add_Object(ObjectManager.LayerType.MONSTER, Monster())
        ObjectManager.Add_Object(ObjectManager.LayerType.BACKGROUND, JustRenderObject("TextureCom_Field",
            floatArrayOf(5f,5f,5f), floatArrayOf(0f,0f,0f), floatArrayOf(0f,0f,0f), RenderManager.RenderGroup.NONBLEND))
    }
}