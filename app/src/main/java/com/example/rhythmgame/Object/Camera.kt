package com.example.rhythmgame.Object

import android.opengl.Matrix
import com.example.rhythmgame.Base.GameObject

object Camera : GameObject() {

    var fovy    = 45f
    var aspect  = 800f/300f
    var near    = 0.1f
    var far     = 1000f
    val Proj    = FloatArray(16)
    val View    = FloatArray(16)
    val ViewProj= FloatArray(16)

    init {
        TransformCom.position[2] = 5f
    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        super.LateUpdate(fTimeDelta)

        Matrix.invertM(View, 0, TransformCom.SRP, 0);
        Matrix.setIdentityM(Proj, 0)
        Matrix.perspectiveM(Proj, 0, fovy, aspect, near, far)

        Matrix.multiplyMM(ViewProj, 0, Proj, 0, View, 0)
    }

    public fun Get_ViewProj(): FloatArray {
        return ViewProj
    }
}