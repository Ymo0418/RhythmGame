package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Component.Comp_Collider
import com.example.rhythmgame.Object.Monster
import com.example.rhythmgame.Object.Player

object CollisionManager: Base() {
    public enum class ColliderGroup {
        PLAYER, MONSTER, WALL
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
        IntersectGroup(ColliderGroup.MONSTER, ColliderGroup.PLAYER)
    }

    override fun LateUpdate(fTimeDelta: Float) {
    }

    public fun RegisterCollider(group: ColliderGroup, collider: Comp_Collider) {
        Colliders[group]?.add(collider)
    }

    public fun ClearGroup() {

    }

    private fun IntersectGroup(a: ColliderGroup, b: ColliderGroup) {
        for(ca in Colliders[a]!!) {
            for(cb in Colliders[b]!!) {
                Intersect(ca, cb)
            }
        }
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

        a.isCollide = true
        b.isCollide = true
        a.collideInfo = b.value
        b.collideInfo = a.value

        return true
    }
}