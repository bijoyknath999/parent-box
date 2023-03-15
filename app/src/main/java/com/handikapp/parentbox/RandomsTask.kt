package com.handikapp.parentbox

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.Tasks
import com.handikapp.parentbox.Utils.AgeCheck
import com.handikapp.parentbox.Utils.Constants
import java.util.*
import java.util.concurrent.locks.Lock


class RandomsTask : AppCompatActivity() {

    private var Shown: Boolean = false
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var TasksRef: DatabaseReference
    var Mode : String = ""
    lateinit var childRef: DatabaseReference
    var Task_Two : String = ""
    var child_age: Int = 0
    var TotalChild: Int = 0
    var ClassName : String = ""
    var index : Int = 0
    private lateinit var progressDialog: ProgressDialog




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_randoms_task)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        TasksRef = FirebaseDatabase.getInstance().reference.child("Tasks")
        childRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!)

        val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        child_age = sharedPreferences.getInt(Constants.ChildAge,0)
        TotalChild = sharedPreferences.getInt(Constants.TotalChild,0)


        if (intent.hasExtra("mode")) {
            Mode = intent.getStringExtra("mode")!!
        }

        if (intent.hasExtra("task_two")) {
            Task_Two = intent.getStringExtra("task_two")!!
        }
        if (intent.hasExtra("class")) {
            ClassName = intent.getStringExtra("class")!!
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading.....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.max = 100
        progressDialog.setCancelable(true)

    }

    private fun RandomTasks() {
        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val random = Random()
                    val index : Int = random.nextInt(snapshot.childrenCount.toInt())
                    val sharedPreferences =
                            getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt(Constants.RandomIndex, index)
                    editor.apply()
                    var count = 0
                    for (dsnapshot in snapshot.children) {
                        if (count == index) {
                            val task: Tasks = dsnapshot.getValue(Tasks::class.java)!!
                            if (AgeCheck.getCheck(task.getAgeLimit()!!, child_age)) {
                                if (snapshot.child(task.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                    progressDialog.dismiss()
                                    val sharedPreferences =
                                            getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString(Constants.Task_One, task.getTaskID())
                                    editor.putString(Constants.Task_One_Title, task.getTitle())
                                    editor.apply()
                                    val TimerIntent = Intent(this@RandomsTask, TimerScreen::class.java)
                                    TimerIntent.putExtra("class","RandomsTask")
                                    startActivity(TimerIntent)
                                    finish()
                                }
                                else
                                {
                                    RandomTasks()
                                }
                            }
                            else{
                                if (task.getPublisher() == "admin")
                                {
                                    if (snapshot.child(task.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                        progressDialog.dismiss()
                                        val sharedPreferences =
                                                getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString(Constants.Task_One, task.getTaskID())
                                        editor.putString(Constants.Task_One_Title, task.getTitle())
                                        editor.apply()
                                        val TimerIntent = Intent(this@RandomsTask, TimerScreen::class.java)
                                        TimerIntent.putExtra("class","RandomsTask")
                                        startActivity(TimerIntent)
                                        finish()                                    }
                                    else
                                    {
                                        RandomTasks()
                                    }
                                }
                                else
                                {
                                    RandomTasks()
                                }
                            }
                            return
                        }
                        count++
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@RandomsTask,"Update","Error : "+error)
                progressDialog.dismiss()            }
        })
    }

    private fun RandomTasks2(index: Int) {


        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val random = Random()
                    var index2 : Int = random.nextInt(snapshot.childrenCount.toInt())
                    var count = 0


                    if (index == index2) {
                        index2 = random.nextInt(snapshot.childrenCount.toInt())
                    }

                    for (dsnapshot in snapshot.children) {
                        /*if (index == index2)
                            index2 = index2+1*/

                        var task_id : String
                        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                        task_id = sharedPreferences.getString(Constants.Task_One, "")!!
                        if (count == index2) {
                            val task: Tasks = dsnapshot.getValue(Tasks::class.java)!!
                            if (AgeCheck.getCheck(task.getAgeLimit()!!, child_age)) {
                                if (task_id != task.getTaskID()){
                                    if (snapshot.child(task.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                        progressDialog.dismiss()
                                        val sharedPreferences =
                                                getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString(Constants.Task_two, task.getTaskID())
                                        editor.putString(Constants.Task_Two_Title, task.getTitle())
                                        editor.apply()
                                        val LockIntent = Intent(this@RandomsTask, LockScreen::class.java)
                                        LockIntent.putExtra("class", "RandomsTask")
                                        startActivity(LockIntent)

                                    }
                                    else{
                                        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                        val index = sharedPreferences.getInt(Constants.RandomIndex, 0)
                                        RandomTasks2(index)
                                    }
                                }
                                else
                                {
                                    val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                    val index = sharedPreferences.getInt(Constants.RandomIndex, 0)
                                    RandomTasks2(index)
                                }
                            }
                            else
                            {
                                if (task.getPublisher() == "admin")
                                {
                                    if (task_id != task.getTaskID()){
                                        if (snapshot.child(task.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                            progressDialog.dismiss()
                                            val sharedPreferences =
                                                    getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                            val editor = sharedPreferences.edit()
                                            editor.putString(Constants.Task_two, task.getTaskID())
                                            editor.putString(Constants.Task_Two_Title, task.getTitle())
                                            editor.apply()
                                            val LockIntent = Intent(this@RandomsTask, LockScreen::class.java)
                                            LockIntent.putExtra("class", "RandomsTask")
                                            startActivity(LockIntent)

                                        }
                                        else{
                                            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                            val index = sharedPreferences.getInt(Constants.RandomIndex, 0)
                                            RandomTasks2(index)
                                        }
                                    }
                                    else
                                    {
                                        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                        val index = sharedPreferences.getInt(Constants.RandomIndex, 0)
                                        RandomTasks2(index)
                                    }
                                }
                                else {
                                    val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                    val index = sharedPreferences.getInt(Constants.RandomIndex, 0)
                                    RandomTasks2(index)
                                }
                            }
                            return
                        }
                        count++
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@RandomsTask,"Update","Error : "+error)
                progressDialog.dismiss()
            }
        })

    }

    private fun QuickMode() {
        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot != null) {
                    if (snapshot.childrenCount.toInt()>0) {
                        val random = Random()
                        index = random.nextInt(snapshot.childrenCount.toInt())
                    }
                    else
                    {
                        index = 0
                    }
                    var count = 0
                    for (dataSnapshot1 in snapshot.children) {
                        val task : Tasks = dataSnapshot1.getValue(Tasks::class.java)!!
                        if (count == index) {
                            if (AgeCheck.getCheck(task.getAgeLimit()!!, child_age))
                            {
                                if (snapshot.child(task.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                    progressDialog.dismiss()
                                    val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString(Constants.FreeTask_ID, task.getTaskID())
                                    editor.putString(Constants.FreeTask_Title, task.getTitle())
                                    editor.putString(Constants.FreeTask_ImageUrl, task.getImage())
                                    editor.putBoolean(Constants.Task_Valid, true)
                                    editor.putBoolean(Constants.FreeMode, true)
                                    editor.apply()
                                    val LockIntent = Intent(this@RandomsTask, LockScreen::class.java)
                                    LockIntent.putExtra("class", "RandomsTask")
                                    LockIntent.putExtra("mode", "free")
                                    startActivity(LockIntent)
                                }
                                else
                                {
                                    QuickMode()
                                }
                            }
                            else
                            {
                                if(task.getPublisher() == "admin")
                                {
                                    if (snapshot.child(task.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                        progressDialog.dismiss()
                                        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString(Constants.FreeTask_ID, task.getTaskID())
                                        editor.putString(Constants.FreeTask_Title, task.getTitle())
                                        editor.putString(Constants.FreeTask_ImageUrl, task.getImage())
                                        editor.putBoolean(Constants.Task_Valid, true)
                                        editor.putBoolean(Constants.FreeMode, true)
                                        editor.apply()
                                        val LockIntent = Intent(this@RandomsTask, LockScreen::class.java)
                                        LockIntent.putExtra("class", "RandomsTask")
                                        LockIntent.putExtra("mode", "free")
                                        startActivity(LockIntent)
                                    }
                                    else
                                    {
                                        QuickMode()
                                    }
                                }
                                else {
                                    QuickMode()
                                }
                            }

                            return
                        }
                        count++
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@RandomsTask,"Update","Error : "+error)
                progressDialog.dismiss()            }
        })
    }


    private fun ShowDialog() {

        childRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("Childs"))
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(this@RandomsTask)
                        .setView(dialogView)
                        .setCancelable(true)
                        .setOnCancelListener {
                            startActivity(Intent(this@RandomsTask,MainActivity::class.java))
                            finish()
                        }
                        .show()

                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please select child"
                    btn.setOnClickListener {
                        val intent = Intent(this@RandomsTask,SelectChild::class.java)
                        intent.putExtra("class","RandomsTask")
                        intent.putExtra("mode",Mode)
                        startActivity(intent)
                        finish()

                    }

                    customDialog.show()
                }
                else
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(this@RandomsTask)
                        .setView(dialogView)
                        .setCancelable(true)
                        .setOnCancelListener {
                            startActivity(Intent(this@RandomsTask,MainActivity::class.java))
                            finish()
                        }
                        .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please, Add your child first"
                    btn.setOnClickListener {
                        val intent = Intent(this@RandomsTask,AddChild::class.java)
                        startActivity(intent)

                    }
                    customDialog.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onStart() {

        Constants.getSelectedChild(this)

        if (!Constants.checkSelectedChild(this)) {
            if(!Shown) {
                Shown = true
                ShowDialog()
            }
        }
        else
        {
            if (Task_Two == "2") {
                progressDialog.show()
                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                val index = sharedPreferences.getInt(Constants.RandomIndex, 0)
                RandomTasks2(index)
            } else if (Task_Two != "2" && ClassName == "ChooseImage" && Mode != "quick") {
                progressDialog.show()
                RandomTasks()
            }
            else if (Task_Two != "2" && TotalChild == 0 && ClassName != "ChooseImage" && Mode == "quick") {
                progressDialog.show()
                QuickMode()
            }
            else if (Task_Two != "2" && TotalChild == 1 && ClassName != "ChooseImage" && Mode == "quick") {
                progressDialog.show()
                QuickMode()
            }
            else if (Task_Two != "2" && ClassName != "ChooseImage" && TotalChild > 1) {
                if (ClassName != "SelectChild")
                {
                    if(!Shown) {
                        Shown = true
                        ShowDialog2()
                    }
                }
                else if (Mode == "quick")
                {
                    QuickMode()
                }
            }
            else if (Task_Two != "2" && Mode != "quick" && ClassName != "ChooseImage" && TotalChild == 0) {
                val intent = Intent(this, ChooseImage::class.java)
                intent.putExtra("class", "RandomsTask")
                startActivity(intent)
                finish()
            }
            else if (Task_Two != "2" && Mode != "quick" && ClassName != "ChooseImage" && TotalChild == 1) {
                val intent = Intent(this, ChooseImage::class.java)
                intent.putExtra("class", "RandomsTask")
                startActivity(intent)
                finish()
            }
        }
        super.onStart()
    }

    private fun ShowDialog2() {
        var name : String = ""
        val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        name = sharedPreferences.getString(Constants.ChildName,"")!!
        val builder3 = AlertDialog.Builder(this)
        builder3.setTitle("Is it for "+name+ "?")
        builder3.setIcon(R.drawable.ic_report)
        builder3.setCancelable(true)
        builder3.setOnCancelListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        if (Mode == "quick")
        {
            builder3.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                progressDialog.show()
                QuickMode()
            }
        }
        else
        {
            builder3.setPositiveButton("Yes") { dialog, which ->
                val intent = Intent(this, ChooseImage::class.java)
                intent.putExtra("class", "RandomsTask")
                startActivity(intent)
                finish()
            }
        }

        builder3.setNegativeButton("No") { dialog, which ->
            val ChildIntent = Intent(this,SelectChild::class.java)
            ChildIntent.putExtra("class","RandomsTask")
            ChildIntent.putExtra("mode",Mode)
            startActivity(ChildIntent)
            finish()
        }
        builder3.show()
    }

    override fun onBackPressed() {
        progressDialog.dismiss()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
        super.onBackPressed()
    }

}