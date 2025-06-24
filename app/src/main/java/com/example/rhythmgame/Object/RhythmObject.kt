package com.example.rhythmgame.Object

import com.example.rhythmgame.Base.GameObject
import com.example.rhythmgame.Manager.SoundManager

abstract class RhythmObject: GameObject() {
    protected var beatRatio = 0f

    override fun Update(fTimeDelta: Float) {
        beatRatio = SoundManager.GetBeatRatio()
    }

    override fun Render(): Boolean {
        return super.Render()
    }
}