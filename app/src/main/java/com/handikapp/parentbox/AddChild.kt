package com.handikapp.parentbox

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.FirebaseDatabase.ChildsModel
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.DOBCoverter
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class AddChild : AppCompatActivity() {

    private var picker: DatePickerDialog? = null
    private var Year = 0
    private  var Month:Int = 0
    private  var Day:Int = 0
    private  var MonthText:Int = 0
    private  var DayText:Int = 0
    private lateinit var DOB : EditText
    private var year : Int = 0
    private var month : Int = 0
    private lateinit var reference : DatabaseReference
    private lateinit var SelectedChildRef : DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var SubmitBTN : Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var LayoutOne : RelativeLayout
    private lateinit var LayoutTwo : LinearLayout







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_child)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        var Username = findViewById<TextView>(R.id.profile_name)
        var image = findViewById<CircleImageView>(R.id.profile_image)
        var ChildName = findViewById<EditText>(R.id.child_name)
        DOB = findViewById<EditText>(R.id.dob)
        SubmitBTN = findViewById(R.id.child_submit_btn)
        LayoutOne = findViewById(R.id.add_child_layout)
        LayoutTwo = findViewById(R.id.add_child_layout2)
        var SkipBTN = findViewById<Button>(R.id.child_skip_btn)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading.....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.max = 100
        progressDialog.setCancelable(false)

        SkipBTN.setOnClickListener {
            Constants.getSelectedChild(this)
            val MainIntent = Intent(this, MainActivity::class.java)
            startActivity(MainIntent)
            finish()
        }
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users").child(currentUser.toString()).child("Childs")
        SelectedChildRef = database.getReference("Users").child(currentUser.toString()).child("selectedChild")


        var calendar:Calendar = Calendar.getInstance()

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personGivenName = acct.givenName
            val personFamilyName = acct.familyName
            val personEmail = acct.email
            val personId = acct.id
            val personPhoto: Uri? = acct.photoUrl
            val FinalURL = personPhoto.toString()
            var imageURL = FinalURL.replace("s96-c", "s384-c", true)
            Username.text = personName

            Picasso.get()
                .load(imageURL)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(image)
        }


        ChildName.setOnFocusChangeListener { view, b ->
            if (b)
            {
                ChildName.hint = ""
            }
            else
            {
                ChildName.hint = "Enter your child’s name"
            }
        }

        LayoutOne.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val rec = Rect()
            LayoutOne.getWindowVisibleDisplayFrame(rec)
            val sheight = LayoutOne.rootView.height
            val keyb = sheight - rec.bottom

            if (keyb > sheight * 0.15) {
                DOB.visibility = View.GONE
                LayoutTwo.visibility = View.GONE
                SubmitBTN.visibility = View.GONE
            } else {
                DOB.visibility = View.VISIBLE
                SubmitBTN.visibility = View.VISIBLE
                LayoutTwo.visibility = View.VISIBLE

            }
        })


        DOB.setOnClickListener {
            ShowDialog()
        }

        SubmitBTN.setOnClickListener {
            var age : Int = 0
            if (month!=0 && year != 0) {
                age = DOBCoverter.getAge(year, month)
            }
            var name : String = ChildName.text.toString().trim()

            if (age == 0)
            {
                DOB.error = "DOB is empty!!"
            }
            else if(TextUtils.isEmpty(name))
            {
                ChildName.error = "Child Name is empty!!"
            }

            else if (year == 0)
            {
                DOB.error = "Year is empty!!"
            }
            else if (month == 0)
            {
                DOB.error = "Month is empty!!"
            }
            else
            {
                progressDialog.show()
                SendData(age, name, year, month)
            }

        }

    }

    private fun ShowDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dob_dialog, null)
        dialogView.setBackgroundColor(Color.parseColor("#00000000"))
        val Picker1 = dialogView.findViewById<NumberPicker>(R.id.numPicker)
        val Picker2 = dialogView.findViewById<NumberPicker>(R.id.numPicker2)

        var Cyear : Int = Calendar.getInstance().get(Calendar.YEAR)
        var Cmonth : Int = Calendar.getInstance().get(Calendar.MONTH)


        Picker1.maxValue = 12
        Picker1.minValue = 1
        Picker1.value = 0
        Picker1.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        Picker1.wrapSelectorWheel = true



        Picker2.maxValue = 2050
        Picker2.minValue = 1950
        Picker2.value = Cyear
        Picker2.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        Picker2.wrapSelectorWheel = true



        Picker1.setOnValueChangedListener { numberPicker, i, i2 ->
            month = i2
        }

        Picker2.setOnValueChangedListener { numberPicker, i, i2 ->
            year = i2
        }
        
        val customDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Submit"){ dialog, which->
                    DOB.setText("$month / $year")
                    dialog.dismiss()
                }
                .show()

        customDialog.setCanceledOnTouchOutside(true)

    }

    private fun SendData(age: Int, name: String, year: Int, month: Int)
    {

        val currentUser = mAuth.currentUser?.uid
        if (currentUser!=null)
        {
            val currentTimestamp = System.currentTimeMillis()
            var id = reference.push().key
                var model = ChildsModel(
                        name,
                        month,
                        year,
                        age,
                        id.toString(),
                        currentTimestamp.toString()
                )
                reference.child(id!!).setValue(model)
                        .addOnSuccessListener {
                            updateChildCount()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Constants.setAlert(this,"Update","Error : "+it)
                        }

        }

    }


    private fun updateChildCount()
    {
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    var total = snapshot.childrenCount
                    SelectedChildRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists())
                            {
                                progressDialog.dismiss()
                                SelectedChildRef.child("totalchild").setValue(total).addOnSuccessListener{
                                    Constants.setAlert(this@AddChild,"Update","Success!!")
                                    Constants.getSelectedChild(this@AddChild)
                                    val GiftsIntent = Intent(this@AddChild, MainActivity::class.java)
                                    startActivity(GiftsIntent)
                                    finish()
                                }
                            }
                            else
                            {
                                progressDialog.dismiss()
                                Constants.getSelectedChild(this@AddChild)
                                Constants.setAlert(this@AddChild,"Update","Success!!")
                                val GiftsIntent = Intent(this@AddChild, MainActivity::class.java)
                                startActivity(GiftsIntent)
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            progressDialog.dismiss()
                            Constants.setAlert(this@AddChild,"Update","Error : "+error)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }




    override fun onBackPressed() {
        Constants.getSelectedChild(this@AddChild)
        super.onBackPressed()
        val MainIntent = Intent(this, MainActivity::class.java)
        startActivity(MainIntent)
        finish()
    }
}