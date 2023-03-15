package com.handikapp.parentbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.Childs
import com.handikapp.parentbox.RecyclerView.ChildsSelectAdapter
import com.handikapp.parentbox.Utils.Constants


class SelectChild : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var ChildsRef: DatabaseReference
    private var childSelectAdapter: ChildsSelectAdapter? = null
    private var childs: List<Childs>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var OurProgressBar : ProgressBar
    var actname : String = ""
    var Mode : String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_child)

        if (intent.hasExtra("class")) {
            actname = intent.getStringExtra("class")!!
        }
        if (intent.hasExtra("mode")) {
            Mode = intent.getStringExtra("mode")!!
        }


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("Childs")

        mToolbar = findViewById(R.id.Childs_Select_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Child's List"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.ChildsList_Select)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)
        Swipe = findViewById(R.id.child_select_swipeRefreshLayout)
        Not_Found = findViewById(R.id.child_select_not_found)
        OurProgressBar = findViewById(R.id.child_select_progressbar)

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
                childSelectAdapter = ChildsSelectAdapter(childs!!,actname, Mode)
                childSelectAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = childSelectAdapter
                Swipe.isRefreshing = false
                OurProgressBar.visibility = View.GONE
                if (childSelectAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE


            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
        super.onBackPressed()
    }

}