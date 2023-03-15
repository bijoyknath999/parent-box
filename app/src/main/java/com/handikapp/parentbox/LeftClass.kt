package com.handikapp.parentbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.handikapp.parentbox.Utils.Constants

class LeftClass : AppCompatActivity() {
    private var doublebackexit = false
    lateinit var BTN : Button
    var LeftTaskID : String = ""
    var LeftTask_No : String = ""
    var LeftQuestion_No : Int = 0
    var LeftImgURL : String = ""
    var ClassName : String = ""

    lateinit var LeftIntent : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_left_class)

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        ClassName = sharedPreferences.getString(Constants.LeftClass, "")!!
        LeftTask_No = sharedPreferences.getString(Constants.LeftTask_NO,"")!!
        LeftTaskID = sharedPreferences.getString(Constants.LeftTask_ID,"")!!
        LeftImgURL = sharedPreferences.getString(Constants.LeftImg_URL,"")!!
        LeftQuestion_No = sharedPreferences.getInt(Constants.LeftQuestion_NO,0)


        BTN = findViewById(R.id.left_class_btn_continue)

        BTN.setOnClickListener{

            if (ClassName == "ChildsMessage")
                LeftIntent = Intent(this,ChildsMessage::class.java)
            else if (ClassName == "SingleTasks") {
                LeftIntent = Intent(this, SingleTasks::class.java)
                LeftIntent.putExtra("task_id",LeftTaskID)
                LeftIntent.putExtra("img_url",LeftImgURL)
                LeftIntent.putExtra("task_no",LeftTask_No)
                LeftIntent.putExtra("question_no",LeftQuestion_No)
            }
            else if (ClassName == "QuizScreen") {
                LeftIntent = Intent(this, QuizScreen::class.java)
                LeftIntent.putExtra("task_id",LeftTaskID)
                LeftIntent.putExtra("task_no",LeftTask_No)
                LeftIntent.putExtra("question_no",LeftQuestion_No)
            }
            else if (ClassName == "Results")
                LeftIntent = Intent(this,Results::class.java)
            else if (ClassName == "ResultsWithClues")
                LeftIntent = Intent(this,ResultsWithClues::class.java)
            else if (ClassName == "GiftingShare")
                LeftIntent = Intent(this,GiftingShare::class.java)
            else if (ClassName == "IntervalTimer")
                LeftIntent = Intent(this,IntervalTimer::class.java)
            else {
                val sharedPreferences2 = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
                val editor2 = sharedPreferences2.edit()
                editor2.clear()
                editor2.commit()
                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.commit()
                LeftIntent = Intent(this, MainActivity::class.java)
            }

            startActivity(LeftIntent)
            finish()

        }
    }

    override fun onBackPressed() {
        if(doublebackexit)
        {
            ShowExitDialog()
        }
        doublebackexit = true
        Constants.setAlert(this,"Warning","You can't go back!!!")
        Handler().postDelayed({
            doublebackexit = false
        },
                2000)
    }


    private fun ShowExitDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_title_dialog2, null)
        builder.setCustomTitle(dialogView)
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            val sharedPreferences2 = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
            val editor2 = sharedPreferences2.edit()
            editor2.clear()
            editor2.commit()
            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
}