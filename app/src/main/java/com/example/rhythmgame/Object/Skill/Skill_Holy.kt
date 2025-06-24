package com.example.rhythmgame.Object.Skill

import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Manager.CollisionManager
import com.example.rhythmgame.Manager.RenderManager
import com.example.rhythmgame.Manager.SoundManager

class Skill_Holy(target: Comp_Transform): Skill(target) {

    init {
        duration = 0.4f
        spriteSize = 16f
        TransformCom.position = target.position.copyOf()
        TransformCom.scale = floatArrayOf(0.5f, 0.5f, 1f)
        TextureCom = Add_Component("TextureCom_Holy") as Comp_Texture

        ColliderCom.SetColliderInfo(TransformCom, 1, 1, 0.3f, 0.3f)

        Components.add(TextureCom)

        SoundManager.PlaySFX("Holy", 1f)
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