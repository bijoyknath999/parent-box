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

class TrophyRedeem : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var ChildsRef: DatabaseReference
    private var trophyListAdapter: TrophyListAdapter? = null
    private var childs: List<Childs>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var OurProgressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trophy_redeem)


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("Childs")

        mToolbar = findViewById(R.id.Childs_trophy_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Trophies List"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.TrophyList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        Swipe = findViewById(R.id.child_trophy_swipe)
        Not_Found = findViewById(R.id.child_trophy_not_found)
        OurProgressBar = findViewById(R.id.child_trophy_progressbar)

        childs = ArrayList()

        Swipe.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            loadchilds()
        })

        loadchilds()


    }

    private fun loadchilds() {
        val query: Query = ChildsRef.orderByChild("timestamp")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (childs as ArrayList<Childs>).clear()
                for (dataSnapshot1 in snapshot.children) {
                    val child: Childs? = dataSnapshot1.getValue(Childs::class.java)
                    if (child != null) {
                        (childs as ArrayList<Childs>).add(child)
                    }
                }
                trophyListAdapter = TrophyListAdapter(childs!!)
                trophyListAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = trophyListAdapter
                Swipe.isRefreshing = false
                OurProgressBar.visibility = View.GONE
                if (trophyListAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}