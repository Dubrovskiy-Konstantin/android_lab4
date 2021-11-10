package com.example.ball.levels_data.level2

import android.graphics.*
import com.example.ball.levels_data.IDrawable
import com.example.ball.levels_data.IMovable
import com.example.ball.levels_data.PlayerBall
import java.util.ArrayList

class MazeL2(val max_X: Float, val max_Y: Float) : IDrawable {
    val rects = ArrayList<RectF>()
    init {
        rects.add(RectF(0f, max_Y-250f, max_X, max_Y))
        rects.add(RectF(200f, 1220f, max_X, 1320f))
        rects.add(RectF(545f, 840f, 800f, 990f))
        rects.add(RectF(200f, 680f, 300f, 990f))
        rects.add(RectF(200f, 540f, 800f, 680f))
        rects.add(RectF(0f, 200f, 800f, 330f))
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

class Enemy1L2(max_X: Float, max_Y: Float): IDrawable {
    val points = ArrayList<PointF>()
    val hitBox: RectF
    init {
        points.add(PointF(max_X,1320f))
        points.add(PointF(970f,1357.5f))
        points.add(PointF(max_X-20,1395f))
        points.add(PointF(970f,1432.5f))
        points.add(PointF(max_X-20,1470f))
        points.add(PointF(970f,1507.5f))
        points.add(PointF(max_X,1545f))
        hitBox = RectF(970f, 1320f, max_X, 1545f)
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
        path.lineTo(points[3].x, points[3].y)
        path.lineTo(points[4].x, points[4].y)
        path.lineTo(points[5].x, points[5].y)
        path.lineTo(points[6].x, points[6].y)
        canvas.drawPath(path, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return hitBox.intersects(playerBall.hitBox.left,playerBall.hitBox.top, playerBall.hitBox.right, playerBall.hitBox.bottom)
    }
}

class Enemy2L2(speed: Float): IMovable {
    val hitBox: RectF
    private var speed = speed
    val points = ArrayList<PointF>()
    init {
        points.add(PointF(75f,475f))
        points.add(PointF(95f,525f))
        points.add(PointF(125f,550f))
        points.add(PointF(95f,575f))
        points.add(PointF(125f,600f))
        points.add(PointF(95f,625f))
        points.add(PointF(75f,675f))
        points.add(PointF(55f,625f))
        points.add(PointF(25f,600f))
        points.add(PointF(55f,575f))
        points.add(PointF(25f,550f))
        points.add(PointF(55f,525f))
        hitBox = RectF(25f,475f,125f, 675f)
    }

    override fun move(max_X: Float, max_Y: Float) {
        for (i in 0 until points.size)
            points[i].y += speed
        hitBox.top += speed
        hitBox.bottom += speed
        if(hitBox.top >= 330) speed = -speed
        if(hitBox.bottom  <= 1545) speed= -speed
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

class Enemy3L2(speed: Float): IMovable {
    val hitBox: RectF
    private var speed = speed
    val center: PointF
    val r: Float
    val motionRectF: RectF
    var motion: PointF
    init {
        motionRectF = RectF(420f, 760f, 1000f, 1140f)

        this.center = PointF(1000f, 760f)
        r = 50f
        motion = PointF(0f, speed)
        hitBox = RectF(center.x-r,center.y-r,center.x+r, center.y+r)
    }

    override fun move(max_X: Float, max_Y: Float) {
        center.x += motion.x
        center.y += motion.y
        when {
            center.x < motionRectF.left ->{
                center.x += speed; motion.x = 0f; motion.y = -speed
            }
            center.x > motionRectF.right ->{
                center.x -= speed; motion.x = 0f; motion.y = speed
            }
            center.y > motionRectF.bottom ->{
                center.y -= speed; motion.x = -speed; motion.y = 0f
            }
            center.y < motionRectF.top ->{
                center.y += speed; motion.x = speed; motion.y = 0f
            }
        }
        hitBox.left = center.x - r
        hitBox.top = center.y - r
        hitBox.right = center.x + r
        hitBox.bottom = center.y + r
    }

    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.rgb(140,0,20)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(center.x, center.y, r, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return hitBox.intersects(playerBall.hitBox.left,playerBall.hitBox.top, playerBall.hitBox.right, playerBall.hitBox.bottom)
    }
}

class Enemy4L2(speed: Float): IMovable {
    val hitBox: RectF
    private var speed = speed
    val points = ArrayList<PointF>()
    init {
        points.add(PointF(850f,190f))
        points.add(PointF(900f,260f))
        points.add(PointF(850f,330f))
        points.add(PointF(800f,260f))
        hitBox = RectF(800f,190f,900f, 330f)
    }

    override fun move(max_X: Float, max_Y: Float) {
        for (i in 0 until points.size)
            points[i].x += speed
        hitBox.left += speed
        hitBox.right += speed
        if(hitBox.left < 800f) speed = -speed
        if(hitBox.right  >= max_X) speed= -speed
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

class PortalInL2(playerR: Float): IDrawable {
    val hitBox: RectF
    private var x : Float
    private var y : Float
    private var r : Float
    init {
        x = 325f
        y = 440f
        r = playerR + 20f
        hitBox = RectF(x-r,y-r, x+r, y+r)
    }
    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.rgb(0,0,255)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, r, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return hitBox.contains(playerBall.hitBox)
    }
}

class PortalOutL2(playerR: Float): IDrawable {
    val hitBox: RectF
    private var x : Float
    private var y : Float
    private var r : Float
    init {
        x = 115f
        y = 100f
        r = playerR + 20f
        hitBox = RectF(x-r,y-r, x+r, y+r)
    }
    override fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.rgb(200,0, 255)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, r, paint)
    }

    override fun contactPlayer(playerBall: PlayerBall): Boolean {
        return false
    }
}

class FinishL2(playerR: Float): IDrawable {
    val hitBox: RectF
    private var x : Float
    private var y : Float
    private var r : Float
    init {
        x = 950f
        y = 100f
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
