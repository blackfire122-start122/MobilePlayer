package com.example.learn

import android.R.attr.value
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

const val host = "http://192.168.0.105:8080"

class MainActivity : AppCompatActivity() {
    private lateinit var gridListenAgain: GridLayout
    private lateinit var linearSongTop: LinearLayout
    private lateinit var avatar: ImageView
    private var areSongFragment :Boolean = false
    private lateinit var user :User
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<ImageView>(R.id.search)
        avatar = findViewById(R.id.avatar)

        search.setOnClickListener {
            val intent = Intent(this, Find::class.java)
            intent.putExtra("areSongFragment", areSongFragment)
            startActivity(intent)
        }

        avatar.setOnClickListener {

        }

        gridListenAgain = findViewById(R.id.gridListenAgain)
        linearSongTop = findViewById(R.id.linearSongTop)

        val retrofit = Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiServiceListenAgainSongs = retrofit.create(ApiServiceListenAgainSongs::class.java)
        val apiServiceTop = retrofit.create(ApiServiceTop::class.java)
        val apiServiceGetUser = retrofit.create(ApiServiceGetUser::class.java)

        apiServiceListenAgainSongs.getSongs().enqueue(object : Callback<List<SongItem>> {
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


        apiServiceTop.getTops().enqueue(object : Callback<List<SongItem>> {
            override fun onResponse(call: Call<List<SongItem>>, response: Response<List<SongItem>>) {
                if (response.isSuccessful) {
                    val songs = response.body()
                    songs?.forEachIndexed {_, songItem ->
                        addSongToLinearLayout(songItem)
                    }
                }
            }

            override fun onFailure(call: Call<List<SongItem>>, t: Throwable) {
                println("[+] $t")
            }

        })

        apiServiceGetUser.getUser().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user = response.body()!!
                    loadUserImage(user)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println("[+] $t")
            }

        })
    }

    private fun loadUserImage(user: User) {
        Glide.with(this)
            .load("$host/api/user/image/${user.image}")
            .error(R.drawable.error_image)
            .into(avatar)
    }


    @SuppressLint("ResourceAsColor")
    private fun addSongToLinearLayout(songItem: SongItem) {
        println(songItem)
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
        nameTextView.setTextColor(Color.parseColor("#FFFFFF"))

        songItemLayout.addView(imageView)
        songItemLayout.addView(nameTextView)

        songItemLayout.setOnClickListener {
            val intent = Intent(this, PlayerSong::class.java)
            val songItemStr = Gson().toJson(songItem)
            intent.putExtra("songItem", songItemStr)
            startActivityForResult(intent,1)
        }

        linearSongTop.addView(songItemLayout)
    }

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
        nameTextView.setTextColor(Color.parseColor("#FFFFFF"))

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
        showFragment()
        areSongFragment = true
    }

    private fun showFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = SongFragmentMainPage()

        val songFragment = findViewById<FrameLayout>(R.id.songFragment)
        songFragment.visibility = View.VISIBLE

        transaction.replace(R.id.songFragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}