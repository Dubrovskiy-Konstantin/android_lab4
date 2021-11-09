package com.example.draw

import android.graphics.Canvas
import android.graphics.RectF

interface IDrawable{
    fun draw(canvas: Canvas)
    fun contactPlayer(player: Player): Boolean
}