package com.example.ball

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import com.example.ball.levels_data.Level
import com.example.ball.levels_data.level1.Level1
import com.example.ball.levels_data.level2.Level2

class GameActivity : AppCompatActivity() {
    private lateinit var currentPlayer: Player
    private lateinit var mediaPlayer : MediaPlayer
    lateinit var surface: SurfaceView
    lateinit var engine: Engine

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
        surface = findViewById(R.id.surface)
        val levels = ArrayList<Level>()
        levels.add(Level1(this))
        levels.add(Level2(this))
        engine = Engine(surface, this, levels)

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

    override fun onDestroy() {
        engine.stop()
        super.onDestroy()
    }

    public fun _toMainMenu(score: Float){
        currentPlayer.Rate += score
        currentPlayer.Games += 1

        val intent = Intent()
        intent.putExtra("player", currentPlayer)
        setResult(RESULT_OK, intent)
        finish()
    }
}