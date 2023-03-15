package com.handikapp.parentbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.handikapp.parentbox.Utils.Constants

class CluesScreen : AppCompatActivity() {
    lateinit var Clues : TextView
    lateinit var text : String
    lateinit var BTN : Button
    lateinit var TaskIntent : Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clues_screen)

        Clues = findViewById(R.id.clues_edittext)
        BTN = findViewById(R.id.clues_continue_btn)


        Clues.setOnFocusChangeListener { view, b ->
            if (b)
            {
                Clues.hint = ""
            }
            else
            {
                Clues.hint = "Use minimum words and play with your childs imagination. Let your child think and search for the gift."
            }
        }
        BTN.setOnClickListener {
            text = Clues.text.toString()
            if (TextUtils.isEmpty(text))
            {
                Clues.error = "Clues Empty!!!"
            }
            else
            {
                var Class : String
                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                Class = sharedPreferences.getString(Constants.Class, "")!!
                val editor = sharedPreferences.edit()
                editor.putString(Constants.Clues_Text, text)
                editor.apply()
                if (Class == "OurTasks")
                    TaskIntent = Intent(this,OurTasks::class.java)
                else if(Class == "ParentsTaskList")
                    TaskIntent = Intent(this, ParentsTaskList::class.java)
                else if(Class == "RandomsTask")
                    TaskIntent = Intent(this, RandomsTask::class.java)
                else
                    TaskIntent = Intent(this,OurTasks::class.java)

                TaskIntent.putExtra("task_two","2")
                startActivity(TaskIntent)
            }
        }

    }
}