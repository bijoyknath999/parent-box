package com.handikapp.parentbox.Fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.*
import com.handikapp.parentbox.FirebaseDatabase.ChildsModel
import com.handikapp.parentbox.Fragments.Quiz.FragmentQuestion1
import com.handikapp.parentbox.R
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.DOBCoverter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.android.synthetic.main.fragment_add_deatils.view.*
import kotlinx.android.synthetic.main.fragment_addchild.view.*
import java.util.*

class FragmentAddChild : Fragment() {

    private var picker: DatePickerDialog? = null
    private var Year = 0
    private  var Month:Int = 0
    private  var Day:Int = 0
    private  var MonthText:Int = 0
    private  var DayText:Int = 0
    private lateinit var DOB : EditText
    private lateinit var ChildName: EditText
    private var year : Int = 0
    private var month : Int = 0
    private lateinit var reference : DatabaseReference
    private lateinit var SelectedChildRef : DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var SubmitBTN : Button
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_addchild, container, false)
        (activity as MainActivity).setActionBarTitle("Add another child")


        ChildName = view.findViewById(R.id.fragment_child_name)
        DOB = view.findViewById(R.id.fragment_dob)
        SubmitBTN = view.findViewById(R.id.fragment_child_submit_btn)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Loading.....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.max = 100
        progressDialog.setCancelable(false)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users").child(currentUser.toString()).child("Childs")
        SelectedChildRef = database.getReference("Users").child(currentUser.toString()).child("selectedChild")


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

        view.add_child_fragment_layout.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val rec = Rect()
            view.add_child_fragment_layout.getWindowVisibleDisplayFrame(rec)
            val sheight = view.add_child_fragment_layout.rootView.height
            val keyb = sheight - rec.bottom

            if (keyb > sheight * 0.15) {
                DOB.visibility = View.GONE
                view.add_child_fragment_layout2.visibility = View.GONE
                SubmitBTN.visibility = View.GONE
            } else {
                DOB.visibility = View.VISIBLE
                SubmitBTN.visibility = View.VISIBLE
                view.add_child_fragment_layout2.visibility = View.VISIBLE

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

        return view
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

        val customDialog = AlertDialog.Builder(requireContext())
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
                        Constants.setAlert(requireContext(),"Update","Error : "+it)
                    }

        }

    }


    private fun updateChildCount()
    {
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    var total = snapshot.childrenCount
                    SelectedChildRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists())
                            {
                                progressDialog.dismiss()
                                SelectedChildRef.child("totalchild").setValue(total).addOnSuccessListener{
                                    Constants.setAlert(requireContext(),"Update","Child added successfully!!")
                                    val transaction = activity!!.supportFragmentManager.beginTransaction()
                                    val frag = FragmentAddChild()
                                    transaction.replace(R.id.main_fragment,frag)
                                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    transaction.commit()
                                }
                            }
                            else
                            {
                                progressDialog.dismiss()
                                Constants.setAlert(requireContext(),"Update","Child added successfully!!")
                                val transaction = activity!!.supportFragmentManager.beginTransaction()
                                val frag = FragmentAddChild()
                                transaction.replace(R.id.main_fragment,frag)
                                transaction.addToBackStack(null)
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                transaction.commit()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            progressDialog.dismiss()
                            Constants.setAlert(requireContext(),"Update","Error : "+error)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}