package com.example.rhythmgame.Component

import com.example.rhythmgame.Base.Base

abstract class Component : Base() {
    override fun Update(fTimeDelta: Float) { }
    override fun LateUpdate(fTimeDelta: Float) { }
    override fun Render() : Boolean { return super.Render() }
    abstract fun Clone() : Component
}