package com.example.rhythmgame.Component

import com.example.rhythmgame.Manager.RenderManager

class Comp_Collider(): Component() {
    var left = 0f
    var top = 0f
    var right = 0f
    var bottom = 0f
    var parentPos = floatArrayOf()

    var value = 0
    var isCollide = false
    var collideInfo = 0

    init {
    }

    public fun SetColliderInfo(parentTransform: Comp_Transform, width:Float, height:Float,
                               offsetX:Float = 0f, offsetY : Float = 0f) {
        parentPos = parentTransform.position
        left = offsetX - width / 2f
        right = offsetX + width / 2f
        top = offsetY + height / 2f
        bottom = offsetY - height / 2f
    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)
    }

    override fun Render(): Boolean {
        return super.Render()
    }

    override fun Clone(): Component {
        return Comp_Collider()
    }
}