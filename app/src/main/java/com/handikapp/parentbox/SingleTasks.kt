package com.handikapp.parentbox

import android.R.bool
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.util.Pair.create
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.handikapp.parentbox.Notifications.NotificationData
import com.handikapp.parentbox.Notifications.PushNotification
import com.handikapp.parentbox.Notifications.RetrofitInstance
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.CountTimer
import com.squareup.picasso.Picasso
import developer.shivam.crescento.CrescentoImageView
import kotlinx.android.synthetic.main.activity_single_tasks.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SingleTasks : AppCompatActivity(), ViewTreeObserver.OnScrollChangedListener, View.OnTouchListener {

    private var doublebackexit = false
    private var ShowUp: Boolean = false
    private lateinit var TasksImage: CrescentoImageView
    private lateinit var ScrollSingle : NestedScrollView
    private lateinit var Fab : FloatingActionButton
    private var Click : Boolean = false
    var TaskID : String = ""
    var ImgURL : String = ""
    var Task_NO : String = ""
    var Mode : Boolean = false

    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var TasksRef: DatabaseReference
    lateinit var Title: TextView
    lateinit var Desc: TextView
    var Question_NO: Int = 0
    var countDownTimer : CountDownTimer?= null
    lateinit var Timer : TextView
    lateinit var UserRef: DatabaseReference
    var Tasktitle : String = ""
    var TaskPublisher : String = ""
    var PublisherID : String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_tasks)

        TasksImage = findViewById(R.id.crescentoImageView)
        ScrollSingle = findViewById(R.id.scrollSingle)
        Fab = findViewById(R.id.fab)
        Timer = findViewById(R.id.single_tasks_timer)

        if (intent.hasExtra("task_id")) {
            TaskID = intent.getStringExtra("task_id")!!
        }
        if (intent.hasExtra("img_url")) {
            ImgURL = intent.getStringExtra("img_url")!!
            if (ImgURL.isEmpty())
            {
                Picasso.get().load(R.drawable.app_logo_1024).into(TasksImage)
            }
            else{
                Picasso.get().load(ImgURL).placeholder(R.drawable.app_logo_1024).error(R.drawable.app_logo_1024).into(TasksImage)
            }
        }
        if (intent.hasExtra("task_no")) {
            Task_NO = intent.getStringExtra("task_no")!!
        }

        if (intent.hasExtra("question_no")) {
            Question_NO = intent.getIntExtra("question_no",0)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        Mode = sharedPreferences.getBoolean(Constants.FreeMode,false)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid


        database = FirebaseDatabase.getInstance()
        TasksRef = database.reference.child("Tasks").child(TaskID)
        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Tasktitle = snapshot.child("title").value.toString()
                    var content = snapshot.child("content").value
                    var image = snapshot.child("image").value
                    TaskPublisher = snapshot.child("publisher").value.toString()
                    PublisherID = snapshot.child("uid").value.toString()


                    if (image.toString().isEmpty())
                    {
                        Picasso.get().load(R.drawable.app_logo_1024).into(TasksImage)
                    }
                    else{
                        Picasso.get().load(image.toString()).placeholder(R.drawable.app_logo_1024).error(R.drawable.app_logo_1024).into(TasksImage)
                    }



                    Title.text = Tasktitle.toString()
                    Desc.text = content.toString()


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        if (ImgURL.isEmpty())
        {
            ImgURL = R.drawable.app_logo_1024.toString()
        }


        TasksImage.setOnClickListener(View.OnClickListener {
            val detailAct = Intent(this, ImageViewActivity::class.java)

            val p1: Pair<View, String> = create(TasksImage as View?, "image")
            // Pair<View, String> p2 = Pair.create((View)holder.movieTitle, "title");
            detailAct.putExtra(
                    "img_url",
                    ImgURL
            )
            val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1)
            startActivity(detailAct, options.toBundle())
        })

        Title = findViewById(R.id.title_single_tasks)
        Desc = findViewById(R.id.desc_single_tasks)

        val face = Typeface.createFromAsset(assets, "fonts/FjallaOne-Regular.ttf")
        Title.typeface = face
        val face2 = Typeface.createFromAsset(assets, "fonts/Abel-Regular.ttf")
        Desc.typeface = face2

        ScrollSingle.setOnTouchListener(this)
        ScrollSingle.viewTreeObserver.addOnScrollChangedListener(this)
        if (!Click) {
            Fab.setOnClickListener {
                Constants.setAlert(this,"Update","Read Properly!!!")
            }
        }
    }

    override fun onScrollChanged() {
        val view : View = ScrollSingle.getChildAt(ScrollSingle.childCount - 1)
        val topDetector : Int = ScrollSingle.scrollY
        val bottomDetector = view.bottom - (ScrollSingle.height + ScrollSingle.scrollY)
        if (bottomDetector==0)
        {
            Click = true
            Fab.setImageResource(R.drawable.ic_check_float)
            Fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.green))
        }
        if (topDetector<=0)
        {
        }

        if (Click)
        {
            if (Mode)
            {
                Fab.setOnClickListener{
                    senddataNotify()
                    val intent = Intent(this, QuizScreen::class.java)
                    intent.putExtra("task_id", TaskID)
                    intent.putExtra("question_no",Question_NO)
                    startActivity(intent)
                    finish()
                }
            }
            else
            {
                Fab.setOnClickListener{
                    if (Task_NO == "1") {
                        senddataNotify()
                        val intent = Intent(this, QuizScreen::class.java)
                        intent.putExtra("task_id", TaskID)
                        intent.putExtra("task_no", "1")
                        intent.putExtra("question_no",Question_NO)
                        startActivity(intent)
                        finish()
                    }
                    else if (Task_NO == "2")
                    {
                        senddataNotify()
                        val intent = Intent(this, QuizScreen::class.java)
                        intent.putExtra("task_id", TaskID)
                        intent.putExtra("task_no", "2")
                        intent.putExtra("question_no",Question_NO)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        Click = true
        Fab.setImageResource(R.drawable.ic_check_float)
        Fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.green))
        if (Click)
        {
            if (Mode)
            {
                Fab.setOnClickListener {
                    senddataNotify()
                    val intent = Intent(this, QuizScreen::class.java)
                    intent.putExtra("task_id", TaskID)
                    intent.putExtra("question_no",Question_NO)
                    startActivity(intent)
                    finish()
                }
            }
            else
            {
                Fab.setOnClickListener{
                    if (Task_NO == "1") {
                        senddataNotify()
                        val intent = Intent(this, QuizScreen::class.java)
                        intent.putExtra("task_id", TaskID)
                        intent.putExtra("task_no", "1")
                        intent.putExtra("question_no",Question_NO)
                        startActivity(intent)
                        finish()
                    }
                    else if (Task_NO == "2")
                    {
                        senddataNotify()
                        val intent = Intent(this, QuizScreen::class.java)
                        intent.putExtra("task_id", TaskID)
                        intent.putExtra("task_no", "2")
                        intent.putExtra("question_no",Question_NO)
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }
        return false
    }

    private fun senddataNotify()
    {
        if(TaskPublisher == "parents")
        {
            if (Question_NO == 0) {
                var personName: String = ""
                var Age: Int = 0
                val currentUser = mAuth.currentUser?.uid
                val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
                Age = sharedPreferences.getInt(Constants.ChildAge, 0)
                val acct = GoogleSignIn.getLastSignedInAccount(this)
                if (acct != null) {
                    personName = acct.displayName.toString()
                }

                UserRef = FirebaseDatabase.getInstance().reference.child("Users").child(PublisherID)
                UserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("token")) {
                            var token = snapshot.child("token").value.toString()
                            PushNotification(
                                    NotificationData(
                                            "Thank you!",
                                            "Your task " + Tasktitle + " was chosen by another parent " + personName + " to teach his/her " + Age + " year old.",
                                            TaskID,
                                            ImgURL,
                                            "taskselect",
                                            currentUser!!,
                                            PublisherID,
                                            Age
                                    ),
                                    token
                            ).also {
                                sendNotification(it)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(ContentValues.TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(ContentValues.TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, e.toString())
        }
    }

    private fun startCounting() {

        Fab.visibility = View.GONE
        Timer.visibility = View.VISIBLE

        var totaltime = 60*1000


        countDownTimer = object  : CountDownTimer(totaltime.toLong(), 1000){
            override fun onFinish() {
                Fab.visibility = View.VISIBLE
                Timer.visibility = View.GONE
            }

            override fun onTick(millisUntilFinished: Long) {
                var time =  Math.round(millisUntilFinished * 0.001f)
                val seconds: Int = time % 60
                Timer.text = seconds.toString()+"s"
            }
        }.start()

        countDownTimer!!.start()
    }

    override fun onStart() {

        CountTimer.stopService(this)

        if (TaskID.isEmpty())
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        if (Question_NO > 0)
        {
            if(!ShowUp) {
                ShowUp = true
                Constants.setAlert(this, "Wrong answer", "Read passage again!")
            }
            startCounting()
        }


        super.onStart()
    }

    override fun onStop() {
        CountTimer.stopService(this)

        var LeftClass : String
        var LeftTask_NO : String
        var LeftTask_ID : String
        var LeftQuestion_NO : String

        if (Timer.isVisible)
        {
            countDownTimer?.cancel()
        }

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LeftClass, "SingleTasks")
        editor.putString(Constants.LeftTask_NO, Task_NO)
        editor.putString(Constants.LeftTask_ID, TaskID)
        editor.putInt(Constants.LeftQuestion_NO, Question_NO)
        editor.putString(Constants.LeftImg_URL, ImgURL)
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