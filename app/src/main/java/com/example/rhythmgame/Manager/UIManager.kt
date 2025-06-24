package com.example.rhythmgame.Manager

import android.view.MotionEvent
import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.UI.BeatNote
import com.example.rhythmgame.Object.UI.HPBar
import com.example.rhythmgame.Object.UI.UIObject
import com.example.rhythmgame.Object.UI.XButton
import com.example.rhythmgame.Object.UI.YButton

object UIManager: Base() {
    var eventQueue = mutableListOf<MotionEvent?>()
    var joystick: Joystick? = null
    var xbutton: XButton? = null
    var ybutton: YButton? = null
    var hpBar: HPBar? = null
    var beatnote: BeatNote? = null

    // Game over 플래그
    var isGameOver = false

    //다른 오브젝트에게 값을 넘겨주고 싶은 UI를 여기(UI매니저)에 세팅해줘야함
    fun setHPBar(h: HPBar) { hpBar = h }
    fun SetJoystick(ui: Joystick) { joystick = ui }
    fun SetXButton(ui: XButton) { xbutton = ui }
    fun SetYButton(ui: YButton) { ybutton = ui }
    fun SetHPBar(ui: HPBar) { hpBar = ui }
    fun SetBeatNote(ui: BeatNote) {
        beatnote = ui
    }

    //다른 오브젝트에서 특정 UI의 특정 값을 필요로 하면 이런식으로
    //새로 만들어서 값을 얻는 함수를 만들면 됨
    //이거는 플레이어가 조이스틱의 움직임을 가져올때 호출하는 함수임
    fun GetJoystickMovement(): Joystick.Movement {
        return if (joystick != null) {
            joystick!!.GetMovement()
        } else {
            Joystick.Movement(0f, 0f)
        }
    }

    public fun TouchEvent(event: MotionEvent?) {
        eventQueue.add(event)
    }

    public fun CheckTouch() {
        val layer = ObjectManager.Get_Objects(ObjectManager.LayerType.UI)
        for (event in eventQueue) {
            for (uiObj in layer) {
                if (uiObj is UIObject) {
                    uiObj.OnTouch(event)
                }
            }
        }
        eventQueue.clear()
    }
}