package com.example.rhythmgame.Object.UI

import android.view.MotionEvent
import com.example.rhythmgame.Base.GameObject

abstract class UIObject: GameObject() {
    abstract fun OnTouch(event: MotionEvent?)
}