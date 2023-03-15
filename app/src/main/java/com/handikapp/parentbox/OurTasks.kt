package com.handikapp.parentbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.Tasks
import com.handikapp.parentbox.RecyclerView.TasksLIstAdapter
import com.handikapp.parentbox.Utils.AgeCheck
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_our_tasks.*


class OurTasks: AppCompatActivity() {
    private var Shown: Boolean = false
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var TopicsRef: DatabaseReference
    lateinit var childRef: DatabaseReference
    private var topicsLIstAdapter:TasksLIstAdapter? = null
    private var topics: List<Tasks>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var OurProgressBar : ProgressBar
    var child_age: Int = 0
    var TotalChild: Int = 0
    var Task_Two : String = ""
    var Mode : String = ""
    var ClassName : String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_our_tasks)


        if (intent.hasExtra("task_two")) {
            Task_Two = intent.getStringExtra("task_two")!!
        }

        if (intent.hasExtra("mode")) {
            Mode = intent.getStringExtra("mode")!!
        }

        if (intent.hasExtra("class")) {
            ClassName = intent.getStringExtra("class")!!
        }
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid

        TopicsRef = FirebaseDatabase.getInstance().reference.child("Tasks")
        childRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!)


        mToolbar = findViewById(R.id.our_topics_page_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Available Tasks"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.topicsList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        Swipe = findViewById(R.id.our_tasks_swipeRefreshLayout)
        Not_Found = findViewById(R.id.our_tasks_not_found)
        OurProgressBar = findViewById(R.id.our_tasks_progressbar)

        val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        child_age = sharedPreferences.getInt(Constants.ChildAge,0)
        TotalChild = sharedPreferences.getInt(Constants.TotalChild,0)


        topics = ArrayList()

        Constants.setAlert(this,"Alert","select the first task")
        if (child_age != 0 && !ClassName.isEmpty()) {
            Swipe.setOnRefreshListener(OnRefreshListener {
                Constants.setAlert(this,"Alert","select the first task")
                loadrequests(child_age)
            })

            loadrequests(child_age)
        }
        else if (Task_Two == "2")
        {
            Constants.setAlert(this,"Alert","select the second task")
            Swipe.setOnRefreshListener(OnRefreshListener {
                Constants.setAlert(this,"Alert","select the second task")
                loadrequests(child_age)
            })

            loadrequests(child_age)
        }
        else if(Mode == "free")
        {
            Constants.setAlert(this,"Alert","select the task")
            Swipe.setOnRefreshListener(OnRefreshListener {
                Constants.setAlert(this,"Alert","select the task")
                loadrequests(child_age)
            })

            loadrequests(child_age)
        }

    }
    private fun loadrequests(child_age: Int) {

        val query: Query = TopicsRef.orderByChild("timestamp")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (topics as ArrayList<Tasks>).clear()
                for (dataSnapshot1 in snapshot.children) {
                    val topic: Tasks? = dataSnapshot1.getValue(Tasks::class.java)
                    if (topic != null) {

                            if(topic.getPublisher() == "admin" )
                            {
                                var task_id : String
                                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                task_id = sharedPreferences.getString(Constants.Task_One, "")!!
                                if (Task_Two=="2")
                                {
                                    if (task_id != topic.getTaskID())
                                    {
                                        if (snapshot.child(topic.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                            (topics as ArrayList<Tasks>).add(topic)
                                        }
                                    }
                                }
                                else{
                                    if (snapshot.child(topic.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                        (topics as ArrayList<Tasks>).add(topic)
                                    }
                                }
                            }
                        else
                        {
                            if (AgeCheck.getCheck(topic.getAgeLimit()!!, child_age))
                            {
                                var task_id : String
                                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                task_id = sharedPreferences.getString(Constants.Task_One, "")!!
                                if (Task_Two=="2")
                                {
                                    if (task_id != topic.getTaskID())
                                    {
                                        if (snapshot.child(topic.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                            (topics as ArrayList<Tasks>).add(topic)
                                        }
                                    }
                                }
                                else{
                                    if (snapshot.child(topic.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                        (topics as ArrayList<Tasks>).add(topic)
                                    }
                                }
                            }
                        }
                    }
                }
                topicsLIstAdapter = TasksLIstAdapter(topics!!, Task_Two,Mode)
                topicsLIstAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = topicsLIstAdapter

                Swipe.isRefreshing = false
                OurProgressBar.visibility = View.GONE
                if (topicsLIstAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE


            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@OurTasks,"Update","Error : "+error)
            }
        })
    }

    private fun ShowDialog() {

        childRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("Childs"))
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(this@OurTasks)
                            .setView(dialogView)
                            .setCancelable(true)
                        .setOnCancelListener {
                            startActivity(Intent(this@OurTasks,MainActivity::class.java))
                            finish()
                        }
                            .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please select child"
                    btn.setOnClickListener {
                        val intent = Intent(this@OurTasks,SelectChild::class.java)
                        intent.putExtra("class","OurTasks")
                        intent.putExtra("mode",Mode)
                        startActivity(intent)
                        finish()
                    }
                    customDialog.show()
                }
                else
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(this@OurTasks)
                            .setView(dialogView)
                            .setCancelable(true)
                            .setOnCancelListener {
                            startActivity(Intent(this@OurTasks,MainActivity::class.java))
                            finish()
                        }
                            .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please, Add your child first"
                    btn.setOnClickListener {
                        val intent = Intent(this@OurTasks,AddChild::class.java)
                        startActivity(intent)
                        finish()

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
            } else {
                if (Task_Two != "2" && ClassName != "ChooseImage" && TotalChild > 1) {
                    if (ClassName != "SelectChild")
                    {
                        if(!Shown) {
                            Shown = true
                            ShowDialog2()
                        }
                    }
                }
                else if (Task_Two != "2" && Mode != "free" && ClassName != "ChooseImage" && TotalChild == 0) {
                    val intent = Intent(this, ChooseImage::class.java)
                    intent.putExtra("class", "OurTasks")
                    startActivity(intent)
                    finish()
                }
                else if (Task_Two != "2" && Mode != "free" && ClassName != "ChooseImage" && TotalChild == 1) {
                    val intent = Intent(this, ChooseImage::class.java)
                    intent.putExtra("class", "OurTasks")
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
        if (Mode == "free")
        {
            builder3.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
            }
        }
        else
        {
            builder3.setPositiveButton("Yes") { dialog, which ->
                val intent = Intent(this, ChooseImage::class.java)
                intent.putExtra("class", "OurTasks")
                startActivity(intent)
                finish()
            }
        }
        builder3.setNegativeButton("No") { dialog, which ->
            val ChildIntent = Intent(this,SelectChild::class.java)
            ChildIntent.putExtra("class","OurTasks")
            ChildIntent.putExtra("mode",Mode)
            startActivity(ChildIntent)
            finish()
        }
        builder3.show()
    }

}