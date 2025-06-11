package com.example.rhythmgame.Manager

import android.opengl.GLES20
import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Base.GameObject

object RenderManager : Base() {
    public enum class RenderGroup {
        NONBLEND, BLEND, UI;
    }

    private val RenderOrder = listOf(
        RenderGroup.NONBLEND, RenderGroup.BLEND, RenderGroup.UI
    )

    private val RenderObjects = mutableMapOf<RenderGroup, MutableList<GameObject>>()

    init {
        for (group in RenderGroup.entries) {
            RenderObjects[group] = mutableListOf()
        }
    }

    private fun Render_NonBlend(): Boolean {
        GLES20.glDisable(GLES20.GL_BLEND)
        RenderObjects[RenderGroup.NONBLEND]?.forEach {
            if(!it.Render())
                return false
        }
        return true
    }

    private fun Render_Blend(): Boolean {
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        RenderObjects[RenderGroup.BLEND]?.forEach {
            if(!it.Render())
                return false
        }
        return true
    }

    private fun Render_UI(): Boolean {
        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        RenderObjects[RenderGroup.UI]?.forEach {
            if(!it.Render())
                return false
        }
        return true
    }

    private fun Clear_RenderGroup() {
        for(objects in RenderObjects.values) {
            objects.clear()
        }
    }

    override fun Render(): Boolean {
        if(!Render_NonBlend())
            return false

        if(!Render_Blend())
            return false

        if(!Render_UI())
            return false

        Clear_RenderGroup()

        return true
    }

    public fun Add_RenderObject(group: RenderGroup, target: GameObject) {
        RenderObjects[group]?.add(target)
    }
}
