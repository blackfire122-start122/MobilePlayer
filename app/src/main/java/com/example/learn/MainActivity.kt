package com.example.learn

import android.R.attr.value
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.gridlayout.widget.GridLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("/api/getListenAgainSongs/")
    fun getSongs(): Call<List<SongItem>>
}

const val host = "http://192.168.0.105:8080"

class MainActivity : AppCompatActivity() {
    private lateinit var gridListenAgain: GridLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<ImageView>(R.id.search)
        val avatar = findViewById<ImageView>(R.id.avatar)

        search.setOnClickListener {
//            val intent = Intent(this, FindSong::class.java)
//            val songItemStr = Gson().toJson(songItem)
//            intent.putExtra("songItem", songItemStr)
//            startActivityForResult(intent,1)
        }

        avatar.setOnClickListener {

        }

        Glide.with(this)
            .load("$host/api/user/image/1.png")
            .error(R.drawable.error_image)
            .into(avatar)

        gridListenAgain = findViewById(R.id.gridListenAgain)

        val retrofit = Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getSongs().enqueue(object : Callback<List<SongItem>> {
            override fun onResponse(call: Call<List<SongItem>>, response: Response<List<SongItem>>) {
                if (response.isSuccessful) {
                    val songs = response.body()
                    songs?.forEachIndexed {_, songItem ->
                        addSongToGridLayout(songItem)
                    }
                }
            }

            override fun onFailure(call: Call<List<SongItem>>, t: Throwable) {
                println("[+] $t")
            }

        })
    }

    @SuppressLint("ResourceAsColor")
    private fun addSongToGridLayout(songItem: SongItem) {
        val songItemLayout = LinearLayout(this)
        songItemLayout.orientation = LinearLayout.VERTICAL

        val layoutParams = GridLayout.LayoutParams()
        layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)

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

        gridListenAgain.addView(songItemLayout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data==null){
            return
        }

        val songItemStr = data.getStringExtra("songItem")
        if (songItemStr != null) {
            val songItem = Gson().fromJson(songItemStr, SongItem::class.java)
            showFragment(songItem)
        }
    }

    private fun showFragment(songItem:SongItem) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = SongFragmentMainPage()

        val bundle = Bundle()
        val songItemStr = Gson().toJson(songItem)
        bundle.putString("songItem", songItemStr)
        fragment.arguments = bundle

        transaction.replace(R.id.songFragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}