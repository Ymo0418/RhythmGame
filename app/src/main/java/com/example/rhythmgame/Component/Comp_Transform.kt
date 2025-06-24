package com.example.rhythmgame.Component

import android.opengl.Matrix
import kotlin.math.sqrt

class Comp_Transform : Component() {
    var position = floatArrayOf(0f, 0f, 0f)
    var rotation = floatArrayOf(0f, 0f, 0f)
    var scale = floatArrayOf(1f, 1f, 1f)
    val SRP = floatArrayOf(1f, 0f, 0f, 0f,
                           0f, 1f, 0f, 0f,
                           0f, 0f, 1f, 0f,
                           0f, 0f, 0f, 1f,)

    override fun Clone(): Comp_Transform {
        val newComponent = Comp_Transform()
        newComponent.position = this.position.copyOf()
        newComponent.rotation = this.rotation.copyOf()
        newComponent.scale = this.scale.copyOf()
        System.arraycopy(this.SRP, 0, newComponent.SRP, 0, this.SRP.size)
        return newComponent
    }

    public fun Distance2D(other: Comp_Transform): Float {
        val dx = position[0] - other.position[0]
        val dy = position[1] - other.position[1]
        return sqrt(dx * dx + dy * dy)
    }

    public fun BuildMatrix() {
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
}