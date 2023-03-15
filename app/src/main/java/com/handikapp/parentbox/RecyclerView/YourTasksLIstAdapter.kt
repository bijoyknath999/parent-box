package com.handikapp.parentbox.RecyclerView


import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants
import com.squareup.picasso.Picasso


class YourTasksLIstAdapter(yourtask: List<YourTask>) : RecyclerView.Adapter<YourTasksLIstAdapter.ViewHolder?>() {

    private val yourtask : List<YourTask>
    lateinit var context: Context

    init {
        this.yourtask = yourtask
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.your_tasks_list_layout, parent, false)
        context = parent.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return yourtask.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val yourtask : YourTask = yourtask[position]
        holder.title.text = yourtask.getTitle()
        val cText = yourtask.getContent()

        if (cText!!.length >= 144)
        {
            holder.content.text = cText.substring(0, 144) + "......"
        }
        else if (cText.isEmpty())
        {
            holder.content.text = ""
        }
        else
        {
            holder.content.text = cText+"......"
        }

        if (!yourtask.getImage().isEmpty()) {
            Picasso.get()
                    .load(yourtask.getImage())
                    .into(holder.imageView)
        } else {
            Picasso.get()
                    .load(R.drawable.app_logo_1024)
                    .into(holder.imageView)
        }

        holder.DeltBtn.setOnClickListener {
            showdialog(yourtask.getTaskID()!!,yourtask.getImage())
        }


        val face = Typeface.createFromAsset(context.assets, "fonts/FjallaOne-Regular.ttf")
        holder.title.typeface = face
        val face2 = Typeface.createFromAsset(context.assets, "fonts/Abel-Regular.ttf")
        holder.content.typeface = face2
    }

    private fun showdialog(taskID: String, image: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Deletion Warning")
        builder.setMessage("Do you want to delete this task?")
        builder.setIcon(R.drawable.ic_report)
        builder.setCancelable(true)
        builder.setOnCancelListener{
            it.dismiss()
        }

        //performing positive action
        builder.setPositiveButton("Yes"){ dialogInterface, which ->
            if (image.isEmpty())
            {
                DeleteChildsWithNOImage(taskID)
            }
            else
            {
                DeleteChilds(taskID,image)
            }
        }

        //performing negative action
        builder.setNegativeButton("No"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun DeleteChildsWithNOImage(taskID: String) {

        lateinit var database : FirebaseDatabase
        lateinit var ChildsRef: DatabaseReference
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Tasks")
        ChildsRef.child(taskID).removeValue()
                .addOnSuccessListener{
                    Constants.setAlert(context,"Update","Task Deleted!!")
                }
                .addOnFailureListener {
                    Constants.setAlert(context,"Update","Error : " + it)
                }

    }

    private fun DeleteChilds(taskID: String, image: String) {
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
        photoRef.delete()
                .addOnSuccessListener(OnSuccessListener {task->

                    lateinit var database : FirebaseDatabase
                    lateinit var ChildsRef: DatabaseReference
                    database = FirebaseDatabase.getInstance()
                    ChildsRef = database.reference.child("Tasks")
                    ChildsRef.child(taskID).removeValue()
                            .addOnSuccessListener{
                                Constants.setAlert(context,"Update","Task Deleted!!")
                            }
                            .addOnFailureListener {
                                Constants.setAlert(context,"Update","Error : " + it)
                            }
                })
                .addOnFailureListener {
                    e ->
                    Constants.setAlert(context,"Update","Error : " + e.message)
                    lateinit var database : FirebaseDatabase
                    lateinit var ChildsRef: DatabaseReference
                    database = FirebaseDatabase.getInstance()
                    ChildsRef = database.reference.child("Tasks")
                    ChildsRef.child(taskID).removeValue()
                            .addOnSuccessListener{
                                Constants.setAlert(context,"Update","Task Deleted!!")
                            }
                            .addOnFailureListener {
                                Constants.setAlert(context,"Update","Error : " + it)
                            }
                }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView : ImageView
        val title : TextView
        val content : TextView
        val textImage : TextView
        val DeltBtn : FloatingActionButton
        init {
            imageView = view.findViewById(R.id.topics_image)
            title = view.findViewById(R.id.topics_title)
            content = view.findViewById(R.id.topics_content)
            textImage = view.findViewById(R.id.our_tasks_image_text)
            DeltBtn = view.findViewById(R.id.your_tasks_del_fab)
        }
    }
}