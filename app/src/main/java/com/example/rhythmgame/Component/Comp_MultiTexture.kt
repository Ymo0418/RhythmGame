package com.example.rhythmgame.Component

import android.content.Context

class Comp_MultiTexture(context: Context, resId: Int, frameX: Int, frameY: Int): Comp_Texture(context, resId) {
    val frameX: Int
    val frameY: Int

    init {
        this.frameX = frameX
        this.frameY = frameY
    }

    override fun Clone(): Comp_Texture {
        return this
    }
}