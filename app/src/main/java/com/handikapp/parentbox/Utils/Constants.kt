package com.handikapp.parentbox.Utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.CreateTask
import com.handikapp.parentbox.FirebaseDatabase.SelectedChildModel
import com.handikapp.parentbox.R
import com.tapadoo.alerter.Alert
import com.tapadoo.alerter.Alerter
import java.io.IOException

object Constants {
    var check : Boolean = false
    var name : String = ""
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var SelectedChildRef : DatabaseReference
    lateinit var TaskRef : DatabaseReference
    var age : Int = 0
    var id : String = ""
    var totalchild : Int = 0
    const val TOTAL_TIME = "total_timer"
    const val TIME = "time"
    const val Timer_Finished = "timer_finished"
    const val Task_Interval_hour = "interval_hour"
    const val Task_Interval_minute = "interval_minute"
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "Server_key"
    const val CONTENT_TYPE = "application/json"
    const val ChildName = "child_name"
    const val ChildID = "child_id"
    const val ChildAge = "child_age"
    const val ChildSelected = "ChildSelected"
    const val TotalChild = "totalchild"
    const val ImagePath = "image_path"
    const val Task_Valid = "task_valid"
    const val Task_One = "task_one"
    const val Task_two = "task_two"
    const val Clues_Text = "clues_text"
    const val Class = "class"
    const val Lock = "lock"
    const val Task_One_Correct = "task_one_correct"
    const val Task_Two_Correct = "task_two_correct"
    const val Task_One_Title = "task_one_title"
    const val Task_Two_Title = "task_two_title"
    const val LeftClass = "leftclass"
    const val LeftTask_NO = "LeftTask_NO"
    const val LeftTask_ID = "LeftTask_ID"
    const val LeftQuestion_NO = "LeftQuestion_NO"
    const val LeftImg_URL = "LeftImg_URL"
    const val FreeTask_ID = "FreeTask_ID"
    const val FreeTask_Title = "FreeTask_Title"
    const val FreeTask_PublisherID = "FreeTask_PublisherID"
    const val FreeTask_ImageUrl = "FreeTask_ImageUrl"
    const val FreeMode = "FreeMode"
    const val RandomIndex = "RandomIndex"
    const val AdminMail = "admin_mail"
    const val ABoutUs = "about_us"
    const val GiftingTips = "gifting_tips"
    const val Tutorial = "tutorial"
    const val RATED = "rated"
    const val CreateTaskID = "createtaskid"
    const val CreateTaskImageUrl = "CreateTaskImageUrl"
    var valid : Boolean = false


    fun setAlert(context: Context, title : String, message : String) : Alert? {
       val alert =  Alerter.create(context as Activity)
            .setTitle(title)
            .setText(message)
            .setIcon(R.drawable.app_icon_512)
            .setBackgroundColor(R.color.quizbgColor)
            .show()
        return alert
    }


    fun checkSelectedChild(context: Context) : Boolean{
        val sharedPreferences = context.getSharedPreferences("sharedPreferences_child", AppCompatActivity.MODE_PRIVATE)
        var valid : Boolean = sharedPreferences.getBoolean(ChildSelected,false)
        return valid
    }

    fun getSelectedChild(context: Context){
        var currentID = mAuth.currentUser!!.uid
        SelectedChildRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentID).child("selectedChild")
        SelectedChildRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    name = snapshot.child("name").value.toString()
                    id = snapshot.child("id").value.toString()
                    age = snapshot.child("age").value.toString().toInt()
                    totalchild = snapshot.child("totalchild").value.toString().toInt()
                    val sharedPreferences = context.getSharedPreferences("sharedPreferences_child", AppCompatActivity.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(ChildName,name)
                    editor.putString(ChildID,id)
                    editor.putInt(ChildAge, age)
                    editor.putInt(TotalChild, totalchild)
                    editor.putBoolean(ChildSelected,true)
                    editor.apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}
