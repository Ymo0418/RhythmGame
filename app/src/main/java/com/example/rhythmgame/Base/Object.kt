package com.example.rhythmgame.Base

import android.util.Log
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Component
import com.example.rhythmgame.Manager.ComponentManager

open class Object : Base() {
    val Components: MutableList<Component> = mutableListOf()
    lateinit var TransformCom: Comp_Transform

    init {
        TransformCom = Add_Component("TransformCom") as Comp_Transform
        Components.add(TransformCom)
    }

    override fun Update(fTimeDelta: Float) {
        for(comp in Components) {
            comp.Update(fTimeDelta)
        }
    }
    override fun LateUpdate(fTimeDelta: Float) {
        for (comp in Components) {
            comp.LateUpdate(fTimeDelta)
        }
    }

    protected fun Add_Component(tag: String) : Component {
        val comp = ComponentManager.Clone_Component(tag)
        if(comp == null) {
            Log.e("Component", "Could not find component $tag:")
            throw RuntimeException("Find component failed")
        }

        return comp
    }
}