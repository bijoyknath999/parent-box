package com.handikapp.parentbox

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.handikapp.parentbox.Utils.Constants
import de.cketti.shareintentbuilder.ShareIntentBuilder
import kotlinx.android.synthetic.main.activity_gift_share_completed.*
import java.io.*


class GiftShareCompleted : AppCompatActivity() {
    var PName: String = ""
    var CName: String = ""
    var Task_One_Title : String = ""
    var Task_Two_Title : String = ""
    var Image_Path : String = ""
    var Selfie_Path : String = ""

    lateinit var imagePath : File
    lateinit var imagefile : File
    lateinit var Imguri : Uri
    lateinit var ShareImgUri : Uri
    lateinit var currentphoto : String

    private lateinit var reference : DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    var personName : String = ""
    lateinit var outFile : File
    private val RECORD_REQUEST_CODE = 101
    private var permissionsRequired = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    lateinit var builder : AlertDialog.Builder

    private val SHARED_IMAGE_QUALITY = 100
    private val SHARED_DIRECTORY = "sharing"
    private val SHARED_IMAGE_FILE = "shared_img.png"
    private val FILE_PROVIDER_AUTHORITY = "com.handikapp.parentbox.fileprovider"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_share_completed)


        if (intent.hasExtra("pname")) {
            PName = intent.getStringExtra("pname")!!
        }

        if (intent.hasExtra("cname")) {
            CName = intent.getStringExtra("cname")!!
        }
        if (intent.hasExtra("task_one_title")) {
            Task_One_Title = intent.getStringExtra("task_one_title")!!
        }
        if (intent.hasExtra("task_two_title")) {
            Task_Two_Title = intent.getStringExtra("task_two_title")!!
        }
        if (intent.hasExtra("gift_img")) {
            Image_Path = intent.getStringExtra("gift_img")!!
        }
        if (intent.hasExtra("selfie_img")) {
            Selfie_Path = intent.getStringExtra("selfie_img")!!
        }

        setSupportActionBar(completed_gift_toolbar as Toolbar?)
        supportActionBar?.title = "Share Task"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (completed_gift_toolbar as Toolbar?)!!.setTitleTextColor(Color.WHITE)


        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
            if (!Image_Path.isEmpty()) {
                var returnedUri = Uri.fromFile(File(Image_Path))
                var file = File(Image_Path)
                var exist = file.exists()
                if (!exist)            {
                    val bitmap: Bitmap = (resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
                    val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                    val canvas = Canvas(imageRounded)
                    val paint = Paint()
                    paint.isAntiAlias = true
                    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                            200F, 200F, paint) // Round Image Corner 100 100 100 100
                    completed_gifting_img_gift.setImageBitmap(imageRounded)
                }
                else
                {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, returnedUri)
                    val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                    val canvas = Canvas(imageRounded)
                    val paint = Paint()
                    paint.isAntiAlias = true
                    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                            200F, 200F, paint) // Round Image Corner 100 100 100 100
                    completed_gifting_img_gift.setImageBitmap(imageRounded)
                }

            }
            else
            {
                val bitmap: Bitmap = (resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
                val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val canvas = Canvas(imageRounded)
                val paint = Paint()
                paint.isAntiAlias = true
                paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                        200F, 200F, paint) // Round Image Corner 100 100 100 100
                completed_gifting_img_gift.setImageBitmap(imageRounded)
            }

            if (!Selfie_Path.isEmpty()) {
                var returnedUri2 = Uri.fromFile(File(Selfie_Path))
                var file = File(Selfie_Path)
                var exist = file.exists()
                if (!exist)
                {
                    val bitmap: Bitmap = (resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
                    val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                    val canvas = Canvas(imageRounded)
                    val paint = Paint()
                    paint.isAntiAlias = true
                    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                            200F, 200F, paint) // Round Image Corner 100 100 100 100
                    completed_gifting_img_selfie.setImageBitmap(imageRounded)
                }
                else
                {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, returnedUri2)
                    val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                    val canvas = Canvas(imageRounded)
                    val paint = Paint()
                    paint.isAntiAlias = true
                    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                            200F, 200F, paint) // Round Image Corner 100 100 100 100
                    completed_gifting_img_selfie.setImageBitmap(imageRounded)
                }

            }
            else
            {
                val bitmap: Bitmap = (resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
                val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val canvas = Canvas(imageRounded)
                val paint = Paint()
                paint.isAntiAlias = true
                paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                        200F, 200F, paint) // Round Image Corner 100 100 100 100
                completed_gifting_img_selfie.setImageBitmap(imageRounded)
            }

            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                PName = acct.displayName.toString()
            }
            else
            {
                Constants.setAlert(this, "Update", "Sign in problem!!")
            }


            completed_gifting_text.text = ""+PName+" gifted "+CName+" for learning about '"+Task_One_Title+"' and '"+Task_Two_Title+"' through Parent Box"
            completed_gifting_share_btn.setOnClickListener {

                val bitmap: Bitmap = takeScreenshot()!!
                saveBitmap(bitmap)
                shareIt()
            }
            }
        else{
            setupPermissions()
        }

    }

    fun takeScreenshot(): Bitmap? {
        val rootView = findViewById<RelativeLayout>(R.id.for_ss)
        rootView.isDrawingCacheEnabled = true
        return rootView.drawingCache
    }

    fun saveBitmap(bitmap: Bitmap) {
        val sdCard = Environment.getExternalStorageDirectory()
        val dir = File(sdCard.absolutePath + "/Parent Box")
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        imagePath = File(dir, fileName)
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }
    }

    private fun shareIt() {

        ShareIntentBuilder.from(this)
                .ignoreSpecification()
                .text("" + PName + " gifted " + CName + " for learning about '" + Task_One_Title + "' and '" + Task_Two_Title + "' through Parent Box" +
                        "\nLink : https://play.google.com/store/apps/details?id=" + packageName)
                .subject("Share from Parent Box")
                .stream(Uri.parse(imagePath.toString()), "image/jpeg")
                .share()
    }

    private fun ShowPermissionDialog() {
        builder = AlertDialog.Builder(this)
        builder.setTitle("Need camera and storage permissions for image capture..")
        builder.setIcon(R.drawable.ic_report)
                .setCancelable(false)

        builder.setPositiveButton("Allow") { dialog, which ->

            setupPermissions()
            dialog.dismiss()

        }

        builder.setNegativeButton("Exit") { dialog, which ->
            finish()
            System.exit(0)
        }
        builder.show()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA,
        )

        val permission2 = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

        if (permission2 != PackageManager.PERMISSION_GRANTED && permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    ShowPermissionDialog()

                } else {
                    startActivity(Intent(this, CompletedTask::class.java))
                    finish()
                }
            }
        }
    }

}