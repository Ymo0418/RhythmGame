package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView

import android.opengl.GLES20
import android.util.Log
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.ComponentManager
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.TestMario
import java.nio.file.Files.move


class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    // [1] 매 프레임 Mario 이동용 방향 벡터
    var moveDirX = 0f
    var moveDirY = 0f

    // [2] Mario 속도 스칼라 (드래그 벡터 크기 비례)
    var moveSpeed = 0f

    // [3] 속도 최대값 (NDC 단위/sec)
    val maxSpeed = 4.0f

    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        // 알파 있는 PNG 지원 위해
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        ComponentManager.Register_Component("TransformCom", Comp_Transform())
        ComponentManager.Register_Component("Texture_Image", Comp_Texture(context, R.drawable.my_image))
        ComponentManager.Register_Component("VIBufferCom", Comp_VIBuffer())
        ComponentManager.Register_Component("ShaderCom", Comp_Shader(context.getString(R.string.VS_VtxPosTex)
                                                                        , context.getString(R.string.FS_VtxPosTex)))
        ComponentManager.Register_Component("Texture_Joystick", Comp_Texture(context, R.drawable.joystick2))


        val joystick = Joystick()
        ObjectManager.Add_Object(ObjectManager.LayerType.UI, joystick)

        val Mario = TestMario()
        //[0]이 x좌표 [1]이 Y좌표
        Mario.TransformCom.position[1] = 0.5f
        ObjectManager.Add_Object(ObjectManager.LayerType.PLAYER, Mario)

    }

    //GLSurfaceView의 크기가 변경되거나 화면 방향이 전환될 때 호출
    //뷰포트 및 투영 매트릭스 등을 설정하는 데 사용
    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // [6] 매 프레임 Mario 이동:
        //      방향(moveDirX, moveDirY) × 속도(moveSpeed) × Δ시간
        val deltaTime = 0.016f  // 약 60 FPS 가정
        val marioObj = ObjectManager
            .Get_Objects(ObjectManager.LayerType.PLAYER)
            .filterIsInstance<TestMario>()
            .firstOrNull()
        marioObj?.let {
            it.TransformCom.position[0] += moveDirX * moveSpeed * deltaTime
            it.TransformCom.position[1] += moveDirY * moveSpeed * deltaTime
        }
        ObjectManager.Update(0.016f)
        ObjectManager.LateUpdate(0.016f)
        ObjectManager.Render()
    }


    private var lastNx = 0f
    private var lastNy = 0f

    //터치 이벤트 로직을 전부 MyGLSurfaceView에서 처리하므로,
    //기존에 SetJoystickPosition으로 하던 호출은 모두 지우셔도 됩니다.
    //onSurfaceCreated에서는 여전히 조이스틱 오브젝트만 등록해 주세요:
    fun SetJoystickPosition(x:Float, y:Float){
        val UILayer  = ObjectManager.Get_Objects(ObjectManager.LayerType.UI)
        if (UILayer.isNotEmpty()) {
            val joystick = UILayer.first() as Joystick
            joystick.TransformCom.position[0] = x  // 또는 show(x,y) 시점에 move 만 호출하도록 수정
            joystick.TransformCom.position[1] = x
        }


        // 2) PLAYER 레이어의 마리오 꺼내서 위치 동기화
        val playerLayer = ObjectManager.Get_Objects(ObjectManager.LayerType.PLAYER).filterIsInstance<TestMario>()
        if (playerLayer.isNotEmpty()) {
            val mario = playerLayer.first()
            mario.TransformCom.position[0] = x    // X 좌표
            mario.TransformCom.position[1] = y    // Y 좌표
        }
    }
}
