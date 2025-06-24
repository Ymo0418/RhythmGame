package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Component.Comp_Collider
import com.example.rhythmgame.Object.Monster.Monster
import com.example.rhythmgame.Object.Player
import kotlin.math.abs

object CollisionManager: Base() {
    public enum class ColliderGroup {
        PLAYER, MONSTER, WALL, SKILL
    }

    private val Colliders = mutableMapOf<ColliderGroup, MutableList<Comp_Collider>>()

    init {
        for(group in ColliderGroup.entries) {
            Colliders[group] = mutableListOf()
        }
    }

    public fun ResetCollideInfo() {
        for(group in ColliderGroup.entries) {
            for(collider in Colliders[group] ?: continue) {
                collider.isCollide = false
                collider.collideInfo = 0
            }
        }
    }

    override fun Update(fTimeDelta: Float) {
        ClearColliders()
    }

    override fun LateUpdate(fTimeDelta: Float) {
        IntersectGroup(ColliderGroup.MONSTER, ColliderGroup.PLAYER)
        IntersectGroupTrigger(ColliderGroup.MONSTER, ColliderGroup.SKILL)
    }

    public fun RegisterCollider(group: ColliderGroup, collider: Comp_Collider) {
        Colliders[group]?.add(collider)
    }

    private fun ClearColliders() {
        for(group in Colliders) {
            group.value.clear()
        }
    }

    private fun ClearGroup(group: ColliderGroup) {
        Colliders[group]?.clear()
    }

    private fun IntersectGroupTrigger(a: ColliderGroup, b: ColliderGroup) {
        for(ca in Colliders[a]!!) {
            for(cb in Colliders[b]!!) {
                IntersectTrigger(ca, cb)
            }
        }
    }
    private fun IntersectGroup(a: ColliderGroup, b: ColliderGroup) {
        for(ca in Colliders[a]!!) {
            for(cb in Colliders[b]!!) {
                Intersect(ca, cb)
            }
        }
    }

    private fun IntersectTrigger(a: Comp_Collider, b: Comp_Collider): Boolean {
        if(a.parentPos[0] + a.right < b.parentPos[0] + b.left)
            return false
        if(a.parentPos[1] + a.bottom > b.parentPos[1] + b.top)
            return false
        if(a.parentPos[0] + a.left > b.parentPos[0] + b.right)
            return false
        if(a.parentPos[1] + a.top < b.parentPos[1] + b.bottom)
            return false

        a.isCollide = true
        b.isCollide = true
        a.collideInfo = b.value
        b.collideInfo = a.value

        return true
    }

    private fun Intersect(a: Comp_Collider, b: Comp_Collider): Boolean {

        if(a.parentPos[0] + a.right < b.parentPos[0] + b.left)
            return false
        if(a.parentPos[1] + a.bottom > b.parentPos[1] + b.top)
            return false
        if(a.parentPos[0] + a.left > b.parentPos[0] + b.right)
            return false
        if(a.parentPos[1] + a.top < b.parentPos[1] + b.bottom)
            return false

        var Inter = listOf(
            (a.right - a.left)/2f + (b.right - b.left)/2 - abs(a.parentPos[0] - b.parentPos[0])
            , (a.top - a.bottom)/2f + (b.top - b.bottom)/2 - abs(a.parentPos[1] - b.parentPos[1])
        )

        var bVertical = if(Inter[0] > Inter[1]) 1 else 0
        var bPush = if(a.parentPos[bVertical] > b.parentPos[bVertical]) 1f else -1f

        a.parentPos[bVertical] += Inter[bVertical] / 2f * bPush
        b.parentPos[bVertical] -= Inter[bVertical] / 2f * bPush

        a.isCollide = true
        b.isCollide = true
        a.collideInfo = b.value
        b.collideInfo = a.value

        return true
    }
}