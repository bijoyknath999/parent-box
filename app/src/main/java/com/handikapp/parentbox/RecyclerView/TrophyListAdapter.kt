package com.handikapp.parentbox.RecyclerView

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.MainActivity
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_gifting_share.*
import kotlinx.android.synthetic.main.child_list_trophy.view.*


class TrophyListAdapter(childs: List<Childs>) : RecyclerView.Adapter<TrophyListAdapter.ViewHolder?>()  {
    private val childs : List<Childs>
    lateinit var context : Context
    lateinit var alertDialog : AlertDialog

    init {
        this.childs = childs
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.child_list_trophy, parent, false)
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
        holder.trophy.typeface = face2


        val child : Childs = childs[position]
        val CName: String = child.getName()
        val firstLetter : String = CName.substring(0, 1).toUpperCase()
        holder.textImage.text = firstLetter
        holder.name.text = CName
        holder.trophy.text = "Got "+child.getTrophies()+" Trophies"
        val ID = child.getID()
        holder.itemView.setOnClickListener {
            showdialog(ID)
        }
    }

    private fun showdialog(ID: String) {

        val builder = AlertDialog.Builder(context)
        val view: View = LayoutInflater.from(context).inflate(R.layout.redeem_dialog, null)
        builder.setCancelable(true)
        builder.setOnCancelListener{
            it.dismiss()
        }
        val trophy: EditText = view.findViewById<View>(R.id.trophy_redeem_dialog_edittext) as EditText
        trophy.inputType = InputType.TYPE_CLASS_NUMBER
        val BTN = view.findViewById<View>(R.id.trophy_redeem_dialog_btn) as Button
        val image = view.findViewById<View>(R.id.trophy_redeem_dialog_image) as ImageView
        val bitmap: Bitmap = (context.resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
        val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(imageRounded)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                200F, 200F, paint) // Round Image Corner 100 100 100 100
        image.setImageBitmap(imageRounded)
        trophy.setOnFocusChangeListener { view, b ->
            if (b)
            {
                trophy.hint = ""
            }
            else
            {
                trophy.hint = "Enter number of trophies"
            }
        }
        BTN.setOnClickListener {
            alertDialog.dismiss()
            RedeemData(ID,trophy.text)
        }
        builder.setView(view)
        alertDialog = builder.create()
        alertDialog.show()

    }

    private fun RedeemData(ID: String, text: Editable) {
        lateinit var mAuth: FirebaseAuth
        lateinit var database : FirebaseDatabase
        lateinit var ChildsRef: DatabaseReference
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid

        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("Childs").child(ID)
        ChildsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild("trophies"))
                {
                    var trophy = snapshot.child("trophies").value.toString()
                    var Tint : Int = Integer.valueOf(trophy)
                    var red : Int = Integer.valueOf(text.toString())
                    if (Tint>=red)
                    {
                        var TotalTrophy : Int  = Tint-red
                        ChildsRef.child("trophies").setValue(TotalTrophy).addOnSuccessListener{
                            Constants.setAlert(context,"Update","Trophy redeemed for gift successfully...")
                            alertDialog.dismiss()
                        }
                    }
                    else if(Tint<red)
                    {

                        Constants.setAlert(context,"Warning","Please enter correct trophy!!")
                    }
                }
                else
                {
                    Constants.setAlert(context,"Update","Trophy is zero!!")
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
        val trophy : TextView
        val RedeemBTN : FloatingActionButton
        init {
            textImage = view.findViewById(R.id.childs_trophy_profile_text)
            name = view.findViewById(R.id.childs_trophy_name)
            trophy = view.findViewById(R.id.childs_trophy_text)
            RedeemBTN = view.findViewById(R.id.childs_trophy_fab)

        }
    }
}