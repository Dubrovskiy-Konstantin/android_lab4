package com.example.ball.levels_data

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class PlayerBall(var xc: Float, var yc: Float, var r: Float, var paint: Paint): IMovable {
    private val startX = xc
    private val startY = yc
    val hitBox = RectF(xc-r, yc-r, xc+r, yc+r)

    fun move(x: Float, y: Float, max_X: Float, max_Y: Float){
        xc += x
        yc += y
        if(xc + r > max_X || xc - r < 0)
            xc -= x
        if(yc + r > max_Y || yc - r < 0)
            yc -= y
        hitBox.left = xc-r
        hitBox.top = yc-r
        hitBox.right = xc+r
        hitBox.bottom = yc+r
    }

    override fun move(max_X: Float, max_Y: Float) {}

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(xc, yc, r, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {return false}

    public fun restart(){
        xc = startX
        yc = startY
    }
}