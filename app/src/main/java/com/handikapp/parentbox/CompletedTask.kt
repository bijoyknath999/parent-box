package com.handikapp.parentbox

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
import com.handikapp.parentbox.RecyclerView.*
import com.handikapp.parentbox.Utils.Constants

class CompletedTask : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var ChildsRef: DatabaseReference
    private var completedListAdapter: CompletedListAdapter? = null
    private var gifts: List<Gifts>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var OurProgressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_task)


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("CompletedTask")

        mToolbar = findViewById(R.id.completed_task_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Completed Task"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.CompletedTaskList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        Swipe = findViewById(R.id.completed_task_swipe)
        Not_Found = findViewById(R.id.completed_task_not_found)
        OurProgressBar = findViewById(R.id.completed_task_progressbar)

        gifts = ArrayList()

        Swipe.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            loadchilds()
        })

        loadchilds()


    }

    private fun loadchilds() {
        val query: Query = ChildsRef.orderByChild("timestamp")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (gifts as ArrayList<Gifts>).clear()
                for (dataSnapshot1 in snapshot.children) {
                    val gift: Gifts? = dataSnapshot1.getValue(Gifts::class.java)
                    if (gift != null) {
                        (gifts as ArrayList<Gifts>).add(gift)
                    }
                }
                completedListAdapter = CompletedListAdapter(gifts!!)
                completedListAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = completedListAdapter
                Swipe.isRefreshing = false
                OurProgressBar.visibility = View.GONE
                if (completedListAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}