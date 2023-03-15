package com.handikapp.parentbox.Utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.handikapp.parentbox.IntervalTimer
import com.handikapp.parentbox.R


class CountTimer : Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"
    var time2 : Int = 0
    var hours : Int = 0
    var minutes : Int = 0
    var seconds : Int = 0
    var mili : Int = 0
    var mili2 : Int = 0
    var Totaltime : Int = 0
    var countDownTimer2 : CountDownTimer?= null
    var TotalTime : Int = 0
    var Time : Int = 0
    var Stop : Boolean = true


    companion object {


        fun startService(context: Context, message: String,time : Int) {
            val startIntent = Intent(context, CountTimer::class.java)
            startIntent.putExtra("inputExtra", message)
            startIntent.putExtra("Time",time)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, CountTimer::class.java)
            context.stopService(stopIntent)
        }


    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int):Int {
//do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        Time = intent?.getIntExtra("Time",0)!!
        Stop = intent.getBooleanExtra("stop",true)

        createNotificationChannel()
        startCounting(this,Time)
        norify("Timer is running", input!!)
        //stopSelf();


        return START_NOT_STICKY
    }

    private fun norify(title: String, message: String)
    {
        val notificationIntent = Intent(this, IntervalTimer::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    fun startCounting(context: Context, time: Int){

        var t = time*1000
        countDownTimer2 = object  : CountDownTimer(t.toLong(), 1000){
            override fun onFinish() {
                val sharedPreferences = context.getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean(Constants.Timer_Finished, true)
                editor.apply()
                norify("Your task is ready", "Tap here to check out task")
            }

            override fun onTick(millisUntilFinished: Long) {
                time2 =  Math.round(millisUntilFinished * 0.001f)

                val sharedPreferences = context.getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt(Constants.TIME, time2)
                editor.apply()

            }
        }.start()

        countDownTimer2!!.start()
    }
}