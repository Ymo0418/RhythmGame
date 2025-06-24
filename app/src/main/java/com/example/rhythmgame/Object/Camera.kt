package com.example.rhythmgame.Object

import android.opengl.Matrix
import android.util.Log
import com.example.rhythmgame.Base.GameObject
import com.example.rhythmgame.Manager.ObjectManager

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
        val PlayerTrans = ObjectManager.Get_Objects(ObjectManager.LayerType.PLAYER).first().GetTransformComp()
        if(TransformCom.Distance2D(PlayerTrans)
            > 0.5f)
        {
            var x = lerp(TransformCom.position[0], PlayerTrans.position[0], 0.05f)
            var y = lerp(TransformCom.position[1], PlayerTrans.position[1], 0.05f)

            TransformCom.position[0] = x
            TransformCom.position[1] = y
        }

        super.Update(fTimeDelta)
    }

    private fun lerp(a: Float, b: Float, t:Float): Float {
        return a + (b-a) * t
    }

    override fun LateUpdate(fTimeDelta: Float) {
        Matrix.invertM(View, 0, TransformCom.SRP, 0);
        Matrix.setIdentityM(Proj, 0)
        Matrix.perspectiveM(Proj, 0, fovy, aspect, near, far)

        Matrix.multiplyMM(ViewProj, 0, Proj, 0, View, 0)

        super.LateUpdate(fTimeDelta)
        TransformCom.BuildMatrix()
    }

    public fun Get_ViewProj(): FloatArray {
        return ViewProj
    }
}