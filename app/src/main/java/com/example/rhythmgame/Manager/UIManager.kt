package com.example.rhythmgame.Manager

import android.graphics.Paint.Join
import com.example.rhythmgame.Base.Base
import com.example.rhythmgame.Object.Joystick
import com.example.rhythmgame.Object.UI.GameOver
import com.example.rhythmgame.Object.UI.HP
import com.example.rhythmgame.Object.UI.XButton
import com.example.rhythmgame.Object.UI.YButton

object UIManager: Base() {
    var joystick: Joystick? = null
    var joystick2: Joystick? = null
    var xbutton: XButton? = null
    var ybutton: YButton? = null
    var hp: HP? = null
    var Gameover: GameOver? = null

//    var Gameover: GameOver? = null
//    var Continue: GameOver? = null
//    var ExitGame: GameOver? = null

    // Game over 플래그
    var isGameOver = false
    var gameOverTimer = -1f

    fun getHpFrame(): Int = hp?.getCurrentFrame() ?: -1
    fun isHpDead(): Boolean = hp?.isDead() ?: false
//
//
//    //var dash: UIButton? = null
//    //등등

    //다른 오브젝트에게 값을 넘겨주고 싶은 UI를 여기(UI매니저)에 세팅해줘야함
    fun SetJoystick(ui: Joystick) {
        joystick = ui
    }

    fun SetJoystick2(ui: Joystick) {
        joystick2 = ui
    }

    //다른 오브젝트에서 특정 UI의 특정 값을 필요로 하면 이런식으로
    //새로 만들어서 값을 얻는 함수를 만들면 됨
    //이거는 플레이어가 조이스틱의 움직임을 가져올때 호출하는 함수임
    fun GetMovement(): Joystick.Movement {
        return if (joystick != null) {
            joystick!!.GetMovement()
        } else {
            Joystick.Movement(0f, 0f)
        }
    }

    fun SetXButton(ui: XButton) {
        xbutton = ui
    }

    fun SetXButton(ui: YButton) {
        ybutton = ui
    }

    fun SetHP(ui: HP) {
        hp = ui
    }

    fun SetGameOver(ui: GameOver) {
        Gameover = ui
    }

    fun GetGameOverShowing(): Boolean {
        return Gameover!!.isShowing()
    }
    fun SetGameOverShowing() {
        Gameover!!.show()
    }

//    fun SetGameOver(ui: GameOver) {
//        Gameover = ui
//    }
//    fun SetContinue(ui: GameOver) {
//        Continue = ui
//    }
//    fun SetExitGame(ui: GameOver) {
//        ExitGame = ui
//    }

}