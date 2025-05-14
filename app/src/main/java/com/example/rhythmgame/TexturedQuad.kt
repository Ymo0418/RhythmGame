package com.example.rhythmgame

import android.opengl.GLES20.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TexturedQuad {
    // 2D 정점 좌표 및 텍스처 좌표 (X, Y, S, T)
    private val vertexData = floatArrayOf(
        -1f,  1f,  0f, 0f,
        -1f, -1f,  0f, 1f,
        1f,  1f,  1f, 0f,
        1f, -1f,  1f, 1f
    )
    private val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertexData.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply { put(vertexData).position(0) }

    fun draw(program: Int) {
        val posHandle = glGetAttribLocation(program, "aPosition")
        val texHandle = glGetAttribLocation(program, "aTexCoord")
        val uniHandle = glGetUniformLocation(program, "uTexture")

        glEnableVertexAttribArray(posHandle)
        glVertexAttribPointer(posHandle, 2, GL_FLOAT, false, 16, vertexBuffer)

        vertexBuffer.position(2)
        glEnableVertexAttribArray(texHandle)
        glVertexAttribPointer(texHandle, 2, GL_FLOAT, false, 16, vertexBuffer)

        glUniform1i(uniHandle, 0)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

        glDisableVertexAttribArray(posHandle)
        glDisableVertexAttribArray(texHandle)
    }
}
