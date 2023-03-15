package com.handikapp.parentbox.Fragments.Quiz

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.handikapp.parentbox.*
import com.handikapp.parentbox.Notifications.NotificationData
import com.handikapp.parentbox.Notifications.PushNotification
import com.handikapp.parentbox.Notifications.RetrofitInstance
import com.handikapp.parentbox.R
import com.handikapp.parentbox.RecyclerView.Questions
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.fragment_add_deatils.view.*
import kotlinx.android.synthetic.main.fragment_question1.*
import kotlinx.android.synthetic.main.fragment_question1.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentQuestion1.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentQuestion1 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var questionsreference : DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    private lateinit var QuestionText : EditText
    private lateinit var OptionOneText : EditText
    private lateinit var OptionTwoText : EditText
    private lateinit var OptionThreeText : EditText
    private lateinit var OptionCorrectAnsText : EditText
    private lateinit var NextQuestionBTN : Button
    private lateinit var Question_NO : TextView
    var id : String = ""
    var no : String = ""
    var Age : String = ""
    var TaskTitle : String = ""
    var personName : String = ""

    private lateinit var progressDialog: ProgressDialog









    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_question1, container, false)
        if (!arguments?.getString("id")!!.isEmpty()) {
            id = arguments?.getString("id")!!
        }
        if (!arguments?.getString("no")!!.isEmpty()) {
            no = arguments?.getString("no")!!
        }

        (activity as CreateTask).setActionBarTitle("Question $no")
        QuestionText = view.findViewById(R.id.create_question1_question)
        OptionOneText = view.findViewById(R.id.create_question1_option_one)
        OptionTwoText = view.findViewById(R.id.create_question1_option_two)
        OptionThreeText = view.findViewById(R.id.create_question1_option_three)
        OptionCorrectAnsText = view.findViewById(R.id.create_question1_option_correct_ans)
        NextQuestionBTN = view.findViewById(R.id.create_question1_submit)
        Question_NO = view.findViewById(R.id.create_question1_question_no)


        activity?.onBackPressedDispatcher?.addCallback(activity as CreateTask,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                startActivity(Intent(activity,MainActivity::class.java))
                activity?.finish()
            }

        })

        Question_NO.text = "Question $no"
        QuestionText.setOnFocusChangeListener { view, b ->
            if (b)
            {
                QuestionText.hint = ""
            }
            else
            {
                QuestionText.hint = "Enter the question here!!"
            }
        }

        OptionOneText.setOnFocusChangeListener { view, b ->
            if (b)
            {
                OptionOneText.hint = ""
            }
            else
            {
                OptionOneText.hint = "Enter option one"
            }
        }

        OptionTwoText.setOnFocusChangeListener { view, b ->
            if (b)
            {
                OptionTwoText.hint = ""
            }
            else
            {
                OptionTwoText.hint = "Enter option two"
            }
        }

        OptionThreeText.setOnFocusChangeListener { view, b ->
            if (b)
            {
                OptionThreeText.hint = ""
            }
            else
            {
                OptionThreeText.hint = "Enter option three"
            }
        }

        OptionCorrectAnsText.setOnFocusChangeListener { view, b ->
            if (b)
            {
                OptionCorrectAnsText.hint = ""
            }
            else
            {
                OptionCorrectAnsText.hint = "Example : 1"
            }
        }
        progressDialog = ProgressDialog(activity)


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        questionsreference = database.getReference("Tasks").child(id).child("Questions")

        if (!id.isEmpty()) {
            NextQuestionBTN.setOnClickListener {
                progressDialog.setTitle("Loading.....")
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.max = 100
                progressDialog.setCancelable(false)
                progressDialog.show()
                SendQuestionsData(no)
            }
        }
        else
        {
            Constants.setAlert(requireContext(),"Update","Please restat your app")
        }
        if (no=="5")
        {
            NextQuestionBTN.text = "Finish"
        }


        view.create_question_layout_main.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val rec = Rect()
            view.create_question_layout_main.getWindowVisibleDisplayFrame(rec)
            val sheight = view.create_question_layout_main.rootView.height
            val keyb = sheight - rec.bottom

            if (keyb > sheight * 0.15) {
                NextQuestionBTN.visibility = View.GONE
            } else {
                NextQuestionBTN.visibility = View.VISIBLE
            }
        })

        return view
    }

    private fun SendQuestionsData(no: String)
    {

        val currentUser = mAuth.currentUser?.uid
        if (currentUser!=null)
        {
            var questiontext = QuestionText.text.toString()
            var optionone = OptionOneText.text.toString().trim()
            var optiontwo = OptionTwoText.text.toString().trim()
            var optionthree = OptionThreeText.text.toString().trim()
            val optioncorrectno = OptionCorrectAnsText.text.toString().trim()
            if (TextUtils.isEmpty(questiontext))
            {
                QuestionText.error = "Question is empty!!"
                progressDialog.dismiss()
            }
            else if (TextUtils.isEmpty(optionone))
            {
                OptionOneText.error = "Option one is empty!!"
                progressDialog.dismiss()
            }
            else if (TextUtils.isEmpty(optiontwo))
            {
                OptionTwoText.error = "Option two is empty!!"
                progressDialog.dismiss()
            }
            else if (TextUtils.isEmpty(optionthree))
            {
                OptionThreeText.error = "Option three is empty!!"
                progressDialog.dismiss()
            }
            else if (TextUtils.isEmpty(optioncorrectno))
            {
                OptionCorrectAnsText.error = "Correct no is empty!!"
                progressDialog.dismiss()
            }
            else
            {
                var question = Questions(optioncorrectno.toInt(),optionone,optiontwo,optionthree,questiontext)
                questionsreference.child(no).setValue(question)
                        .addOnSuccessListener {
                            progressDialog.dismiss()

                            if(no!="5")
                            {
                                var n : Int = no.toInt()
                                n++
                                val bundle = Bundle()
                                bundle.putString("id",id)
                                bundle.putString("no",n.toString())
                                val transaction = (activity as CreateTask).supportFragmentManager.beginTransaction()
                                val frag = FragmentQuestion1()
                                frag.arguments = bundle
                                transaction.replace(R.id.create_task_fragment,frag)
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                transaction.commit()
                                Constants.setAlert(requireContext(),"Update","Success!!")
                            }
                            else if (no == "5")
                            {
                                val currentUser = mAuth.currentUser?.uid
                                val acct = GoogleSignIn.getLastSignedInAccount(activity)
                                if (acct != null) {
                                    personName = acct.displayName.toString()
                                }
                                questionsreference = database.getReference("Tasks").child(id)
                                questionsreference.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists())
                                        {
                                            var age = snapshot.child("ageLimit").value.toString()
                                            var Title = snapshot.child("title").value.toString()
                                            var publisher = snapshot.child("publisher").value.toString()
                                            var img_url  = snapshot.child("image").value.toString()
                                            var publisher_uid  = snapshot.child("uid").value.toString()
                                            if (publisher == "admin")
                                            {
                                                PushNotification(
                                                        NotificationData(
                                                                "Parent Box New Task",
                                                                "A new task is created by Parent Box for a " + age + " year old named " + Title,
                                                                id,
                                                                img_url,
                                                                "createtask",
                                                                currentUser!!,
                                                                publisher_uid,
                                                                age.toInt()
                                                        ),
                                                        "/topics/All"
                                                ).also {
                                                    sendNotification(it)
                                                }
                                            }
                                            else{
                                                PushNotification(
                                                        NotificationData(
                                                                "Parent Box New Task",
                                                                "A new task is created by Another Parent for a " + age + " year old named " + Title,
                                                                id,
                                                                img_url,
                                                                "createtask",
                                                                currentUser!!,
                                                                publisher_uid,
                                                                age.toInt()
                                                        ),
                                                        "/topics/All"
                                                ).also {
                                                    sendNotification(it)
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        progressDialog.dismiss()
                                        Constants.setAlert(requireContext(),"Update","Error : "+error)
                                    }
                                })


                                ShowDialog()
                            }
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Constants.setAlert(requireContext(),"Update","Error : "+it)
                        }
            }
        }

    }

    private fun ShowDialog() {
        val dialogView = layoutInflater.inflate(R.layout.task_created_dialog, null)
        val customDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .setOnCancelListener {
                    startActivity(Intent(activity,MainActivity::class.java))
                    activity?.finish()
                }
                .show()
        val btn = dialogView.findViewById<Button>(R.id.task_created_dialog_btn)
        btn.setOnClickListener {
            val sharedPreferences = activity?.getSharedPreferences("sharedPreferences_createtask", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.clear()
            editor?.commit()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        customDialog.show()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}