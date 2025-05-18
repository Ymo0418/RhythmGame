package com.example.rhythmgame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.GLSurfaceView
import android.os.SystemClock
import java.nio.*

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var programHandle  = 0
    private var textureId      = 0

    private var offsetX        = 0f
    private var offsetY        = 0f
    private var offsetHandle   = 0

    // 시트 크기: 5열 × 2행
    private val spriteCols     = 5
    private val spriteRows     = 2

    // 매 100ms마다 다음 열로 이동
    private var regionCol      = 0          // var 으로 변경!
    private val regionRow      = 0          // 고정된 행
    private var lastFrameTime  = 0L
    private val framePeriod    = 100L       // 100ms

    private val vertexData = floatArrayOf(
        -1f,  1f, 0f,
        -1f, -1f, 0f,
        1f,  1f, 0f,
        1f, -1f, 0f
    )
    private lateinit var vertexBuffer   : FloatBuffer
    private lateinit var texCoordBuffer : FloatBuffer

    override fun onSurfaceCreated(
        gl: javax.microedition.khronos.opengles.GL10?,
        config: javax.microedition.khronos.egl.EGLConfig?
    ) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        // 정점 버퍼 생성
        vertexBuffer = ByteBuffer
            .allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertexData)
                position(0)
            }

        // UV 버퍼 생성
        texCoordBuffer = ByteBuffer
            .allocateDirect(4 * 2 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        // 쉐이더 컴파일 & 프로그램 생성
        val vs = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER,   vertexShaderCode)
        val fs = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        programHandle = ShaderUtils.createProgram(vs, fs)

        // u_Offset 위치 캐시
        offsetHandle = GLES20.glGetUniformLocation(programHandle, "u_Offset")

        // 뒤집힌 텍스처 로딩
        textureId = loadFlippedTexture(context, R.drawable.img)

        // 타이밍 초기화 & UV 계산
        lastFrameTime = SystemClock.uptimeMillis()
        updateTexCoords()
    }

    override fun onSurfaceChanged(
        gl: javax.microedition.khronos.opengles.GL10?,
        width: Int, height: Int
    ) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        // 1) 0.1초마다 regionCol 증가 → wrap
        val now = SystemClock.uptimeMillis()
        if (now - lastFrameTime >= framePeriod) {
            regionCol = (regionCol + 1) % spriteCols
            updateTexCoords()
            lastFrameTime += framePeriod
        }

        // 2) 화면 클리어 & 프로그램 사용
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(programHandle)

        // a_Position 바인딩
        val posLoc = GLES20.glGetAttribLocation(programHandle, "a_Position")
        GLES20.glEnableVertexAttribArray(posLoc)
        GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // a_TexCoord 바인딩
        val texLoc = GLES20.glGetAttribLocation(programHandle, "a_TexCoord")
        GLES20.glEnableVertexAttribArray(texLoc)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // u_Offset 전달
        GLES20.glUniform2f(offsetHandle, offsetX, offsetY)

        // 텍스처 바인딩
        val samplerLoc = GLES20.glGetUniformLocation(programHandle, "u_Texture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(samplerLoc, 0)

        // 드로우
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // 클린업
        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)
    }

    /** 비트맵을 수직 뒤집어 업로드 */
    private fun loadFlippedTexture(context: Context, resId: Int): Int {
        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        if (texIds[0] == 0) throw RuntimeException("Failed to generate texture")

        val opts = BitmapFactory.Options().apply { inScaled = false }
        val bmp  = BitmapFactory.decodeResource(context.resources, resId, opts)
        val m    = Matrix().apply { preScale(1f, -1f) }
        val flipped = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, false)
        bmp.recycle()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, flipped, 0)
        flipped.recycle()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return texIds[0]
    }

    /** regionCol/regionRow에 맞춘 UV 계산 */
    private fun updateTexCoords() {
        val fw = 1f / spriteCols
        val fh = 1f / spriteRows

        val u0 = regionCol * fw
        val u1 = u0 + fw

        // “맨 위 행” 기준으로 v 계산
        val v1 = 1f - regionRow * fh
        val v0 = v1 - fh

        val coords = floatArrayOf(
            u0, v1,
            u0, v0,
            u1, v1,
            u1, v0
        )
        texCoordBuffer.apply {
            clear()
            put(coords)
            position(0)
        }
    }

    companion object {
        private const val vertexShaderCode = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            uniform   vec2 u_Offset;
            varying   vec2 v_TexCoord;
            void main() {
                gl_Position = a_Position + vec4(u_Offset, 0.0, 0.0);
                v_TexCoord  = a_TexCoord;
            }
        """
        private const val fragmentShaderCode = """
            precision mediump float;
            varying   vec2 v_TexCoord;
            uniform   sampler2D u_Texture;
            void main() {
                gl_FragColor = texture2D(u_Texture, v_TexCoord);
            }
        """
    }
}
