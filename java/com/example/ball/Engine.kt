package com.example.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.abs
import kotlin.math.sign

//class Engine(surfaceView: SurfaceView, val context: Context){
class Engine(surfaceView: SurfaceView, val context: Context, val levels: ArrayList<Level>){
    private lateinit var sensor : Sensor
    private lateinit var sensorManager : SensorManager
    private lateinit var mySensorEventListener : SensorEventListener
    var values: FloatArray = FloatArray(3)


    private val lock = ReentrantLock()
    private val condition = lock.newCondition()


    private var currentLevel: Int = 0
    //private var render: Render = Render()
    private lateinit var callback: SurfaceHolder.Callback
    private var surfaceHolder: SurfaceHolder? = null
    var time = System.nanoTime()
    @Volatile
    private var stopped = false

    var threadRunnable = Runnable {
        while (!stopped) {
            val canvas: Canvas? = surfaceHolder?.lockCanvas()
            if (surfaceHolder == null || canvas == null) {
                lock.withLock {
                    try {
                        condition.await()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            } else {
                if (!levels[currentLevel].isFinished){
                    val timeElapsed = System.nanoTime() - time
                    time = System.nanoTime()
                    levels[currentLevel].update(values[1], values[0])
                    levels[currentLevel].draw(canvas)
                }
                else{
                    val paint = Paint()
                    paint.color = Color.WHITE
                    paint.style = Paint.Style.STROKE
                    paint.textSize = 60f
                    canvas.drawText("YOU WIN!!!!!",(canvas.width/2).toFloat(), (canvas.height/2).toFloat(), paint)

                    if(currentLevel < levels.size - 1){
                        currentLevel++
                        Thread.sleep(2000)
                    }
                    else{
                        exit()
                    }
                }
                surfaceHolder?.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun exit(){
        throw Exception("EXIT")
    }

    init {
        val engineThread = Thread(threadRunnable, "EngineThread")
        engineThread.start()
        callback = object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                this@Engine.surfaceHolder = surfaceHolder
                lock.withLock{ condition.signalAll() }
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
                this@Engine.surfaceHolder = surfaceHolder
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                this@Engine.surfaceHolder = null
            }
        }
        surfaceView.holder.addCallback(callback)


        sensorManager = context.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensor =
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null)
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            else
                throw Exception("Sensor = null")

        mySensorEventListener = (object: SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (event.sensor.type == Sensor.TYPE_ORIENTATION){
                    val const = 57.2957549575f
                    val limit = 30f
                    values = event.values.clone()
                    for(i in values.indices) {
                        values[i] *= const
                        if(abs(values[i]) > limit)
                            values[i] = sign(values[i])*limit
                    }
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                return
            }
        })
        sensorManager.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    fun stop() {
        stopped = true
        sensorManager.unregisterListener(mySensorEventListener, sensor)
    }

}