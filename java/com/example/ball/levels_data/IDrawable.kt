package com.example.ball.levels_data

import android.graphics.Canvas

interface IDrawable{
    fun draw(canvas: Canvas)
    fun contactPlayer(playerBall: PlayerBall): Boolean
}