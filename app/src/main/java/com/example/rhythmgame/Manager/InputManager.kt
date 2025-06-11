package com.example.rhythmgame.Manager

import com.example.rhythmgame.Base.Base

object InputManager: Base() {

    class TouchInput {
        var x = 0f
        var y = 0f
    }

    private var touchInputPre = TouchInput()
    private var touchInput = TouchInput()


    public fun Set_TouchInput(x:Float, y:Float) {
        touchInput.x = x
        touchInput.y = y
    }

    public fun Get_TouchInput(): TouchInput {
        return touchInput
    }
}