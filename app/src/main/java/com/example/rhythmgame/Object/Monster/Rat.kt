package com.example.rhythmgame.Object.Monster

import com.example.rhythmgame.Component.Comp_Texture
import com.example.rhythmgame.Component.Comp_Transform

class Rat(playerTrans: Comp_Transform): Monster(playerTrans) {
    init {
        moveCount = 3
        speed = 0.01f
        stateSpriteCounts = intArrayOf(6, 6)
        TransformCom.scale = floatArrayOf(0.4f, 0.4f, 1f)
        ColliderCom.SetColliderInfo(TransformCom, 1, 0.3f, 0.1f)

        TextureComs = arrayOf(
            Add_Component("TextureCom_Rat_Idle") as Comp_Texture,
            Add_Component("TextureCom_Rat_Run") as Comp_Texture
        )

        for(textureCom in TextureComs)
            Components.add(textureCom)
    }

    override fun Update(fTimeDelta: Float) {
        super.Update(fTimeDelta)

        if(direction.isNotEmpty()){
            if(direction[0] > 0f)
                TransformCom.rotation[1] = 0f
            else if(direction[0] < 0f)
                TransformCom.rotation[1] = 180f
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
    }
}