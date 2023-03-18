package com.handikapp.parentbox

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.handikapp.parentbox.FirebaseDatabase.ChildsModel
import com.handikapp.parentbox.RecyclerView.Gifts
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.DOBCoverter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.activity_results.results_next_task_btn
import kotlinx.android.synthetic.main.activity_results.results_tap
import kotlinx.android.synthetic.main.activity_results_with_clues.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.io.File

class ResultsWithClues : AppCompatActivity() {
    private var doublebackexit = false
    private var AlreadyShown: Boolean = false
    lateinit var Celebrate : KonfettiView
    var isRunning = false
    var timeMil : Long = 0
    var countDownTimer : CountDownTimer?= null
    lateinit var TapBTN : TextView
    lateinit var GiftImg : ImageView
    lateinit var FinishBTN : Button
    lateinit var CluesText : TextView
    lateinit var ScrollLayout : ScrollView
    lateinit var Clues : TextView
    lateinit var Name : TextView
    lateinit var child_name : String
    lateinit var ImagePath : String
    lateinit var clues : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_with_clues)

        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_heart)
        val drawableShape = Shape.DrawableShape(drawable!!, true)

        Celebrate = findViewById(R.id.results_clues_konfettiView)
        TapBTN = findViewById(R.id.results_clues_tap)
        GiftImg = findViewById(R.id.results_clues_gift_img)
        FinishBTN = findViewById(R.id.results_clues_finish_btn)
        CluesText = findViewById(R.id.results_clues_text)
        ScrollLayout = findViewById(R.id.results_clues_scroll)
        Clues = findViewById(R.id.results_clues)
        Name = findViewById(R.id.results_clues_name)





        val sharedPreferences = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        child_name = sharedPreferences.getString(Constants.ChildName, "")!!
        val sharedPreferences2 = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        ImagePath = sharedPreferences2.getString(Constants.ImagePath,"")!!
        clues = sharedPreferences2.getString(Constants.Clues_Text,"")!!

        Name.text = child_name
        Clues.text = clues
        var returnedUri = Uri.parse(ImagePath)
        val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, returnedUri)
        val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(imageRounded)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                200F, 200F, paint) // Round Image Corner 100 100 100 100
        GiftImg.setImageBitmap(imageRounded)

        TapBTN.setOnClickListener {
            Celebrate.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .addShapes(Shape.Square, Shape.Circle,drawableShape)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(5000L)
                    .addSizes(Size(12), Size(16, 6f))
                    .setPosition(-50f, Celebrate.width + 50f, -50f, -50f)
                    .streamFor(300, 5000L)

            TapBTN.visibility = View.GONE
            GiftImg.visibility = View.VISIBLE
            FinishBTN.visibility = View.VISIBLE
            CluesText.visibility = View.VISIBLE
            ScrollLayout.visibility = View.VISIBLE
        }

        FinishBTN.setOnClickListener {
            if (AlreadyShown)
            {
                val intent = Intent(this,GiftingShare::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            else
            {
                ShowDialog()
            }
        }

    }

    private fun ShowDialog() {

        val dialogVi = layoutInflater.inflate(R.layout.custom_clues_confirm_dialog, null)
        val customDialog = AlertDialog.Builder(this)
                .setView(dialogVi)
                .setCancelable(true)
                .setOnCancelListener {
                    AlreadyShown = true
                    it.dismiss()
                }
                .show()
        val YesBTN = dialogVi.findViewById<Button>(R.id.clues_confirm_yes_btn)
        val SeeBTN = dialogVi.findViewById<Button>(R.id.clues_confirm_see_btn)

        YesBTN.setOnClickListener {
            AlreadyShown = true
            val intent = Intent(this,GiftingShare::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        SeeBTN.setOnClickListener {
            AlreadyShown = true
            customDialog.dismiss()
        }
        
        customDialog.show()
    }


    override fun onStop() {
        var LeftClass : String
        var LeftTask_NO : String
        var LeftTask_ID : String
        var LeftQuestion_NO : String

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LeftClass, "ResultsWithClues")
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