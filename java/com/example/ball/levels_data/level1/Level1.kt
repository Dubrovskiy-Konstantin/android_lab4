package com.example.ball.levels_data.level1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.ArrayList
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.ball.levels_data.Level
import com.example.ball.levels_data.IDrawable
import com.example.ball.levels_data.IMovable
import com.example.ball.levels_data.PlayerBall


class Level1(context: Context) : Level(context) {
    override var isFinished = false
    override var score: Float = 0f
    override var countDeath: Int = 0
    override var elapsedTime: Long = System.currentTimeMillis()
    val max_X : Float
    val max_Y : Float
    var paint: Paint
    var playerBall: PlayerBall
    val iMovable = ArrayList<IMovable>()
    val iDrawable = ArrayList<IDrawable>()

    init {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        max_X = metrics.widthPixels.toFloat()
        max_Y = metrics.heightPixels.toFloat()

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = 3f

        val paint = Paint()
        paint.color = Color.RED
        playerBall = PlayerBall(max_X/2, max_Y-max_Y*0.2f, 50f, paint)

        iDrawable.add(MazeL1(max_X,max_Y))
        iDrawable.add(Enemy1L1())
        iDrawable.add(FinishL1(playerR = playerBall.r))

        iMovable.add(Enemy2L1(4f))
        iMovable.add(playerBall)
        iDrawable.addAll(iMovable)
    }

    override fun draw(canvas: Canvas){
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
        paint.style = Paint.Style.STROKE
        this.iDrawable.forEach { it.draw(canvas) }
    }

    override fun update(x: Float, y: Float) {
        playerBall.move(0.5f * x, 0.5f * y, max_X, max_Y)
        iMovable.forEach{ it.move(max_X, max_Y) }
        iDrawable.forEach {
            if(it !is PlayerBall){
                if(it.contactPlayer(playerBall))
                    when(it){
                        is MazeL1 ->{
                            playerBall.move(-0.5f * x, -0.5f * y, max_X, max_Y)
                        }
                        is Enemy1L1,
                        is Enemy2L1 ->{
                            playerBall.restart()
                            countDeath++
                        }
                        is FinishL1 ->{
                            elapsedTime = System.currentTimeMillis() - elapsedTime
                            val maxScore = 100f
                            score = (1f/((elapsedTime/1000f + 10f*countDeath)/60f))*maxScore
                            if(score > maxScore)
                                score = maxScore
                            isFinished = true
                        }
                    }
            }
        }
    }
}