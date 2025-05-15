package com.example.rhythmgame

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.example.rhythmgame.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import java.nio.*

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var programHandle = 0
    private var textureId = 0

    // 정점 좌표 (x, y, z)
    private val vertexData = floatArrayOf(
        -1f,  1f, 0f,
        -1f, -1f, 0f,
        1f,  1f, 0f,
        1f, -1f, 0f
    )
    // 텍스처 좌표 (u, v)
    private val texCoordData = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 0f,
        1f, 1f
    )
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer

    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        // 배경색 검은색으로 설정
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        // 버퍼 준비
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertexData)
                position(0)
            }
        texCoordBuffer = ByteBuffer.allocateDirect(texCoordData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(texCoordData)
                position(0)
            }

        // 쉐이더 컴파일·링크
        val vs = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fs = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        programHandle = ShaderUtils.createProgram(vs, fs)

        // 텍스처 로딩 (res/drawable/my_texture.png)
        textureId = TextureHelper.loadTexture(context, R.drawable.my_image)
    }

    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(programHandle)

        // 속성 위치 가져오기
        val posLoc = GLES20.glGetAttribLocation(programHandle, "a_Position")
        val texLoc = GLES20.glGetAttribLocation(programHandle, "a_TexCoord")
        val samplerLoc = GLES20.glGetUniformLocation(programHandle, "u_Texture")

        // 정점 데이터 연결
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // 텍스처 좌표 연결
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // 텍스처 바인딩
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(samplerLoc, 0)

        // 사각형 그리기 (Triangle Strip)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)
    }

    companion object {
        // 매우 간단한 버텍스 쉐이더
        private const val vertexShaderCode = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            varying vec2 v_TexCoord;
            void main() {
              gl_Position = a_Position;
              v_TexCoord = a_TexCoord;
            }
        """
        // 텍스처 샘플링 프래그먼트 쉐이더
        private const val fragmentShaderCode = """
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform sampler2D u_Texture;
            void main() {
              gl_FragColor = texture2D(u_Texture, v_TexCoord);
            }
        """
    }
}
