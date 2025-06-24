package com.example.rhythmgame.Object.Monster

import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Manager.CollisionManager
import java.nio.Buffer

class Mino(playerTrans: Comp_Transform): Monster(playerTrans) {
    init {
        hp = 3
        moveCount = 2
        speed = 0.02f
        stateSpriteCounts = intArrayOf(16, 12)
        val multi = 0.006f
        TransformCom.scale = floatArrayOf(288f * multi, 160f * multi, 1f)
        ColliderCom.SetColliderInfo(TransformCom, 1, 1, 1.0f, 0.9f, 0f, -0.4f)

        TextureComs = arrayOf(
            Add_Component("TextureCom_Mino_Idle") as Comp_Texture,
            Add_Component("TextureCom_Mino_Run") as Comp_Texture
        )

        for(textureCom in TextureComs)
            Components.add(textureCom)
    }

    override fun Update(fTimeDelta: Float) {
        if(direction.isNotEmpty()){
            if(direction[0] > 0f)
                TransformCom.rotation[1] = 180f
            else if(direction[0] < 0f)
                TransformCom.rotation[1] = 0f
        }

        when(curState) {
            0 -> {
                if(currentFrame == 0 && preFrame == stateSpriteCounts[curState] - 1) {
                    ++nonMoveCount
                }

                if(nonMoveCount == moveCount) {
                    curState = 1
                    nonMoveCount = 0
                    CalcDirection()
                }
            }
            1 -> {
                if(stateSpriteCounts[curState] - 1 == currentFrame)
                    curState = 0

                TransformCom.position[0] += direction[0] * speed
                TransformCom.position[1] += direction[1] * speed
            }
        }

        super.Update(fTimeDelta)
    }

    override fun LateUpdate(fTimeDelta: Float) {
        if(ColliderCom.isCollide)
        {
            hp -= ColliderCom.collideInfo
        }

        if(hp <= 0)
            isDead = true

        CollisionManager.RegisterCollider(CollisionManager.ColliderGroup.MONSTER, ColliderCom)

        super.LateUpdate(fTimeDelta)
    }

    override fun Render(): Boolean {
        return super.Render()
    }
}