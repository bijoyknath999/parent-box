package com.handikapp.parentbox.RecyclerView

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.GiftShareCompleted
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_gifting_share.*
import kotlinx.android.synthetic.main.child_list_trophy.view.*


class CompletedListAdapter(gifts: List<Gifts>) : RecyclerView.Adapter<CompletedListAdapter.ViewHolder?>()  {
    private val gifts : List<Gifts>
    lateinit var context : Context
    lateinit var alertDialog : AlertDialog

    init {
        this.gifts = gifts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.completed_task_list, parent, false)
        context = parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return gifts.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val face = Typeface.createFromAsset(context.assets, "fonts/FjallaOne-Regular.ttf")
        holder.name.typeface = face
        val face2 = Typeface.createFromAsset(context.assets, "fonts/Abel-Regular.ttf")
        holder.TaskOne.typeface = face2
        holder.TaskTwo.typeface = face2



        val gift : Gifts = gifts[position]
        val CName: String = gift.getChildName()
        holder.textImage.text = (position + 1).toString()
        holder.name.text = CName
        holder.TaskOne.text = "Task One : "+gift.getTaskOneTitle()
        holder.TaskTwo.text = "Task Two : "+gift.getTaskTwoTitle()
        holder.itemView.setOnClickListener {
            val GiftIntent = Intent(context,GiftShareCompleted::class.java)
            GiftIntent.putExtra("pname",gift.getPName())
            GiftIntent.putExtra("cname",gift.getChildName())
            GiftIntent.putExtra("task_one_title",gift.getTaskOneTitle())
            GiftIntent.putExtra("task_two_title",gift.getTaskTwoTitle())
            GiftIntent.putExtra("gift_img",gift.getGiftImg())
            GiftIntent.putExtra("selfie_img",gift.getSelfieImg())
            context.startActivity(GiftIntent)
        }

        holder.delBTN.setOnClickListener {
            showdialog(gift.getGiftID())
        }

    }


    private fun showdialog(giftID: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Deletion Warning")
        builder.setMessage("Are you want to delete this completed task?")
        builder.setIcon(R.drawable.ic_report)
        builder.setCancelable(true)
        builder.setOnCancelListener{
            it.dismiss()
        }

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            DeleteChilds(giftID)
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
        lateinit var mAuth: FirebaseAuth
        lateinit var database : FirebaseDatabase
        lateinit var ChildsRef: DatabaseReference
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("CompletedTask")
        ChildsRef.child(id).removeValue()
            .addOnSuccessListener{
                Constants.setAlert(context,"Update","Task Deleted!!")
            }
            .addOnFailureListener {
                Constants.setAlert(context,"Update","Error : "+it)
            }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textImage : TextView
        val name : TextView
        val TaskOne : TextView
        val TaskTwo : TextView
        val delBTN : FloatingActionButton
        init {
            textImage = view.findViewById(R.id.completed_task_profile_text)
            name = view.findViewById(R.id.completed_task_name)
            TaskOne = view.findViewById(R.id.completed_task_one)
            TaskTwo = view.findViewById(R.id.completed_task_two)
            delBTN = view.findViewById(R.id.completed_task_del_fab)

        }
    }
}