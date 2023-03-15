package com.handikapp.parentbox

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.CountTimer
import kotlinx.android.synthetic.main.activity_interval_timer.*


class IntervalTimer : AppCompatActivity() {
    private var doublebackexit = false
    lateinit var TimerProgress : ProgressBar
    lateinit var TextCount : TextView
    lateinit var ContinueBTN : Button
    lateinit var ReadyText : TextView
    var hour : Int = 0
    var minute : Int = 0
    var totaltime : Int = 0
    var time : Int = 0
    var countDownTimer : CountDownTimer?= null
    var TotalTime : Int = 0
    var TimeS : Int = 0
    var TimerFinished : Boolean = false


    var TaskID : String = ""
    var URL : String = ""
    lateinit var database : FirebaseDatabase
    lateinit var TasksRef: DatabaseReference

    private val TAG: String = IntervalTimer::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interval_timer)

        TimerProgress = findViewById(R.id.interval_timer_progressBar)
        TextCount = findViewById(R.id.interval_timer_textViewCount)
        ContinueBTN = findViewById(R.id.interval_timer_continue_btn)
        ReadyText = findViewById(R.id.interval_ready_text)

        val sharedPreferences2 = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        TaskID = sharedPreferences2.getString(Constants.Task_two, "")!!


        database = FirebaseDatabase.getInstance()
        TasksRef = database.reference.child("Tasks").child(TaskID)
        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    URL = snapshot.child("image").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@IntervalTimer,"Update","Error : "+error)
            }

        })


        val sharedPreferences = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
        TotalTime = sharedPreferences.getInt(Constants.TOTAL_TIME, 0)
        TimeS = sharedPreferences.getInt(Constants.TIME, 0)
        TimerFinished = sharedPreferences.getBoolean(Constants.Timer_Finished, false)
        hour = sharedPreferences.getInt(Constants.Task_Interval_hour,0)
        minute = sharedPreferences.getInt(Constants.Task_Interval_minute,0)

        ContinueBTN.setOnClickListener {
            val sharedPreferences = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            CountTimer.stopService(this)
            val intent = Intent(this,SingleTasks::class.java)
            intent.putExtra("task_id",TaskID)
            intent.putExtra("img_url",URL)
            intent.putExtra("task_no","2")
            startActivity(intent)
            finish()
        }


        if (TimerFinished)
        {
            val sharedPreferences = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            CountTimer.stopService(this)
            TimerProgress.visibility = View.GONE
            TextCount.visibility = View.GONE
            ContinueBTN.visibility = View.VISIBLE
            ReadyText.visibility = View.VISIBLE
        }
        else {
            startCounting()
        }

    }

    private fun startCounting() {
        var mili : Int = 0
        var mili2 : Int = 0
        mili = hour * 3600
        mili2 = minute * 60
        if(TimeS > 1)
            totaltime = TimeS*1000
        else
            totaltime = (mili + mili2)*1000

        if (TotalTime > 1 )
            TimerProgress.max = TotalTime/1000
        else
            TimerProgress.max = totaltime/1000


        countDownTimer = object  : CountDownTimer(totaltime.toLong(), 1000){
            override fun onFinish() {
                TimerProgress.visibility = View.GONE
                TextCount.visibility = View.GONE
                ContinueBTN.visibility = View.VISIBLE
                ReadyText.visibility = View.VISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {
                TimerProgress.progress = Math.round(millisUntilFinished * 0.001f)
                time =  Math.round(millisUntilFinished * 0.001f)
                val hours: Int = time / 3600
                val minutes: Int = time % 3600 / 60
                val seconds: Int = time % 60

                val sharedPreferences = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt(Constants.TIME, time)
                editor.putInt(Constants.TOTAL_TIME, totaltime)
                editor.apply()
                TextCount.text = hours.toString() +"h "+minutes.toString() + "m "+seconds.toString()+"s"
            }
        }.start()

        countDownTimer!!.start()
    }


    override fun onBackPressed() {
        if(doublebackexit)
        {
            CountTimer.startService(this, "Tap here to see timer...", time)
            var LeftClass: String
            var LeftTask_NO: String
            var LeftTask_ID: String
            var LeftQuestion_NO: String

            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(Constants.LeftClass, "IntervalTimer")
            editor.putString(Constants.LeftTask_NO, "")
            editor.putString(Constants.LeftTask_ID, "")
            editor.putInt(Constants.LeftQuestion_NO, 0)
            editor.putString(Constants.LeftImg_URL, "")
            editor.apply()
            moveTaskToBack(true)
        }
        doublebackexit = true
        Constants.setAlert(this,"Warning","You can't go back!!!")
        Handler().postDelayed({
            doublebackexit = false
        },
                5000)
    }



    override fun onStart() {
        CountTimer.stopService(this)
        super.onStart()
    }


    override fun onStop() {

            CountTimer.startService(this, "Tap here to see timer...", time)

            var LeftClass: String
            var LeftTask_NO: String
            var LeftTask_ID: String
            var LeftQuestion_NO: String

            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(Constants.LeftClass, "IntervalTimer")
            editor.putString(Constants.LeftTask_NO, "")
            editor.putString(Constants.LeftTask_ID, "")
            editor.putInt(Constants.LeftQuestion_NO, 0)
            editor.putString(Constants.LeftImg_URL, "")
            editor.apply()
            super.onStop()

    }
}