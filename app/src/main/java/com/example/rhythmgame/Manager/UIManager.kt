package com.example.rhythmgame.Manager

import android.graphics.Paint.Join
import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Object.Joystick

object UIManager: Base() {
    var joystick: Joystick? = null
    //var skill1: UIButton? = null
    //var dash: UIButton? = null

    fun SetJoystick(ui: Joystick) {
        joystick = ui
    }
    fun GetMovement(): Joystick.Movement = joystick?.GetMovement() ?: Joystick.Movement(0f, 0f)
}