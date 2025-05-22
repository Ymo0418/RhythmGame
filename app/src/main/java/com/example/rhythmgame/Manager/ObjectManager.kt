package com.example.rhythmgame.Manager

import android.util.Log
import androidx.constraintlayout.helper.widget.Layer
import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Base.Object

object ObjectManager : Base() {
    public enum class LayerType {
        CAMERA, PLAYER, MONSTER, BACKGROUND
    }
    private val LayerOrder = listOf(
        LayerType.CAMERA, LayerType.PLAYER, LayerType.MONSTER, LayerType.BACKGROUND
    )

    private val Layers = mutableMapOf<LayerType, MutableList<Object>>()

    init {
        for(layer in LayerType.entries) {
            Log.e("dd", "$layer")
            Layers[layer] = mutableListOf()
        }
    }

    override fun Update(fTimeDelta: Float) {
        for(layer in LayerOrder) {
            Layers[layer]?.forEach {
                it.Update(fTimeDelta)
            }
        }
    }

    override fun LateUpdate(fTimeDelta: Float) {
        for(layer in LayerOrder) {
            Layers[layer]?.forEach {
                it.LateUpdate(fTimeDelta)
            }
        }
    }

    override fun Render(): Boolean {
        for(layer in LayerOrder) {
            Layers[layer]?.forEach {
                if(!it.Render())
                    return false
            }
        }
        return true
    }

    public fun Add_Object(layer: LayerType, target: Object) {
        Layers[layer]?.add(target)
    }
}