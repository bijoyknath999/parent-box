package com.handikapp.parentbox

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.Tasks
import com.handikapp.parentbox.RecyclerView.TasksLIstAdapter
import com.handikapp.parentbox.RecyclerView.YourTask
import com.handikapp.parentbox.RecyclerView.YourTasksLIstAdapter
import com.handikapp.parentbox.Utils.AgeCheck
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_our_tasks.*


class YourTasks: AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var yourTaskRef: DatabaseReference
    private var yourTaskListAdapter:YourTasksLIstAdapter? = null
    private var yourTask: List<YourTask>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var OurProgressBar : ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_tasks)


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        yourTaskRef = FirebaseDatabase.getInstance().reference.child("Tasks")

        mToolbar = findViewById(R.id.your_tasks_page_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Your Tasks"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.your_tasks_List)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        Swipe = findViewById(R.id.your_tasks_swipeRefreshLayout)
        Not_Found = findViewById(R.id.your_tasks_not_found)
        OurProgressBar = findViewById(R.id.your_tasks_progressbar)


        yourTask = ArrayList()

        Swipe.setOnRefreshListener(OnRefreshListener {
            loadyourtaskrequests()
        })

        loadyourtaskrequests()

    }
    private fun loadyourtaskrequests() {

        val currentUser = mAuth.currentUser?.uid

        val query: Query = yourTaskRef.orderByChild("timestamp")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (yourTask as ArrayList<YourTask>).clear()
                for (dataSnapshot1 in snapshot.children) {
                    val task: YourTask? = dataSnapshot1.getValue(YourTask::class.java)
                    if (task != null) {
                            if(task.getUID() == currentUser)
                            (yourTask as ArrayList<YourTask>).add(task)
                    }
                }
                yourTaskListAdapter = YourTasksLIstAdapter(yourTask!!)
                yourTaskListAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = yourTaskListAdapter

                Swipe.isRefreshing = false
                OurProgressBar.visibility = View.GONE
                if (yourTaskListAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}