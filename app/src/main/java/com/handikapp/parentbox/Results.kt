package com.handikapp.parentbox

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.handikapp.parentbox.Notifications.NotificationData
import com.handikapp.parentbox.Notifications.PushNotification
import com.handikapp.parentbox.Notifications.RetrofitInstance
import com.handikapp.parentbox.Utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.io.File


class Results : AppCompatActivity() {
    private var doublebackexit = false
    lateinit var Celebrate : KonfettiView
    lateinit var Name : TextView
    lateinit var GiftImage : ImageView
    lateinit var TapBTN : TextView
    lateinit var Result_Trophy : TextView
    lateinit var SecondTaskText : TextView
    lateinit var GiftHereText : TextView
    lateinit var NextTaskBTN : Button
    var child_name: String = ""
    var ImagePath: String = ""
    var ChildId: String = ""
    var Mode : Boolean = false
    lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var ChildsRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_heart)
        val drawableShape = Shape.DrawableShape(drawable!!, true)

        Celebrate = findViewById(R.id.konfettiView)
        TapBTN = findViewById(R.id.results_tap)
        GiftImage = findViewById(R.id.results_gift_img)
        SecondTaskText = findViewById(R.id.results_go_2nd_task_text)
        NextTaskBTN = findViewById(R.id.results_next_task_btn)
        Name = findViewById(R.id.results_name)
        Result_Trophy = findViewById(R.id.results_trophy)
        GiftHereText = findViewById(R.id.results_here_is_gift)

        val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        child_name = sharedPreferences.getString(Constants.ChildName, "")!!
        ChildId = sharedPreferences.getString(Constants.ChildID,"")!!

        val sharedPreferences2 = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        ImagePath = sharedPreferences2.getString(Constants.ImagePath, "")!!
        Mode = sharedPreferences2.getBoolean(Constants.FreeMode,false)

        Name.text = child_name


        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()

        if (Mode)
        {

            GiftImage.setImageResource(R.drawable.trophy)
            GiftHereText.visibility = View.GONE


            TapBTN.setOnClickListener {
                Celebrate.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .addShapes(Shape.Square, Shape.Circle, drawableShape)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(5000L)
                        .addSizes(Size(12), Size(16, 6f))
                        .setPosition(Celebrate.x + Celebrate.width / 2, Celebrate.y + Celebrate.height / 3)
                        .burst(300)

                Result_Trophy.visibility = View.VISIBLE
                TapBTN.visibility = View.GONE
                GiftImage.visibility = View.VISIBLE
                NextTaskBTN.text = "Finish"
                NextTaskBTN.visibility = View.VISIBLE

            }

            NextTaskBTN.setOnClickListener {
                sendDataNotify()
                SendData()
            }
        }
        else
        {
            var returnedUri = Uri.fromFile(File(ImagePath))
            val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, returnedUri)
            val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(imageRounded)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                    200F, 200F, paint) // Round Image Corner 100 100 100 100
            GiftImage.setImageBitmap(imageRounded)

            TapBTN.setOnClickListener {
                Celebrate.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .addShapes(Shape.Square, Shape.Circle, drawableShape)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(5000L)
                        .addSizes(Size(12), Size(16, 6f))
                        .setPosition(Celebrate.x + Celebrate.width / 2, Celebrate.y + Celebrate.height / 3)
                        .burst(300)

                TapBTN.visibility = View.GONE
                GiftImage.visibility = View.VISIBLE
                NextTaskBTN.visibility = View.VISIBLE
                SecondTaskText.visibility = View.VISIBLE
            }

            NextTaskBTN.setOnClickListener {
                val intent = Intent(this,IntervalTimer::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun SendData() {
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid

        database = FirebaseDatabase.getInstance()
        ChildsRef = database.reference.child("Users").child(currentUser!!).child("Childs").child(ChildId)
        ChildsRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChild("trophies"))
                {
                    var trophy = snapshot.child("trophies").value.toString()
                    var Tint : Int = Integer.valueOf(trophy)
                    var TotalTrophy : Int  = Tint+1
                    ChildsRef.child("trophies").setValue(TotalTrophy).addOnSuccessListener{

                        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.commit()
                        val Mintent = Intent(this@Results,MainActivity::class.java)
                        Mintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(Mintent)
                        finish()
                    }
                }
                else
                {
                    ChildsRef.child("trophies").setValue(1).addOnSuccessListener{
                        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.clear()
                        editor.commit()
                        val Mintent = Intent(this@Results,MainActivity::class.java)
                        Mintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(Mintent)
                        finish()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendDataNotify() {
        var personName : String = ""
        var Age : Int = 0
        var Task_Title : String = ""
        var Task_ID : String = ""
        var Task_Img_Url : String = ""
        var Task_Publisher_ID : String = ""
        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        Task_Title = sharedPreferences.getString(Constants.FreeTask_Title,"")!!
        Task_ID = sharedPreferences.getString(Constants.FreeTask_ID,"")!!
        Task_Img_Url = sharedPreferences.getString(Constants.FreeTask_ImageUrl,"")!!
        Task_Publisher_ID = sharedPreferences.getString(Constants.FreeTask_PublisherID,"")!!



        val sharedPreferences2 = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        Age = sharedPreferences2.getInt(Constants.ChildAge,0)
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            personName = acct.displayName.toString()
        }
        val currentUser = mAuth.currentUser?.uid
        PushNotification(
                NotificationData(
                        "Parent Box Task",
                        "Another "+Age+" year old learned about "+Task_Title,
                        Task_ID,
                        Task_Img_Url,
                        "notify",
                        currentUser!!,
                        Task_Publisher_ID,
                        Age
                ),
                "/topics/All"
        ).also {
            sendNotification(it)
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

    override fun onStop() {
        var LeftClass : String
        var LeftTask_NO : String
        var LeftTask_ID : String
        var LeftQuestion_NO : String

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LeftClass, "Results")
        editor.putString(Constants.LeftTask_NO, "")
        editor.putString(Constants.LeftTask_ID, "")
        editor.putInt(Constants.LeftQuestion_NO, 0)
        editor.putString(Constants.LeftImg_URL, "")
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