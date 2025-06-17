package com.example.rhythmgame.Component

import android.opengl.Matrix
import android.util.Log

class Comp_Transform : Component() {
    var position = floatArrayOf(0f, 0f, 0f)
    var rotation = floatArrayOf(0f, 0f, 0f)
    var scale = floatArrayOf(1f, 1f, 1f)
    val SRP = FloatArray(16)

    override fun LateUpdate(fTimeDelta: Float) {
        Matrix.setIdentityM(SRP, 0)

        //이동
        Matrix.translateM(SRP, 0, position[0], position[1], position[2])

        //자전
        Matrix.rotateM(SRP, 0, rotation[0], 1f, 0f, 0f)
        Matrix.rotateM(SRP, 0, rotation[1], 0f, 1f, 0f)
        Matrix.rotateM(SRP, 0, rotation[2], 0f, 0f, 1f)

        //크기
        Matrix.scaleM(SRP, 0, scale[0], scale[1], scale[2])
    }

    override fun Clone(): Comp_Transform {
        val newComponent = Comp_Transform()
        newComponent.position = this.position.copyOf()
        newComponent.rotation = this.rotation.copyOf()
        newComponent.scale = this.scale.copyOf()
        System.arraycopy(this.SRP, 0, newComponent.SRP, 0, this.SRP.size)
        return newComponent
    }
}