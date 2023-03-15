package com.handikapp.parentbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.handikapp.parentbox.Utils.Constants

class LockScreen : AppCompatActivity() {

    var ClassName : String = ""
    var FreeMode : Boolean = false

    lateinit var LockBTN : FloatingActionButton
    lateinit var LockDesc : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)



        if (intent.hasExtra("class")) {
            ClassName = intent.getStringExtra("class")!!
        }

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        FreeMode = sharedPreferences.getBoolean(Constants.FreeMode,false)

        LockDesc = findViewById(R.id.lock_description)
        if (FreeMode)
        {
            LockDesc.text = "Now lock the app and give the device to your child. They will complete the task and collect virtual trophies."
        }

        LockBTN = findViewById(R.id.lock_fab)
        LockBTN.setOnClickListener {
            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(Constants.Lock, true)
            editor.apply()
            val intent = Intent(this,ChildsMessage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        if (ClassName == "RandomsTask")
        {
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        super.onBackPressed()
    }
}