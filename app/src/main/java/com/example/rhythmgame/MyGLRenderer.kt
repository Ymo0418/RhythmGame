package com.example.rhythmgame

import android.content.Context
import android.opengl.GLSurfaceView

import android.opengl.GLES20
import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Comp_VIBuffer
import com.example.rhythmgame.Manager.ComponentManager
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Object.TestMario

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        //GLES20.glClearColor(0f, 0f, 0f, 1f)

        ComponentManager.Register_Component("TransformCom", Comp_Transform())
        ComponentManager.Register_Component("Texture_Image", Comp_Texture(context, R.drawable.my_image))
        ComponentManager.Register_Component("VIBufferCom", Comp_VIBuffer())
        ComponentManager.Register_Component("ShaderCom", Comp_Shader(context.getString(R.string.VS_VtxPosTex)
                                                                        , context.getString(R.string.FS_VtxPosTex)))
        //val joystick = Joystick()
        //ObjectManager.Add_Object(ObjectManager.LayerType.UI, joystick)

        val Mario = TestMario()
        Mario.TransformCom.position[1] = 0.5f
        //[0]이 x좌표 [1]이 Y좌표
        ObjectManager.Add_Object(ObjectManager.LayerType.PLAYER, Mario)
    }

    //GLSurfaceView의 크기가 변경되거나 화면 방향이 전환될 때 호출
    //뷰포트 및 투영 매트릭스 등을 설정하는 데 사용
    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        ObjectManager.Update(0.016f)
        ObjectManager.LateUpdate(0.016f)
        ObjectManager.Render()
    }

    fun SetJoystickPosition(x:Float, y:Float){
        val UILayer = ObjectManager.Get_Objects(ObjectManager.LayerType.PLAYER) // UI로 수정 해야 조이스틱 사용가능
        UILayer.first().TransformCom.position[0] = x
        UILayer.first().TransformCom.position[1] = y

    }
}
