package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Component.Component

object ComponentManager : Base() {
    val Components: HashMap<String, Component> = hashMapOf()

    fun Register_Component(tag: String, comp: Component) : Boolean {
        if(Components.containsKey(tag))
            return false

        Components[tag] = comp
        return true
    }

    fun Clone_Component(tag: String) : Component? {
        if(!Components.containsKey(tag))
            return null

        return Components[tag]?.Clone()
    }
}