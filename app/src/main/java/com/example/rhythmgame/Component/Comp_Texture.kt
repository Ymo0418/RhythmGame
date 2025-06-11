package com.example.rhythmgame.Component

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils

open class Comp_Texture(context: Context, resId: Int) : Component() {
    public lateinit var textureID : IntArray

    init {
        textureID = IntArray(1)
        GLES20.glGenTextures(1, textureID, 0)
        if (textureID[0] == 0)
            throw RuntimeException("Failed to generate texture")

        // 비트맵 로딩
        val options = BitmapFactory.Options().apply { inScaled = false }
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)

        // 바인딩 및 파라미터 세팅
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // 비트맵 → OpenGL 텍스처 업로드
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        // 바인딩 해제
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    public fun Bind_Texture() {

    }

    override fun Clone(): Comp_Texture {
        return this
    }
}