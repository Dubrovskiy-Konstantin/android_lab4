package com.example.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.ArrayList
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.sign


class Level1(context: Context) : Level(context) {
    override var isFinished = false
    val max_X : Float
    val max_Y : Float
    var paint: Paint
    var player: Player
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
//        val canvas = Canvas()
//        max_X = canvas.width.toFloat()
//        max_Y = canvas.height.toFloat()


        val paint = Paint()
        paint.color = Color.RED
        player = Player(max_X/2, max_Y-max_Y*0.2f, 50f, paint)

        iDrawable.add(Maze1(max_X,max_Y))
        iDrawable.add(Enemy1())
        iDrawable.add(Finish(playerR = player.r))

        iMovable.add(Enemy2(4f))
        iMovable.add(player)
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
        player.move(0.5f * x, 0.5f * y, max_X, max_Y)
        iMovable.forEach{ it.move(max_X, max_Y) }
        iDrawable.forEach {
            if(it !is Player){
                if(it.contactPlayer(player))
                    when(it){
                        is Maze1 ->{
                            player.move(-0.5f * x, -0.5f * y, max_X, max_Y)
                        }
                        is Enemy1,
                        is Enemy2 ->{
                            player.restart()
                        }
                        is Finish ->{
                            isFinished = true
                        }
                    }
            }
        }
    }
}