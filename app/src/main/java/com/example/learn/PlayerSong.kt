package com.example.learn

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson

class PlayerSong() : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private var songItem: SongItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_song)

        val intent = intent
        val songItemStr = intent.getStringExtra("songItem")

        val imageSong = findViewById<ImageView>(R.id.imageSong)
        val textViewNameSong = findViewById<TextView>(R.id.textViewNameSong)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val forwardButton = findViewById<ImageButton>(R.id.forwardButon)
        val playPauseButton = findViewById<ImageButton>(R.id.playPauseButton)

        val back = findViewById<ImageView>(R.id.back)

        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        songItem = Gson().fromJson(songItemStr, SongItem::class.java)
        textViewNameSong.text = songItem?.name ?: "No name"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MediaManager.mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        MediaManager.mediaPlayer.setOnCompletionListener {
            playPauseButton?.setImageResource(R.drawable.play)
        }

        playPauseButton?.setOnClickListener {
            if (MediaManager.mediaPlayer.isPlaying){
                MediaManager.mediaPlayer.pause()
                playPauseButton.setImageResource(R.drawable.play)
            }else{
                MediaManager.mediaPlayer.start()
                playPauseButton.setImageResource(R.drawable.pause)
            }
        }

        back.setOnClickListener {
            handleSystemBackButton()
        }

        backButton.setOnClickListener {

        }

        forwardButton.setOnClickListener {

        }

        val handler = Handler()

        val updateSeekBar = object : Runnable {
            override fun run() {
                seekBar.progress = MediaManager.mediaPlayer.currentPosition
                handler.postDelayed(this, 1000)
            }
        }

        handler.postDelayed(updateSeekBar, 0)

        Glide.with(this)
            .load("$host/api/songs/image/${songItem?.image}")
            .error(R.drawable.error_image)
            .into(imageSong)

        songItem?.let { MediaManager.play(it) }
        seekBar.max = MediaManager.mediaPlayer.duration
    }

    override fun onBackPressed() {
        handleSystemBackButton()
    }

    private fun handleSystemBackButton() {
        val intent = Intent()
        setResult(RESULT_OK,intent)
        finish()
    }
}