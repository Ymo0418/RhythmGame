package com.example.rhythmgame.Base

abstract class Base {
    open fun Update(fTimeDelta : Float) { }
    open fun LateUpdate(fTimeDelta: Float) { }
    open fun Render() : Boolean { return true }
}