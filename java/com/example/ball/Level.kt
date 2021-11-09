package com.example.draw

import android.content.Context
import android.graphics.*

abstract class Level(open val context: Context) {
    abstract var isFinished: Boolean
    abstract fun draw(canvas: Canvas)
    abstract fun update(x: Float, y: Float)
}