package com.example.rhythmgame.Object.UI

import com.example.rhythmgame.Component.Comp_Shader
import com.example.rhythmgame.Component.Comp_Texture

class Joystick: UiObject() {
    init {
        TextureCom = Add_Component("Texture_Joystick") as Comp_Texture
        Components.add(TextureCom)
    }
}