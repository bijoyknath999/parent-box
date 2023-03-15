package com.handikapp.parentbox

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.handikapp.parentbox.Fragments.Quiz.FragmentAddDeatils
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_create_task.*

class CreateTask : AppCompatActivity() {
    private var doublebackexit = false
    private lateinit var mToolbar : Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)


        mToolbar = findViewById(R.id.create_task_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Create Task"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)

        supportFragmentManager.beginTransaction().replace(R.id.create_task_fragment, FragmentAddDeatils()).commit()


    }
    fun setActionBarTitle(title: String) {
        mToolbar.title = title
    }

    override fun onBackPressed() {
        var task_id : String
        val sharedPreferences = getSharedPreferences("sharedPreferences_createtask", MODE_PRIVATE)
        task_id = sharedPreferences.getString(Constants.CreateTaskID,"").toString()

        if(doublebackexit)
        {
            if (!task_id.isEmpty())
            {
                showdialog(task_id)
            }
            else
            {
                super.onBackPressed()
            }
        }
        else
        {
            if (!task_id.isEmpty())
            {
                Constants.setAlert(this,"Warning","Please click BACK again to delete this task!!")
            }
            else
            {
                Constants.setAlert(this,"Warning","Please click BACK again to go back")
            }
        }
        doublebackexit = true

        Handler().postDelayed({
            doublebackexit = false
        },
                2000)
    }

    private fun showdialog(taskID: String) {
        val builder = android.app.AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Deletion Warning")
        builder.setMessage("Are you want to delete this task?")
        builder.setIcon(R.drawable.ic_report)
        builder.setCancelable(true)
        builder.setOnCancelListener{
            it.dismiss()
        }


        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            var ImageUrl : String
            val sharedPreferences = getSharedPreferences("sharedPreferences_createtask", MODE_PRIVATE)
            ImageUrl = sharedPreferences.getString(Constants.CreateTaskImageUrl,"").toString()

            if (!ImageUrl.isEmpty())
            {
                DeleteChilds(taskID,ImageUrl)
            }
            else{
              DeleteChildsWithNOImage(taskID)
            }
        }

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: android.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    private fun DeleteChildsWithNOImage(taskID: String) {

        lateinit var database : FirebaseDatabase
        lateinit var ChildsRef: DatabaseReference
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Tasks")
        ChildsRef.child(taskID).removeValue()
                .addOnSuccessListener{
                    Constants.setAlert(this,"Update","Task Deleted!!")
                    val sharedPreferences = getSharedPreferences("sharedPreferences_createtask", AppCompatActivity.MODE_PRIVATE)
                    val editor = sharedPreferences?.edit()
                    editor?.clear()
                    editor?.commit()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Constants.setAlert(this,"Update","Error : " + it)
                }

    }

    private fun DeleteChilds(taskID: String, image: String) {
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
        photoRef.delete()
                .addOnSuccessListener(OnSuccessListener { task->

                    lateinit var database : FirebaseDatabase
                    lateinit var ChildsRef: DatabaseReference
                    database = FirebaseDatabase.getInstance()
                    ChildsRef = database.reference.child("Tasks")
                    ChildsRef.child(taskID).removeValue()
                            .addOnSuccessListener{
                                Constants.setAlert(this,"Update","Task Deleted!!")
                                val sharedPreferences = getSharedPreferences("sharedPreferences_createtask", AppCompatActivity.MODE_PRIVATE)
                                val editor = sharedPreferences?.edit()
                                editor?.clear()
                                editor?.commit()
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Constants.setAlert(this,"Update","Error : " + it)
                            }
                })
                .addOnFailureListener {
                    e ->
                    Constants.setAlert(this,"Update","Error : " + e.message)
                    lateinit var database : FirebaseDatabase
                    lateinit var ChildsRef: DatabaseReference
                    database = FirebaseDatabase.getInstance()
                    ChildsRef = database.reference.child("Tasks")
                    ChildsRef.child(taskID).removeValue()
                            .addOnSuccessListener{
                                Constants.setAlert(this,"Update","Task Deleted!!")
                                val sharedPreferences = getSharedPreferences("sharedPreferences_createtask", AppCompatActivity.MODE_PRIVATE)
                                val editor = sharedPreferences?.edit()
                                editor?.clear()
                                editor?.commit()
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Constants.setAlert(this,"Update","Error : " + it)
                            }
                }
    }

}