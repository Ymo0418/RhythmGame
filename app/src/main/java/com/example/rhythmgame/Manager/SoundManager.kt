package com.example.rhythmgame.Manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Looper
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import com.example.rhythmgame.Base.Base

object SoundManager: Base() {

    private var bgmPlayer: ExoPlayer? = null
    private lateinit var sfxPlayer: SoundPool
    private val sfxMap = mutableMapOf<String, Int>()

    private var bpm = 60f
    private var beatInterval = 0f
    private var lastBeatTime = 0L
    private var beatWindow = 100L //ms 단위의 혀용 오차

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

    fun Get_ValidBeat(): Boolean {
        val now = System.currentTimeMillis()
        val timeSinceLast = now - lastBeatTime

        if (timeSinceLast >= beatInterval) {
            // 다음 박자로 넘어감
            lastBeatTime += beatInterval.toLong()
        }

        // 지금이 박자 근처인지 확인 (허용 오차 범위 내면 true 반환)
        val delta = Math.abs(now - lastBeatTime)
        return delta <= beatWindow
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

    fun PlayBGM(context: Context, resId: Int, loop: Boolean = true) {

        this.bpm = bpm
        this.beatInterval = 60000f / bpm  // 1박자 간격 (ms)
        lastBeatTime = System.currentTimeMillis()

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")

        android.os.Handler(Looper.getMainLooper()).post() {
            bgmPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaItem((MediaItem.fromUri(uri)))
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
                prepare()
                play()
            }
        }
    }

    fun StopBGM() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
    }

    fun Release() {
        sfxPlayer.release()
        bgmPlayer?.release()
        bgmPlayer = null
    }
}
