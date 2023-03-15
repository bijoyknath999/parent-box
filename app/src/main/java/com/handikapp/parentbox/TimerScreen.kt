package com.handikapp.parentbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.CountTimer

class TimerScreen : AppCompatActivity() {
    lateinit var IntervalTime : TextView
    lateinit var ContinueBTN : Button
    lateinit var HourPicker: NumberPicker
    lateinit var MinutePicker : NumberPicker
    var hour : Int = 0
    var minute : Int = 0
    var ClassName : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_screen)

        if (intent.hasExtra("class")) {
            ClassName = intent.getStringExtra("class")!!
        }


        IntervalTime = findViewById(R.id.timer_interval_time)
        ContinueBTN = findViewById(R.id.timer_continue_btn)
        HourPicker = findViewById(R.id.timePicker)
        MinutePicker = findViewById(R.id.timePicker2)

        CountTimer.stopService(this)
        val sharedPreferences = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()

        IntervalTime.text = "Interval Time : "+hour+":"+minute

        HourPicker.minValue = 0
        HourPicker.maxValue = 12
        HourPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        HourPicker.wrapSelectorWheel = true

        HourPicker.setOnValueChangedListener { numberPicker, i, i2 ->
            hour = i2
            IntervalTime.text = "Interval Time : "+hour+":"+minute
        }


        MinutePicker.minValue = 10
        MinutePicker.maxValue = 59
        MinutePicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        MinutePicker.wrapSelectorWheel = true

        MinutePicker.setOnValueChangedListener { numberPicker, i3, i4 ->
            minute = i4
            IntervalTime.text = "Interval Time : "+hour+":"+minute
        }


        ContinueBTN.setOnClickListener {
            if (hour != 0 || minute != 0)
            {
                val sharedPreferences = getSharedPreferences(
                        "sharedPreferences_timer",
                        MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putInt(Constants.Task_Interval_hour, hour)
                editor.putInt(Constants.Task_Interval_minute, minute)
                editor.apply()
                startActivity(Intent(this, CluesScreen::class.java))
            }
            else
            {
                IntervalTime.text = "Please Select interval time!!!"
            }

        }
    }

    override fun onBackPressed() {
        if (ClassName == "RandomsTask")
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        val sharedPreferences = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
        super.onStart()
    }



}