package com.example.ball.levels_data.level1

import android.graphics.*
import com.example.ball.levels_data.IDrawable
import com.example.ball.levels_data.IMovable
import com.example.ball.levels_data.PlayerBall
import java.util.ArrayList


class MazeL1(val max_X: Float, val max_Y: Float) : IDrawable {
    val rects = ArrayList<RectF>()
    init {
        rects.add(RectF(800f, 1300f, max_X, max_Y))
        rects.add(RectF(860f, 1200f, max_X, 1300f))
        rects.add(RectF(560f, 1100f, max_X, 1200f))
        rects.add(RectF(300f, 850f, 400f, 1300f))
        rects.add(RectF(740f, 470f, max_X, 550f))
        rects.add(RectF(0f, 350f,350f, 450f))
        rects.add(RectF(0f, max_Y-300f, max_X, max_Y))
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        rects.forEach { canvas.drawRect(it, paint) }
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        rects.forEach {
            if(it.intersects(playerBall.hitBox.left,playerBall.hitBox.top, playerBall.hitBox.right, playerBall.hitBox.bottom))
                return true
        }
        return false
    }
}

class Enemy1L1: IDrawable {
    val points = ArrayList<PointF>()
    val hitBox: RectF
    init {
        points.add(PointF(570f,1050f))
        points.add(PointF(680f,990f))
        points.add(PointF(680f,1090f))
        hitBox = RectF(570f, 990f, 680f, 1090f)
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.rgb(140,0,20)
        paint.style = Paint.Style.FILL
        val path = Path()
        path.reset()
        path.moveTo(points[0].x, points[0].y)
        path.lineTo(points[1].x, points[1].y)
        path.lineTo(points[2].x, points[2].y)
        path.setLastPoint(points[2].x, points[2].y)
        canvas.drawPath(path, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return hitBox.intersects(playerBall.hitBox.left,playerBall.hitBox.top, playerBall.hitBox.right, playerBall.hitBox.bottom)
    }
}

class Enemy2L1(speed: Float): IMovable {
    val hitBox: RectF
    private var speed = speed
    val points = ArrayList<PointF>()
    init {
        points.add(PointF(620f,655f))
        points.add(PointF(640f,605f))
        points.add(PointF(660f,630f))
        points.add(PointF(680f,605f))
        points.add(PointF(700f,630f))
        points.add(PointF(720f,605f))
        points.add(PointF(740f,630f))
        points.add(PointF(760f,605f))
        points.add(PointF(780f,655f))
        points.add(PointF(760f,705f))
        points.add(PointF(740f,680f))
        points.add(PointF(720f,705f))
        points.add(PointF(700f,680f))
        points.add(PointF(680f,705f))
        points.add(PointF(660f,680f))
        points.add(PointF(640f,705f))
        hitBox = RectF(620f,605f,780f, 705f)
    }

    override fun move(max_X: Float, max_Y: Float) {
        for (i in 0 until points.size)
            points[i].x += speed
        hitBox.right += speed
        hitBox.left += speed
        if(hitBox.right >= max_X) speed = -speed
        if(hitBox.left  <= 0) speed= -speed
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.rgb(140,0,20)
        paint.style = Paint.Style.FILL
        val path = Path()
        path.reset()
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size)
            path.lineTo(points[i].x, points[i].y)
        path.lineTo(points[0].x, points[0].y)
        path.setLastPoint(points[0].x, points[0].y)
        canvas.drawPath(path, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return hitBox.intersects(playerBall.hitBox.left,playerBall.hitBox.top, playerBall.hitBox.right, playerBall.hitBox.bottom)
    }
}

class FinishL1(playerR: Float): IDrawable {
    val hitBox: RectF
    private var x : Float
    private var y : Float
    private var r : Float
    init {
        x = 940f
        y = 115f
        r = playerR + 20f
        hitBox = RectF(x-r,y-r, x+r, y+r)
    }
    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.GREEN
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, r, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return hitBox.contains(playerBall.hitBox)
    }
}