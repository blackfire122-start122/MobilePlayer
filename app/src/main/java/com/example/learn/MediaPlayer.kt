package com.example.learn

import android.media.AudioManager
import android.media.MediaPlayer
import java.io.IOException

object MediaManager {
    val mediaPlayer: MediaPlayer = MediaPlayer()

    init {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    fun play(songUrl: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource("$host/streamSong/$songUrl")
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
