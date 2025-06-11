package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Component.Component

object ComponentManager : Base() {
    //컴포넌트 원본들 <tag, 원본컴포넌트> 형식으로 보관됨
    private val Components: HashMap<String, Component> = hashMapOf()

    //새로운 컴포넌트 원본 추가
    fun Register_Component(tag: String, comp: Component) : Boolean {
        if(Components.containsKey(tag))
            return false

        Components[tag] = comp
        return true
    }

    //컴포넌트 원본을 복사해 반환
    fun Clone_Component(tag: String) : Component? {
        if(!Components.containsKey(tag))
            return null

        return Components[tag]?.Clone()
    }
}