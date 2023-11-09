package com.example.learn

import android.media.AudioManager
import android.media.MediaPlayer
import java.io.IOException

object MediaManager {
    val mediaPlayer: MediaPlayer = MediaPlayer()
    private lateinit var song: SongItem

    init {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    fun getSong(): SongItem {
        return song
    }

    fun play(songItem: SongItem) {
        song = songItem
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource("$host/streamSong/${song.fileSong}")
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
