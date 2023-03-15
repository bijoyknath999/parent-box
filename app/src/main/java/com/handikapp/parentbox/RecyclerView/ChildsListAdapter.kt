package com.handikapp.parentbox.RecyclerView

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.ChildsList
import com.handikapp.parentbox.FirebaseDatabase.SelectedChildModel
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants

class ChildsListAdapter(childs: List<Childs>) : RecyclerView.Adapter<ChildsListAdapter.ViewHolder?>()  {
    private val childs : List<Childs>
    lateinit var context : Context
    lateinit var mAuth : FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var ChildsRef : DatabaseReference
    lateinit var ChildsRef2 : DatabaseReference
    var totalchild : Int = 0

    init {
        this.childs = childs
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.childs_list_layout, parent, false)
        context = parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return childs.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val face = Typeface.createFromAsset(context.assets, "fonts/FjallaOne-Regular.ttf")
        holder.name.typeface = face
        val face2 = Typeface.createFromAsset(context.assets, "fonts/Abel-Regular.ttf")
        holder.dob.typeface = face2
        holder.age.typeface = face2


        val child : Childs = childs[position]
        val CName: String = child.getName()
        val firstLetter : String = CName.substring(0, 1).toUpperCase()
        holder.textImage.text = firstLetter
        holder.name.text = CName
        val year : Int = child.getDobYear()
        val month : Int = child.getDobMonth()
        holder.dob.text = "DOB : " + month + "/" + year
        holder.age.text = child.getAge().toString() + " years old"
        val taskID = child.getID()
        holder.delBTN.setOnClickListener {
            showdialog(taskID)
        }

        holder.itemView.setOnClickListener {
            SendSelectedData(taskID,CName,child.getAge())
        }
    }

    private fun SendSelectedData(taskID: String, cName: String, age: Int) {


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef2 = database.reference.child("Users").child(currentUser!!).child("Childs")
        ChildsRef2.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    totalchild = snapshot.childrenCount.toInt()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        ChildsRef = database.reference.child("Users").child(currentUser).child("selectedChild")
        val model = SelectedChildModel(cName, taskID,age,totalchild)
        ChildsRef.setValue(model).addOnSuccessListener(OnSuccessListener {
            Constants.setAlert(context,"Update","Selected Child : "+cName)
            Constants.getSelectedChild(context)
        })
    }

    private fun showdialog(taskID: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Deletion Warning")
        builder.setMessage("Do you want to remove this child from your list?")
        builder.setIcon(R.drawable.ic_report)
        builder.setCancelable(true)
        builder.setOnCancelListener{
            it.dismiss()
        }


        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            DeleteChilds(taskID)
        }

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun DeleteChilds(id: String) {
        val sharedPreferences = context.getSharedPreferences("sharedPreferences_child", AppCompatActivity.MODE_PRIVATE)
        var childID = sharedPreferences.getString(Constants.ChildID,"").toString()
        if (id == childID)
        {
            val sharedPreferences = context.getSharedPreferences("sharedPreferences_child", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            lateinit var mAuth: FirebaseAuth
            lateinit var database : FirebaseDatabase
            lateinit var ChildsRef: DatabaseReference
            //Firebase Auth instance
            mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser?.uid
            database = FirebaseDatabase.getInstance()
            ChildsRef = database.reference.child("Users").child(currentUser!!).child("Childs")
            ChildsRef.child(id).removeValue()
                    .addOnSuccessListener{

                    }
                    .addOnFailureListener {
                        Constants.setAlert(context,"Update","Error : "+ it)
                    }
            DeleteSelectedChild()
        }
        else
        {
            lateinit var mAuth: FirebaseAuth
            lateinit var database : FirebaseDatabase
            lateinit var ChildsRef: DatabaseReference
            //Firebase Auth instance
            mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser?.uid
            database = FirebaseDatabase.getInstance()
            ChildsRef = database.reference.child("Users").child(currentUser!!).child("Childs")
            ChildsRef.child(id).removeValue()
                    .addOnSuccessListener{
                        context.startActivity(Intent(context,ChildsList::class.java))
                        (context as Activity).finish()
                        Constants.setAlert(context,"Update","Child Deleted!!")
                    }
                    .addOnFailureListener {
                        Constants.setAlert(context,"Update","Error : "+ it)
                    }
        }
    }

    private fun DeleteSelectedChild() {
        lateinit var mAuth: FirebaseAuth
        lateinit var database : FirebaseDatabase
        lateinit var ChildsRef: DatabaseReference
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!)
        ChildsRef.child("selectedChild").removeValue()
                .addOnSuccessListener{
                    context.startActivity(Intent(context,ChildsList::class.java))
                    (context as Activity).finish()
                    Constants.setAlert(context,"Update","Child Deleted!!")
                }
                .addOnFailureListener {
                    Constants.setAlert(context,"Update","Error : "+ it)
                }    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textImage : TextView
        val name : TextView
        val dob : TextView
        val age : TextView
        val delBTN : FloatingActionButton
        init {
            textImage = view.findViewById(R.id.childs_profile_text)
            name = view.findViewById(R.id.childs_name)
            dob = view.findViewById(R.id.childs_dob)
            age = view.findViewById(R.id.childs_age)
            delBTN = view.findViewById(R.id.childs_del_fab)

        }
    }
}