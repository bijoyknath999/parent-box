package com.handikapp.parentbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import com.handikapp.parentbox.Utils.Constants

class ChildsMessage : AppCompatActivity() {

    private var doublebackexit = false
    lateinit var ContinueBTN : Button
    lateinit var textView : TextView
    var TaskID : String = ""
    var URL : String = ""
    var FreeTask_ID : String = ""
    var FreeMode : Boolean = false
    lateinit var database : FirebaseDatabase
    lateinit var TasksRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_childs_message)

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        TaskID = sharedPreferences.getString(Constants.Task_One, "")!!
        FreeMode = sharedPreferences.getBoolean(Constants.FreeMode,false)
        FreeTask_ID = sharedPreferences.getString(Constants.FreeTask_ID,"")!!

        textView = findViewById(R.id.child_message_text)



        if (FreeMode)
        {
            textView.text = "Hey Dear, if you complete this task You will get trophy."+
                    "\nLet's complete it"
            database = FirebaseDatabase.getInstance()
            TasksRef = database.reference.child("Tasks").child(FreeTask_ID)
            TasksRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        URL = snapshot.child("image").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constants.setAlert(this@ChildsMessage,"Update","Error : "+error)
                }

            })


            ContinueBTN = findViewById(R.id.childs_message_continue)
            ContinueBTN.setOnClickListener{
                val intent = Intent(this,SingleTasks::class.java)
                intent.putExtra("task_id",FreeTask_ID)
                intent.putExtra("img_url",URL)
                startActivity(intent)
                finish()
            }
        }
        else
        {
            database = FirebaseDatabase.getInstance()
            TasksRef = database.reference.child("Tasks").child(TaskID)
            TasksRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        URL = snapshot.child("image").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constants.setAlert(this@ChildsMessage,"Update","Error : "+error)
                }

            })



            ContinueBTN = findViewById(R.id.childs_message_continue)
            ContinueBTN.setOnClickListener{
                val intent = Intent(this,SingleTasks::class.java)
                intent.putExtra("task_id",TaskID)
                intent.putExtra("img_url",URL)
                intent.putExtra("task_no","1")
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStop() {
        var LeftClass : String
        var LeftTask_NO : String
        var LeftTask_ID : String
        var LeftQuestion_NO : String

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LeftClass, "ChildsMessage")
        editor.putString(Constants.LeftTask_NO, "")
        editor.putString(Constants.LeftTask_ID, "")
        editor.putString(Constants.LeftImg_URL, "")
        editor.putInt(Constants.LeftQuestion_NO, 0)
        editor.apply()
        super.onStop()
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