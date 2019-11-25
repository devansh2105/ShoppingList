package com.ait.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*



class SplashActivity : AppCompatActivity() {

    lateinit var timer: Timer
    var i =1
    inner class updateProgress :  TimerTask () {
        override fun run () {

                if(i< 100) {
                    runOnUiThread {
                        tvProgPerc.text = "$i%"
                    }
                    progressBar.setProgress(i)
                    i++
                }
                else {
                    timer.cancel()
                    Log.d("TIMER",getString(R.string.loop_entered_here))
                    val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    this@SplashActivity.startActivity(mainIntent)
                    this@SplashActivity.finish()
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar.setProgress(0)
        tvProgPerc.setText("")

        timer = Timer()
        timer.schedule(updateProgress(), 0,  50)
    }
}