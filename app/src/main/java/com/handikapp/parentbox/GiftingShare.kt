package com.handikapp.parentbox

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.handikapp.parentbox.Notifications.NotificationData
import com.handikapp.parentbox.Notifications.PushNotification
import com.handikapp.parentbox.Notifications.RetrofitInstance
import com.handikapp.parentbox.RecyclerView.Gifts
import com.handikapp.parentbox.Utils.Constants
import com.hsalf.smileyrating.SmileyRating
import de.cketti.shareintentbuilder.ShareIntentBuilder
import kotlinx.android.synthetic.main.activity_gifting_share.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


class GiftingShare : AppCompatActivity() {
    private var doublebackexit = false
    var PName: String = ""
    var CName: String = ""
    var Task_One_Title : String = ""
    var Task_Two_Title : String = ""
    var Image_Path : String = ""
    var Age : String = ""

    lateinit var imagePath : File
    lateinit var imagefile : File
    lateinit var Imguri : Uri
    lateinit var currentphoto : String
    var Uploaded : Boolean = false
    var Rated : Boolean = false

    var AdminMail : String = ""
    var CheckExist : Boolean = false

    private lateinit var reference : DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    var personName : String = ""
    var outFile : File? = null
    private val RECORD_REQUEST_CODE = 101
    private var permissionsRequired = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gifting_share)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Users").child(currentUser.toString()).child("CompletedTask")


        val sharedPreferences3 =
            getSharedPreferences("sharedPreferences_admin", AppCompatActivity.MODE_PRIVATE)
        AdminMail = sharedPreferences3.getString(Constants.AdminMail, "")!!

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        CName = sharedPreferences.getString(Constants.ChildName,"").toString()
        Task_One_Title = sharedPreferences.getString(Constants.Task_One_Title,"").toString()
        Task_Two_Title = sharedPreferences.getString(Constants.Task_Two_Title,"").toString()
        Image_Path = sharedPreferences.getString(Constants.ImagePath,"").toString()
        Age = sharedPreferences.getString(Constants.ChildAge,"").toString()


        val sharedPreferences2 = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        CName = sharedPreferences2.getString(Constants.ChildName,"").toString()
        Age = sharedPreferences2.getInt(Constants.ChildAge,0).toString()

        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {

            if (!Image_Path.isEmpty()) {
                var returnedUri = Uri.fromFile(File(Image_Path))
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, returnedUri)
                val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                val canvas = Canvas(imageRounded)
                val paint = Paint()
                paint.isAntiAlias = true
                paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                        200F, 200F, paint) // Round Image Corner 100 100 100 100
                gifting_img_gift.setImageBitmap(imageRounded)
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
                gifting_img_gift.setImageBitmap(imageRounded)
            }

            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                PName = acct.displayName.toString()
            }
            else
            {
                Constants.setAlert(this,"Update","Sign in problem!!")
            }


            gifting_capture.setOnClickListener {
                var fileName = String.format("%d.jpg", System.currentTimeMillis())
                var currentSto : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
                imagefile = File.createTempFile(fileName, "jpg", currentSto)
                currentphoto = imagefile.absolutePath
                Imguri = FileProvider.getUriForFile(
                        this,
                        "com.handikapp.parentbox.fileprovider",
                        imagefile
                )
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Imguri)
                startActivityForResult(intent, 234)
            }

            gifting_text.text = ""+PName+" gifted "+CName+" for learning about '"+Task_One_Title+"' and '"+Task_Two_Title+"' through Parent Box"
            gifting_share_btn.setOnClickListener {

                if(!gifting_img_selfie.isVisible)
                {
                    gifting_capture.visibility = View.GONE
                    gifting_capture.visibility = View.GONE
                    gifting_img_selfie.visibility = View.VISIBLE
                    val bitmap: Bitmap = (resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
                    val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
                    val canvas = Canvas(imageRounded)
                    val paint = Paint()
                    paint.isAntiAlias = true
                    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                            200F, 200F, paint) // Round Image Corner 100 100 100 100
                    gifting_img_selfie.setImageBitmap(imageRounded)
                }
                else
                {
                    val bitmap: Bitmap = takeScreenshot()!!
                    saveBitmap(bitmap)
                    saveImage()
                    SendData()
                    shareIt()
                }
            }
            val sharedPreferences = getSharedPreferences("sharedPreferences_rated", MODE_PRIVATE)
            Rated = sharedPreferences.getBoolean(Constants.RATED,false)

            gifting_finish_btn.setOnClickListener {
                if (!CheckExist && !Rated)
                {
                    SendData()
                    showRatingDialog()
                }
                else{
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
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }

            }
            gifting_img_selfie.setOnClickListener {
                var fileName = String.format("%d.jpg", System.currentTimeMillis())
                var currentSto : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
                imagefile = File.createTempFile(fileName, "jpg", currentSto)
                currentphoto = imagefile.absolutePath
                Imguri = FileProvider.getUriForFile(
                        this,
                        "com.handikapp.parentbox.fileprovider",
                        imagefile
                )
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Imguri)
                startActivityForResult(intent, 234)
            }

        }
        else{
            setupPermissions()
        }
    }

    private fun sendDataNotify() {
        if (!Uploaded) {
            val currentUser = mAuth.currentUser?.uid
            PushNotification(
                    NotificationData(
                            "Parent Box Task",
                            "Another " + Age + " year old learned about " + Task_One_Title + " and " + Task_Two_Title,
                            "",
                            "",
                            "giftshare",
                            currentUser!!,
                            "",
                            Age.toInt()
                    ),
                    "/topics/All"
            ).also {
                sendNotification(it)
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

    private fun showRatingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.rating_dialog, null)
        val customDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .setOnCancelListener {
                    CheckExist = true
                    it.dismiss()
                }
                .show()
        dialogView.setBackgroundColor(Color.TRANSPARENT)
        var smileyRating : SmileyRating = dialogView.findViewById(R.id.smile_rating)
        smileyRating.setSmileySelectedListener(SmileyRating.OnSmileySelectedListener { type -> // You can compare it with rating Type
            if (SmileyRating.Type.GREAT == type || SmileyRating.Type.GOOD == type) {
                customDialog.dismiss()
                ShowDialog()
            } else if (SmileyRating.Type.TERRIBLE == type || SmileyRating.Type.BAD == type || SmileyRating.Type.OKAY == type) {
                customDialog.dismiss()
                ShowDialog2()
            }

        })
        val btnSkip = dialogView.findViewById<TextView>(R.id.rating_dialog_rating_skip)
        btnSkip.setOnClickListener {
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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            customDialog.dismiss()
        }
    }

    private fun ShowDialog() {

        val dialogVi = layoutInflater.inflate(R.layout.custom_rate_dialog, null)
        val customDialog = AlertDialog.Builder(this)
                .setView(dialogVi)
                .setCancelable(true)
                .setOnCancelListener {
                    CheckExist = true
                    it.dismiss()
                }
                .show()
        val RateBTN = dialogVi.findViewById<Button>(R.id.rate_dialog_rate_now)
        val Later = dialogVi.findViewById<TextView>(R.id.rate_dialog_later)
        val AlreadyRated = dialogVi.findViewById<TextView>(R.id.rate_dialog_already_rated)

        RateBTN.setOnClickListener {
            CheckExist = true
            customDialog.dismiss()
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        Later.setOnClickListener {
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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        AlreadyRated.setOnClickListener {
            val sharedPreferences = getSharedPreferences("sharedPreferences_rated", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(Constants.RATED,true)
            editor.apply()
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        customDialog.show()
    }


    private fun ShowDialog2() {
        val builder3 = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_title_dialog, null)
        builder3.setCustomTitle(dialogView)
        builder3.setIcon(R.drawable.ic_report)
        builder3.setCancelable(true)
        builder3.setOnCancelListener {
            CheckExist = true
            it.dismiss()
        }

        builder3.setPositiveButton("Yes",{dialog, Which->
            dialog.dismiss()
            if (!AdminMail.isEmpty()) {
                val email = Intent(Intent.ACTION_SENDTO)
                email.data = Uri.parse("mailto:"+AdminMail)
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
            CheckExist = true
        })

        builder3.setNegativeButton("Later",{dialog, Which->
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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
        builder3.show()
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

    fun saveImage() {
        val draw = gifting_img_selfie.drawable as BitmapDrawable
        val bitmap = draw.bitmap
        var outStream: FileOutputStream? = null
        val sdCard = Environment.getExternalStorageDirectory()
        val dir = File(sdCard.absolutePath + "/Parent Box")
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
        outStream.flush()
        outStream.close()
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(outFile)
        sendBroadcast(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 234 && resultCode== RESULT_OK){
            val f: File = File(currentphoto)
            val imguri: Uri = Uri.fromFile(f)

            val bitmap : Bitmap = (handleSamplingAndRotationBitmap(this, imguri)!!)
            val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(imageRounded)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                    200F, 200F, paint) // Round Image Corner 100 100 100 100
            gifting_img_selfie.visibility = View.VISIBLE
            gifting_capture.visibility = View.GONE
            gifting_img_selfie.setImageBitmap(imageRounded)
        }
        else
        {
            gifting_img_selfie.visibility = View.VISIBLE
            gifting_capture.visibility = View.GONE
            val bitmap: Bitmap = (resources.getDrawable(R.drawable.trophy) as BitmapDrawable).bitmap
            val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(imageRounded)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                    200F, 200F, paint) // Round Image Corner 100 100 100 100
            gifting_img_selfie.setImageBitmap(imageRounded)        }
    }

    fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri?): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream: InputStream? = context.contentResolver.openInputStream(selectedImage!!)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream!!.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = context.contentResolver.openInputStream(selectedImage)
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        img = rotateImageIfRequired(img!!, selectedImage)
        return img
    }

    private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            val totalPixels = (width * height).toFloat()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(selectedImage.path!!)
        val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun SendData() {

        if (!Uploaded)
        {
            sendDataNotify()
            val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
            val task_one = sharedPreferences.getString(Constants.Task_One,"").toString()
            val task_two = sharedPreferences.getString(Constants.Task_two,"").toString()
            val task_one_title = sharedPreferences.getString(Constants.Task_One_Title,"").toString()
            val task_two_title = sharedPreferences.getString(Constants.Task_Two_Title,"").toString()

            val sharedPreferences2 = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)

            val child_id = sharedPreferences2.getString(Constants.ChildAge,"").toString()



            val currentUser = mAuth.currentUser?.uid
            if (currentUser!=null)
            {
                val currentTimestamp = System.currentTimeMillis()
                var id = reference.push().key
                var model = Gifts(PName,child_id,CName,task_one,task_two,task_one_title,task_two_title,Image_Path,outFile.toString(),currentTimestamp.toString(),id.toString())
                reference.child(id!!).setValue(model)
                        .addOnSuccessListener {
                            Uploaded = true
                        }
                        .addOnFailureListener {
                            Constants.setAlert(this,"Update","Error : "+it)
                        }
            }
        }
    }

    private fun ShowPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need camera and storage permissions for image capture..")
        builder.setIcon(R.drawable.ic_report)
                .setCancelable(false)
        builder.setPositiveButton("Allow") { dialog, which ->

            setupPermissions()

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
                    startActivity(Intent(this,GiftingShare::class.java))
                    finish()
                }
            }
        }
    }


    override fun onStart() {
        setupPermissions()
        super.onStart()
    }

    override fun onStop() {

        var LeftClass : String
        var LeftTask_NO : String
        var LeftTask_ID : String
        var LeftQuestion_NO : String

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.LeftClass, "GiftingShare")
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