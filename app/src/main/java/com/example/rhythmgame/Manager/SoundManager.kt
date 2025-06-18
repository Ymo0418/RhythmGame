package com.example.rhythmgame.Manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.example.rhythmgame.Base.Base

object SoundManager: Base() {

    private var bgmPlayer: MediaPlayer? = null
    private lateinit var sfxPlayer: SoundPool
    private val sfxMap = mutableMapOf<String, Int>()

    private var bpm = 68f
    private var startTime = System.currentTimeMillis()
    private var beatInterval_ms = 60000f / bpm
    private var beatWindow = 0.1f //혀용 오차
    private var beatRatio = 0f

    fun Init(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        sfxPlayer = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    override fun Update(fTimeDelta: Float) {
        var now = System.currentTimeMillis()
        var elapsed = now - startTime

        val elapsedInCurrentBeat = elapsed % beatInterval_ms
        beatRatio = (elapsedInCurrentBeat / beatInterval_ms)
    }

    fun GetBeatRatio(): Float {
        return beatRatio
    }

    fun GetBeatValid(): Boolean {
        return (beatRatio < beatWindow ||
            beatRatio > (1f - beatWindow))
    }

    fun LoadSE(context: Context, name: String, resId: Int) {
        val soundId = sfxPlayer.load(context, resId, 1)
        sfxMap[name] = soundId
    }

    fun PlaySFX(name: String, volume: Float = 1.0f) {
        sfxMap[name]?.let { id ->
            sfxPlayer.play(id, volume, volume, 1, 0, 1f)
        }
    }

    fun PlayBGM(context: Context, resId: Int, speed: Float = 1f, loop: Boolean = true) {
        StopBGM()

        bgmPlayer = MediaPlayer.create(context, resId)?.apply {
            isLooping = loop
            setOnErrorListener { mp, what, extra ->
                Log.e("SoundManager", "BGM error: what=$what, extra=$extra")
                StopBGM()
                true
            }

            start()

            val params = playbackParams
            params.speed = speed
            playbackParams = params
        }
    }

    fun ChangeSpeedBGM(speed: Float = 1f) {
        bgmPlayer?.let {
            val params = it.playbackParams
            params.speed = speed
            it.playbackParams = params
        }
    }

    fun StopBGM() {
        bgmPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        bgmPlayer = null
    }

    fun Release() {
        StopBGM()
    }
}
