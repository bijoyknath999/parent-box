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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.Childs
import com.handikapp.parentbox.RecyclerView.ChildsListAdapter
import com.handikapp.parentbox.Utils.Constants
import java.security.AccessController.getContext

class ChildsList : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var ChildsRef: DatabaseReference
    lateinit var UsersRef: DatabaseReference
    private var childsLIstAdapter: ChildsListAdapter? = null
    private var childs: List<Childs>? = null
    private var recyclerView : RecyclerView? = null
    private lateinit var mToolbar : Toolbar
    private lateinit var Swipe : SwipeRefreshLayout
    private lateinit var Not_Found : TextView
    private lateinit var SelectedChild : TextView
    private lateinit var OurProgressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_childs_list)


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        UsersRef = database.reference.child("Users").child(currentUser!!)
        ChildsRef = database.reference.child("Users").child(currentUser).child("Childs")

        mToolbar = findViewById(R.id.Childs_List_toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Child's List"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mToolbar.setTitleTextColor(Color.WHITE)
        recyclerView = findViewById(R.id.ChildsList)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = false
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.setHasFixedSize(true)




        Swipe = findViewById(R.id.child_list_swipe)
        Not_Found = findViewById(R.id.child_list_not_found)
        OurProgressBar = findViewById(R.id.child_list_progressbar)
        SelectedChild = findViewById(R.id.child_list_selected_child)

        childs = ArrayList()

        Swipe.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            loadchilds()
        })

        loadchilds()

        loadSelectedChild()

    }

    private fun loadSelectedChild() {
        UsersRef.child("selectedChild").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var selectChildTitle: String = snapshot.child("name").value.toString()
                    SelectedChild.text = selectChildTitle
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadchilds() {
        val query: Query = ChildsRef.orderByChild("timestamp")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                (childs as ArrayList<Childs>).clear()
                for (dataSnapshot1 in snapshot.children) {
                    val child: Childs? = dataSnapshot1.getValue(Childs::class.java)
                    if (child != null) {
                        (childs as ArrayList<Childs>).add(child)
                    }
                }
                childsLIstAdapter = ChildsListAdapter(childs!!)
                childsLIstAdapter!!.notifyDataSetChanged()
                recyclerView!!.adapter = childsLIstAdapter
                Swipe.isRefreshing = false
                OurProgressBar.visibility = View.GONE
                if (childsLIstAdapter!!.itemCount == 0)
                    Not_Found.visibility = View.VISIBLE
                else
                    Not_Found.visibility = View.GONE

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}