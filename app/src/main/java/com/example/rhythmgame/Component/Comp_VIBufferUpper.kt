package com.example.rhythmgame.Component

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Comp_VIBufferUpper : Comp_VIBuffer() {
    init {
        vertexData = floatArrayOf(
        -1f, 2f, 0f,
        -1f, 0f, 0f,
        1f, 2f, 0f,
        1f, 0f, 0f
    )
    }

    override fun Clone(): Comp_VIBuffer {
        return Comp_VIBuffer()
    }
}