package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Base.GameObject

object ObjectManager : Base() {
    public enum class LayerType {
        CAMERA, PLAYER, MONSTER, BACKGROUND, UI
    }
    private val LayerOrder = listOf(
        LayerType.CAMERA, LayerType.PLAYER, LayerType.MONSTER, LayerType.BACKGROUND, LayerType.UI
    )

    private val Layers = mutableMapOf<LayerType, MutableList<GameObject>>()

    init {
        for(layer in LayerType.entries) {
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

    public fun Add_Object(layer: LayerType, target: GameObject) {
        Layers[layer]?.add(target)
    }

    public fun Get_Objects(layer: LayerType): MutableList<GameObject> {
        return Layers.getValue(layer)
    }


}