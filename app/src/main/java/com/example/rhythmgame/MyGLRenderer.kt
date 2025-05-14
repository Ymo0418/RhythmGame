package com.example.rhythmgame

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.example.rhythmgame.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var programHandle = 0
    private var textureId = 0
    private lateinit var quad: TexturedQuad

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 배경색 설정
        glClearColor(0f, 0f, 0f, 1f)

        // 셰이더 프로그램 컴파일·링크
        val vertexShader   = ShaderUtils.loadShader(GL_VERTEX_SHADER,   vertexShaderCode)
        val fragmentShader = ShaderUtils.loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        programHandle = glCreateProgram().also {
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
        }

        // 텍스처 로드
        textureId = loadTexture(context, R.drawable.my_image)

        // 화면에 그릴 사각형(쿼드) 생성
        quad = TexturedQuad()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 뷰포트 설정
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        // 프로그램 사용
        glUseProgram(programHandle)
        // 텍스처 바인딩
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        // 쿼드 그리기
        quad.draw(programHandle)
    }

    private fun loadTexture(context: Context, resId: Int): Int {
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)
        if (textures[0] == 0) throw RuntimeException("Texture 생성 실패")

        val options = BitmapFactory.Options().apply { inScaled = false }
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)

        // 텍스처 파라미터 설정
        glBindTexture(GL_TEXTURE_2D, textures[0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        return textures[0]
    }

    companion object {
        // 간단한 정점 셰이더
        private const val vertexShaderCode = """
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying   vec2 vTexCoord;
            void main() {
                vTexCoord = aTexCoord;
                gl_Position = aPosition;
            }
        """
        // 간단한 프래그먼트 셰이더
        private const val fragmentShaderCode = """
            precision mediump float;
            uniform sampler2D uTexture;
            varying vec2      vTexCoord;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """
    }
}
