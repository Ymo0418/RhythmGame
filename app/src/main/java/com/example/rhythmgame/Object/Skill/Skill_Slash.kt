package com.example.rhythmgame.Object.Skill

import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Manager.CollisionManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager

class Skill_Slash(target: Comp_Transform, left: Boolean): Skill(target) {
    init {
        duration = 0.25f
        spriteSize = 6f
        TransformCom.position = target.position.copyOf()
        TransformCom.rotation[1] = if(left) 180f else 0f
        TransformCom.scale = floatArrayOf(1f, 1f, 1f)
        TextureCom = Add_Component("TextureCom_Slash") as Comp_Texture

        ColliderCom.SetColliderInfo(TransformCom, 1, 1, 2.0f, 0.6f, 0f, 0.1f)

        Components.add(TextureCom)

        SoundManager.PlaySFX("Slash", 1f)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        if(ColliderCom.isCollide) {
            bDone = true
        }

        if(!bDone)
            CollisionManager.RegisterCollider(CollisionManager.ColliderGroup.SKILL, ColliderCom)

        RenderManager.Add_RenderObject(RenderManager.RenderGroup.BLEND, this)

        super.LateUpdate(fTimeDelta)
    }
}