package com.example.ball

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager

class GameActivity : AppCompatActivity() {
    private lateinit var currentPlayer: Player
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //----- Remove action bar -----
        try { this.supportActionBar!!.hide() }
        catch (e: NullPointerException) {}
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //----- Get Extras -----
        if(intent.extras != null){
            currentPlayer = intent!!.extras!!.get("player") as Player
        }
        else return
        //----- Views -----
        setContentView(R.layout.activity_game)

    }

    override fun onResume() {
        super.onResume()
        //----- Music -----
        mediaPlayer = MediaPlayer.create(this, R.raw.level_1)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun onPause() {
        mediaPlayer.stop()
        super.onPause()
    }

    fun onClick(view: View){
        _toMainMenu()
    }

    private fun _toMainMenu(){
        currentPlayer.Rate += 100.5f
        currentPlayer.Games += 1

        val intent = Intent()
        intent.putExtra("player", currentPlayer)
        setResult(RESULT_OK, intent)
        finish()
    }
}