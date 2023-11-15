package com.example.learn

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.widget.doAfterTextChanged
import androidx.gridlayout.widget.GridLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Find : AppCompatActivity() {
    private lateinit var linearLayoutSongs: LinearLayout
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val intent = intent
        val areSongFragment = intent.getBooleanExtra("areSongFragment", false)

        if (areSongFragment){
            val fragment = SongFragmentMainPage()
            val transaction = supportFragmentManager.beginTransaction()

            val songFragment = findViewById<FrameLayout>(R.id.songFragment)
            songFragment.visibility = View.VISIBLE

            transaction.replace(R.id.songFragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val editTextFind = findViewById<EditText>(R.id.editTextFind)
        linearLayoutSongs = findViewById(R.id.LinearLayoutSongs)

        val backButtonFind = findViewById<ImageView>(R.id.backButtonFind)
        backButtonFind.setOnClickListener {
            handleSystemBackButton()
        }


        editTextFind.doAfterTextChanged {
            val retrofit = Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiServiceFind = retrofit.create(ApiServiceFind::class.java)

            apiServiceFind.findSongs(editTextFind.text.toString()).enqueue(object : Callback<List<SongItem>> {
                override fun onResponse(call: Call<List<SongItem>>, response: Response<List<SongItem>>) {
                    if (response.isSuccessful) {
                        val songs = response.body()
                        runOnUiThread {
                            linearLayoutSongs.removeAllViews()
                            songs?.forEachIndexed { _, songItem ->
                                addSongToListSongs(songItem)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<SongItem>>, t: Throwable) {
                    runOnUiThread {
                        println("[+] $t")
                    }
                }

            })
        }
    }

    private fun addSongToListSongs(songItem: SongItem){
        val songItemLayout = LinearLayout(this)
        songItemLayout.orientation = LinearLayout.VERTICAL

        val layoutParams = GridLayout.LayoutParams()

        layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT

        layoutParams.setMargins(8)

        songItemLayout.layoutParams = layoutParams

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.layoutParams = ViewGroup.LayoutParams(resources.getDimensionPixelSize(R.dimen.image_width), resources.getDimensionPixelSize(R.dimen.image_height))

        Glide.with(this)
            .load("$host/api/songs/image/${songItem.image}")
            .error(R.drawable.error_image)
            .into(imageView)

        val nameTextView = TextView(this)
        nameTextView.text = songItem.name

        nameTextView.setPadding(16, 8, 16, 8)
        nameTextView.textSize = 25F
        nameTextView.gravity = Gravity.CENTER

        songItemLayout.addView(imageView)
        songItemLayout.addView(nameTextView)

        songItemLayout.setOnClickListener {
            val intent = Intent(this, PlayerSong::class.java)
            val songItemStr = Gson().toJson(songItem)
            intent.putExtra("songItem", songItemStr)
            startActivityForResult(intent,1)
        }

        linearLayoutSongs.addView(songItemLayout)
    }

    override fun onBackPressed() {
        handleSystemBackButton()
    }

    private fun handleSystemBackButton() {
        finish()
    }
}