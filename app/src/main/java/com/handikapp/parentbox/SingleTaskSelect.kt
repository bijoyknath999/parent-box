package com.handikapp.parentbox

import android.R.bool
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
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
import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.handikapp.parentbox.Utils.Constants
import com.squareup.picasso.Picasso
import developer.shivam.crescento.CrescentoImageView
import kotlinx.android.synthetic.main.activity_single_tasks.*


class SingleTaskSelect : AppCompatActivity(){

    private lateinit var TasksImage: CrescentoImageView
    private lateinit var ScrollSingle : NestedScrollView
    private lateinit var Fab : FloatingActionButton
    var TaskID : String = ""
    var ImgURL : String = ""
    var TaskTwo : String = ""
    var PublisherID : String = ""

    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var TasksRef: DatabaseReference
    lateinit var Title: TextView
    lateinit var Desc: TextView
    lateinit var NextIntent: Intent
    var Mode : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_task_select)

        TasksImage = findViewById(R.id.single_select_crescentoImageView)
        ScrollSingle = findViewById(R.id.single_select_scrollSingle)
        Fab = findViewById(R.id.single_select_fab)


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
        if (intent.hasExtra("task_two")) {
            TaskTwo = intent.getStringExtra("task_two")!!
        }
        if (intent.hasExtra("mode")) {
            Mode = intent.getStringExtra("mode")!!
        }

        if (intent.hasExtra("publisher_id"))
        {
            PublisherID = intent.getStringExtra("publisher_id")!!
        }


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid

        database = FirebaseDatabase.getInstance()
        TasksRef = database.reference.child("Tasks").child(TaskID)
        TasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var title = snapshot.child("title").value
                    var content = snapshot.child("content").value

                    Title.text = title.toString()
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

        Title = findViewById(R.id.title_single_select)
        Desc = findViewById(R.id.desc_single_select)

        val face = Typeface.createFromAsset(assets, "fonts/FjallaOne-Regular.ttf")
        Title.typeface = face
        val face2 = Typeface.createFromAsset(assets, "fonts/Abel-Regular.ttf")
        Desc.typeface = face2

        Fab.setOnClickListener{
            if (Mode == "free")
            {
                NextIntent = Intent(this, LockScreen::class.java)
                val sharedPreferences =
                        getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Constants.FreeTask_ID, TaskID)
                editor.putString(Constants.FreeTask_PublisherID, PublisherID)
                editor.putString(Constants.FreeTask_Title, Title.text.toString())
                editor.putString(Constants.FreeTask_ImageUrl, ImgURL)
                editor.putBoolean(Constants.FreeMode, true)
                editor.apply()
                startActivity(NextIntent)
            }
            else if(Mode == "notify")
            {
                NextIntent = Intent(this, LockScreen::class.java)
                val sharedPreferences =
                        getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Constants.FreeTask_ID, TaskID)
                editor.putString(Constants.FreeTask_PublisherID, PublisherID)
                editor.putString(Constants.FreeTask_Title, Title.text.toString())
                editor.putString(Constants.FreeTask_ImageUrl, ImgURL)
                editor.putBoolean(Constants.FreeMode, true)
                editor.apply()
                startActivity(NextIntent)
            }
            else{
                if (TaskTwo.isEmpty()) {
                    NextIntent = Intent(this, TimerScreen::class.java)
                    val sharedPreferences =
                            getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Constants.Task_One, TaskID)
                    editor.putString(Constants.Task_One_Title, Title.text.toString())
                    editor.apply()
                    startActivity(NextIntent)
                }
                else if (TaskTwo == "2")
                {
                    NextIntent = Intent(this, LockScreen::class.java)
                    val sharedPreferences =
                            getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Constants.Task_two, TaskID)
                    editor.putString(Constants.Task_Two_Title, Title.text.toString())
                    editor.apply()
                    startActivity(NextIntent)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (Mode == "notify")
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else
        {
            super.onBackPressed()
        }
    }
}