package com.example.rhythmgame.Base

import android.util.Log
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Component.Component
import com.example.rhythmgame.Manager.ComponentManager

open class GameObject : Base() {
    protected val Components: MutableList<Component> = mutableListOf()
    protected lateinit var TransformCom: Comp_Transform
    protected var isDead = false

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

    override fun Render(): Boolean {
        TransformCom.BuildMatrix()
        return super.Render()
    }

    protected fun Add_Component(tag: String) : Component {
        val comp = ComponentManager.Clone_Component(tag)
        if(comp == null) {
            Log.e("Component", "Could not find component $tag:")
            throw RuntimeException("Find component failed")
        }

        return comp
    }

    public fun GetTransformComp(): Comp_Transform {
        return TransformCom
    }

    public fun IsDead(): Boolean {
        return isDead
    }
}