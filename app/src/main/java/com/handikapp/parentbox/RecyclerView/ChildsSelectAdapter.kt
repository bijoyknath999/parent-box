package com.handikapp.parentbox.RecyclerView

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.*
import com.handikapp.parentbox.FirebaseDatabase.SelectedChildModel
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants


class ChildsSelectAdapter(childs: List<Childs>, actname: String, mode: String) : RecyclerView.Adapter<ChildsSelectAdapter.ViewHolder?>()  {
    private val childs : List<Childs>
    lateinit var context : Context
    var actname: String = ""
    var mode: String = ""

    init {
        this.childs = childs
        this.actname = actname
        this.mode = mode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.childs_list_select_layout, parent, false)
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
        val ID = child.getID()

        holder.itemView.setOnClickListener {
            SendSelectedData(ID,CName,child.getAge())
        }
    }

    private fun SendSelectedData(taskID: String, cName: String, age: Int) {
        val mAuth : FirebaseAuth
        val database : FirebaseDatabase
        val ChildsRef : DatabaseReference
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("selectedChild")

        val ChildsRef2 : DatabaseReference
        var totalchild : Int = 0
        ChildsRef2 = database.reference.child("Users").child(currentUser).child("Childs")
        ChildsRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    totalchild = snapshot.childrenCount.toInt()

                    val model = SelectedChildModel(cName, taskID,age,totalchild)
                    ChildsRef.setValue(model).addOnSuccessListener(OnSuccessListener {
                        Constants.setAlert(context,"Update","Selected Child : "+cName)
                        Constants.getSelectedChild(context)
                        if (mode == "free") {
                            val intent = Intent(context, OurTasks::class.java)
                            intent.putExtra("class", "SelectChild")
                            intent.putExtra("mode","free")
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                        else if(mode == "quick")
                        {
                            val intent = Intent(context, RandomsTask::class.java)
                            intent.putExtra("mode","quick")
                            intent.putExtra("class", "SelectChild")
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }

                        else if (actname == "CreateTask")
                        {
                            val Actintent = Intent(context, CreateTask::class.java)
                            Actintent.putExtra("class", "SelectChild")
                            context.startActivity(Actintent)
                            (context as Activity).finish()
                        }
                        else
                        {
                            val intent = Intent(context, ChooseImage::class.java)
                            intent.putExtra("class", "SelectChild")
                            intent.putExtra("class", actname)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textImage : TextView
        val name : TextView
        val dob : TextView
        val age : TextView
        init {
            textImage = view.findViewById(R.id.childs_select_image_text)
            name = view.findViewById(R.id.childs_select_name)
            dob = view.findViewById(R.id.childs_select_dob)
            age = view.findViewById(R.id.childs_select_age)

        }
    }
}