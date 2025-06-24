package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Component.Comp_Transform
import com.example.rhythmgame.Manager.ObjectManager
import com.example.rhythmgame.Manager.ObjectManager.Get_Objects
import com.example.rhythmgame.Object.Monster.Mino
import com.example.rhythmgame.Object.Monster.Rat
import com.example.rhythmgame.Object.Player
import kotlin.random.Random

object SpawnManager: Base() {
    private var cooldown = arrayOf(2f, 5f, 10f)
    private var curCooldown = 0f
    private var PlayerTrans: Comp_Transform? = null
    private var range = arrayOf(2f, 3f)

    override fun Update(fTimeDelta: Float) {

        curCooldown -= fTimeDelta

        if(curCooldown <= 0f)
        {
            if(PlayerTrans == null) {
                PlayerTrans = ObjectManager.Get_Objects(ObjectManager.LayerType.PLAYER).first().GetTransformComp()
            }

            val rx = (if(Random.nextBoolean()) 1f else -1f) * (Random.nextFloat() * (range[1] - range[0]) + range[0])
            val ry = (if(Random.nextBoolean()) 1f else -1f) * (Random.nextFloat() * (range[1] - range[0]) + range[0])
            val randomMonster = Random.nextInt(0, 2)

            when(randomMonster) {
                0 -> {
                    curCooldown = cooldown[0]
                    val rat = Rat(PlayerTrans as Comp_Transform)
                    rat.GetTransformComp().position[0] = rx
                    rat.GetTransformComp().position[1] = ry
                    ObjectManager.Add_Object(ObjectManager.LayerType.MONSTER, rat)}
                1 -> {
                    curCooldown = cooldown[1]
                    val mino = Mino(PlayerTrans as Comp_Transform)
                    mino.GetTransformComp().position[0] = rx
                    mino.GetTransformComp().position[1] = ry
                    ObjectManager.Add_Object(ObjectManager.LayerType.MONSTER, mino)}
                }
            }
        }
}