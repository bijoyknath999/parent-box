package com.handikapp.parentbox

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.RecyclerView.Questions
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_quiz_screen.*
import org.w3c.dom.Text

class QuizScreen : AppCompatActivity(), View.OnClickListener {

    private var doublebackexit = false
    private var mCurrentPosition: Int = 1 // Default and the first question position
    private var muserName:String? = null

    private var mCorrectAnswers: Int = 0

    private var mSelectedOptionPosition: Int = 0

    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var TasksRef: DatabaseReference

    private var anS : Int = 0
    private var questionS : String? = null
    private var optionOne : String? = null
    private var optionTwo : String? = null
    private var optionThree : String? = null

    lateinit var OOne: CheckBox
    lateinit var OTwo: CheckBox
    lateinit var OThree: CheckBox
    lateinit var TQuestion : TextView
    lateinit var Submit_BTN : Button
    lateinit var QuestionNo : TextView
    var TaskID : String = ""
    var Task_No : String = ""
    var URL : String = ""
    var Question_No : Int = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_screen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        if (intent.hasExtra("task_id")) {
            TaskID = intent.getStringExtra("task_id")!!
        }
        if (intent.hasExtra("task_no")) {
            Task_No = intent.getStringExtra("task_no")!!
        }
        if (intent.hasExtra("question_no")) {
            Question_No = intent.getIntExtra("question_no",0)
        }



        OOne = findViewById(R.id.option_one)
        OTwo = findViewById(R.id.option_two)
        OThree = findViewById(R.id.option_three)
        TQuestion = findViewById(R.id.question_text)
        Submit_BTN = findViewById(R.id.question_submit)
        QuestionNo = findViewById(R.id.question_no)




        OOne.setOnClickListener(this)
        OTwo.setOnClickListener(this)
        OThree.setOnClickListener(this)

        // TODO(STEP 1: Adding a click event for submit button.)
        // START
        Submit_BTN.setOnClickListener(this)
        // END

        if (Question_No != 0)
        {
            mCurrentPosition = Question_No
        }


        getFromFirebase(mCurrentPosition)

        TasksRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(TaskID)
        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    URL = snapshot.child("image").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@QuizScreen,"Update","Error : "+error)
            }
        })

    }

    private fun getFromFirebase(mCurrentPosition: Int) {
        TasksRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(TaskID).child("Questions").child(mCurrentPosition.toString())
        val query: Query = TasksRef.orderByValue()
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val question: Questions? = snapshot.getValue(Questions::class.java)
                if (question != null) {
                    questionS = question.getQuestion()
                    anS = question.getAns()
                    optionOne = question.getOptionOne()
                    optionTwo = question.getOptionTwo()
                    optionThree = question.getOptionThree()
                    setQuestion(questionS!!, optionOne!!,optionTwo!!,optionThree!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.option_one -> {
                ClickOption(OOne,1)
            }

            R.id.option_two -> {

                ClickOption(OTwo,2)
            }

            R.id.option_three -> {

                ClickOption(OThree,3)
            }

            // TODO(STEP 2: Adding a click event for submit button. And change the questions and check the selected answers.)
            // START
            R.id.question_submit -> {

            }
        }
    }

    /**
     * A function for setting the question to UI components.
     */

    private fun ClickOption(option : CheckBox,selectedOptionNum: Int){

        mSelectedOptionPosition = selectedOptionNum

        defaultOptionsView()
        option.isChecked = true

        question_submit.setOnClickListener {

            // This is to check if the answer is wrong
            if (anS == mSelectedOptionPosition) {
                mCorrectAnswers ++
                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Constants.Task_One_Correct,
                    mCorrectAnswers.toString()
                )
                editor.apply()


                mSelectedOptionPosition = 0


                if (mSelectedOptionPosition == 0) {

                    mCurrentPosition++

                    when {

                        mCurrentPosition <= 5 -> {

                            getFromFirebase(mCurrentPosition)
                        }
                        else -> {

                            if (Task_No == "1")
                            {
                                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString(Constants.Task_One_Correct,
                                    mCorrectAnswers.toString()
                                )
                                editor.apply()
                                val intent = Intent(this, Results::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            else if (Task_No == "2")
                            {
                                val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString(Constants.Task_Two_Correct,
                                    mCorrectAnswers.toString()
                                )
                                editor.apply()
                                val intent = Intent(this, ResultsWithClues::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                val intent = Intent(this, Results::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
            else{
                if (mSelectedOptionPosition != 0) {
                    val intent = Intent(this, SingleTasks::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("task_id", TaskID)
                    intent.putExtra("img_url", URL)
                    intent.putExtra("task_no", Task_No)
                    intent.putExtra("question_no", mCurrentPosition)
                    startActivity(intent)
                    finish()
                }
            }
        }


    }
    private fun setQuestion(questionS: String, optionOne: String, optionTwo: String, optionThree: String) {


        defaultOptionsView()

        // TODO (STEP 6: Check here if the position of question is last then change the text of the button.)
        // START
        if (mCurrentPosition == 5) {
            Submit_BTN.text = "FINISH"
        } else {
            Submit_BTN.text = "SUBMIT"
        }
        // END

        OOne.isChecked = false
        OTwo.isChecked = false
        OThree.isChecked = false

        QprogressBar.progress = mCurrentPosition
        text_progress.text = "$mCurrentPosition" + "/" + QprogressBar.max

        TQuestion.text = questionS
        OOne.text = optionOne
        OTwo.text = optionTwo
        OThree.text = optionThree
        QuestionNo.text = "Question " + mCurrentPosition
    }


    /**
     * A function to set default options view when the new question is loaded or when the answer is reselected.
     */
    private fun defaultOptionsView() {

        val options = ArrayList<CheckBox>()
        options.add(0, OOne)
        options.add(1, OTwo)
        options.add(2, OThree)

        for (option in options) {
            option.typeface = Typeface.DEFAULT
            option.isChecked = false
        }
    }

    override fun onStop() {
        var LeftClass : String
        var LeftTask_NO : String
        var LeftTask_ID : String
        var LeftQuestion_NO : String

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LeftClass, "QuizScreen")
        editor.putString(Constants.LeftTask_NO, Task_No)
        editor.putString(Constants.LeftTask_ID, TaskID)
        editor.putString(Constants.LeftImg_URL, URL)
        editor.putInt(Constants.LeftQuestion_NO, mCurrentPosition)
        editor.apply()
        super.onStop()
    }

    override fun onBackPressed() {
        if(doublebackexit)
        {
            ShowExitDialog()
        }
        doublebackexit = true
        Constants.setAlert(this,"Warning","You can't go back!!!")
        Handler().postDelayed({
            doublebackexit = false
        },
                2000)
    }


    private fun ShowExitDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_title_dialog2, null)
        builder.setCustomTitle(dialogView)
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            val sharedPreferences2 = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
            val editor2 = sharedPreferences2.edit()
            editor2.clear()
            editor2.commit()
            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
}