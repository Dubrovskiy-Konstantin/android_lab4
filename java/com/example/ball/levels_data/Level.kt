package com.example.ball.levels_data

import android.content.Context
import android.graphics.*

abstract class Level(open val context: Context) {
    abstract var isFinished: Boolean
    abstract var score: Float
    abstract var countDeath: Int
    abstract var elapsedTime: Long
    abstract fun draw(canvas: Canvas)
    abstract fun update(x: Float, y: Float)
}