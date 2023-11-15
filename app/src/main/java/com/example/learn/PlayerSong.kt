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
import androidx.appcompat.widget.PopupMenu

class PlayerSong() : AppCompatActivity(), WebSocketClient.WebSocketListenerInterface{
    @SuppressLint("MissingInflatedId")
    private var songItem: SongItem? = null
    private lateinit var popupMenu: PopupMenu
    private val webSocketClient = WebSocketClient(this)

    @SuppressLint("MissingInflatedId")
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
        val addFriend = findViewById<ImageView>(R.id.addFriend)

        val back = findViewById<ImageView>(R.id.back)

        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        popupMenu = PopupMenu(this, addFriend)

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
                val messagePause = Message.StringMessage("pause","")
                webSocketClient.sendMessage(Gson().toJson(messagePause))
            }else{
                MediaManager.mediaPlayer.start()
                playPauseButton.setImageResource(R.drawable.pause)
                val messagePlay = Message.StringMessage("play","")
                webSocketClient.sendMessage(Gson().toJson(messagePlay))
            }
        }

        back.setOnClickListener {
            handleSystemBackButton()
        }

        backButton.setOnClickListener {

        }

        forwardButton.setOnClickListener {

        }

        addFriend.setOnClickListener {
            webSocketClient.sendMessage(Gson().toJson(Message.StringMessage("getFriendsOnline", "")))
            popupMenu.show()
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


    override fun onConnected() {

    }

    override fun onResponse(data: Message) {
        when (data) {
            is Message.OnlineUsers -> {
                if (data.type=="OnlineUsers"){
                    for (user in data.content){
                        popupMenu.menu.add(user.username)
                    }
                }
            }
            is Message.StringMessage -> {

            }
        }
    }

    override fun onClosed() {

    }
}