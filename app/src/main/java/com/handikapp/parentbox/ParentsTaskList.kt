package com.handikapp.parentbox

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.ParentsTaskAdapter
import com.handikapp.parentbox.RecyclerView.PartentsTask
import com.handikapp.parentbox.Utils.AgeCheck
import com.handikapp.parentbox.Utils.Constants

class ParentsTaskList : AppCompatActivity() {
    private var Shown: Boolean = false
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var PTasksRef: DatabaseReference
    lateinit var childRef: DatabaseReference
    private var ptasksLIstAdapter: ParentsTaskAdapter? = null
    private var ptasks: List<PartentsTask>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var PProgressBar : ProgressBar
    var child_age: Int = 0
    var Task_Two : String = ""
    var TotalChild: Int = 0
    var ClassName : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parents_task_list)

        PTasksRef = FirebaseDatabase.getInstance().reference.child("Tasks")

        if (intent.hasExtra("task_two")) {
            Task_Two = intent.getStringExtra("task_two")!!
        }

        if (intent.hasExtra("class")) {
            ClassName = intent.getStringExtra("class")!!
        }

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid

        childRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!)


        mToolbar = findViewById(R.id.parents_task_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Tasks From Other Parents"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.parentstask_List)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        Swipe = findViewById(R.id.parents_task_swipeRefreshLayout)
        Not_Found = findViewById(R.id.parents_task_not_found)
        PProgressBar = findViewById(R.id.parents_task_progressbar)

        ptasks = ArrayList()

        val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        child_age = sharedPreferences.getInt(Constants.ChildAge,0)
        TotalChild = sharedPreferences.getInt(Constants.TotalChild,0)



        if (child_age != 0 && !ClassName.isEmpty()) {
            Swipe.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                loadOtherParentsTasks(child_age)
            })

            loadOtherParentsTasks(child_age)
        }
        else if (Task_Two == "2")
        {
            Swipe.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                loadOtherParentsTasks(child_age)
            })

            loadOtherParentsTasks(child_age)
        }

    }

    private fun loadOtherParentsTasks(child_age: Int) {
        val query: Query = PTasksRef.orderByChild("timestamp")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (ptasks as ArrayList<PartentsTask>).clear()
                for (dataSnapshot1 in snapshot.children) {
                    val ptask: PartentsTask? = dataSnapshot1.getValue(PartentsTask::class.java)
                    if (ptask != null) {
                        if (AgeCheck.getCheck(ptask.getAgeLimit()!!, child_age))
                        {
                                if (ptask.getPublisher() == "parents")
                                {
                                    var task_id : String
                                    val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                    task_id = sharedPreferences.getString(Constants.Task_One, "")!!
                                    if (Task_Two=="2")
                                    {
                                        if (task_id != ptask.getTaskID())
                                        {
                                            if (snapshot.child(ptask.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                                (ptasks as ArrayList<PartentsTask>).add(ptask)
                                            }
                                        }
                                    }
                                    else{
                                        if (snapshot.child(ptask.getTaskID().toString()).child("Questions").childrenCount.toInt() == 5) {
                                            (ptasks as ArrayList<PartentsTask>).add(ptask)
                                        }
                                    }
                        }
                        }
                    }
                }
                ptasksLIstAdapter = ParentsTaskAdapter(ptasks!!, Task_Two)
                ptasksLIstAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = ptasksLIstAdapter

                Swipe.isRefreshing = false
                PProgressBar.visibility = View.GONE
                if (ptasksLIstAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun ShowDialog() {

        childRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("Childs"))
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(this@ParentsTaskList)
                            .setView(dialogView)
                            .setCancelable(true)
                            .setOnCancelListener {
                                startActivity(Intent(this@ParentsTaskList,MainActivity::class.java))
                                finish()
                            }
                            .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please select child"
                    btn.setOnClickListener {
                        val intent = Intent(this@ParentsTaskList,SelectChild::class.java)
                        intent.putExtra("class","ParentsTaskList")
                        startActivity(intent)
                        finish()

                    }
                    customDialog.show()
                }
                else
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(this@ParentsTaskList)
                            .setView(dialogView)
                            .setCancelable(true)
                            .setOnCancelListener {
                                startActivity(Intent(this@ParentsTaskList,MainActivity::class.java))
                                finish()
                            }
                            .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please, Add your child first"
                    btn.setOnClickListener {
                        val intent = Intent(this@ParentsTaskList,AddChild::class.java)
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
        } else {
            if (Task_Two != "2" && ClassName != "ChooseImage" && TotalChild > 1) {
                if (ClassName != "SelectChild")
                {
                    if(!Shown) {
                        Shown = true
                        ShowDialog2()
                    }
                }
            } else if (Task_Two != "2" && ClassName != "ChooseImage" && TotalChild == 0) {
                val intent = Intent(this, ChooseImage::class.java)
                intent.putExtra("class", "ParentsTaskList")
                startActivity(intent)
                finish()
            }
            else if (Task_Two != "2" && ClassName != "ChooseImage" && TotalChild == 1) {
                val intent = Intent(this, ChooseImage::class.java)
                intent.putExtra("class", "ParentsTaskList")
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
        builder3.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(this, ChooseImage::class.java)
            intent.putExtra("class", "ParentsTaskList")
            startActivity(intent)
            finish()
        }
        builder3.setNegativeButton("No") { dialog, which ->
            val ChildIntent = Intent(this,SelectChild::class.java)
            ChildIntent.putExtra("class","ParentsTaskList")
            startActivity(ChildIntent)
            finish()
        }
        builder3.show()
    }
}