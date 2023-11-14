package com.example.learn

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson

class SongFragmentMainPage : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_blank, container, false)

        val pauseImageView = view.findViewById<ImageView>(R.id.pauseMainFragment)
        val headImageFragment = view.findViewById<ImageView>(R.id.headImageFragment)

        if (MediaManager.mediaPlayer.isPlaying){
            pauseImageView.setImageResource(R.drawable.pause)
        }

        val songItem = MediaManager.getSong()

        Glide.with(this)
            .load("${com.example.learn.host}/api/songs/image/${songItem.image}")
            .error(R.drawable.error_image)
            .into(headImageFragment)


        pauseImageView.setOnClickListener {
            if (MediaManager.mediaPlayer.isPlaying){
                MediaManager.mediaPlayer.pause()
                pauseImageView.setImageResource(R.drawable.play)
//                val messagePause = Message.StringMessage("pause","")
//                webSocketClient.sendMessage(Gson().toJson(messagePause))
            }else{
                MediaManager.mediaPlayer.start()
                pauseImageView.setImageResource(R.drawable.pause)
//                val messagePlay = Message.StringMessage("play","")
//                webSocketClient.sendMessage(Gson().toJson(messagePlay))
            }
        }

        return view
    }
}