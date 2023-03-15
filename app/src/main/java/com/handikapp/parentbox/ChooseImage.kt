package com.handikapp.parentbox

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.ValueCallback
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.handikapp.parentbox.Utils.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class ChooseImage : AppCompatActivity() {

    private var DialogShowed: Boolean = false
    private lateinit var CaptureBTN : Button
    private lateinit var PickBTN : Button
    private lateinit var SubmitBTN : Button
    private lateinit var ButtonLayout : RelativeLayout
    private lateinit var ImageGift : ImageView
    private lateinit var SelectedImage : ImageView
    private lateinit var Full_layout : LinearLayout
    private lateinit var TopText : TextView
    private lateinit var context : Context
    private lateinit var bmp : Bitmap
    private lateinit var NoGiftsText : TextView
    lateinit var currentphoto : String
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mCapturedImageURI: Uri? = null
    private var mCameraPhotoPath: String? = null
    private val INPUT_FILE_REQUEST_CODE = 1
    private val FILECHOOSER_RESULTCODE = 1
    private val TAG = ChooseImage::class.java.simpleName

    lateinit var actname : String
    lateinit var TaskIntent : Intent
    lateinit var imagefile : File
    lateinit var Imguri : Uri

    private val RECORD_REQUEST_CODE = 101
    private var permissionsRequired = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_image)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        if (intent.hasExtra("class")) {
            actname = intent.getStringExtra("class")!!
        }




        CaptureBTN = findViewById<Button>(R.id.capture_btn)
        PickBTN = findViewById<Button>(R.id.pick_btn)
        SubmitBTN = findViewById<Button>(R.id.img_submit_btn)
        ButtonLayout = findViewById<RelativeLayout>(R.id.submit_btn_layout)
        ImageGift = findViewById(R.id.img_gift)
        SelectedImage = findViewById(R.id.selected_img)
        Full_layout = findViewById<LinearLayout>(R.id.full_layout)
        TopText = findViewById<TextView>(R.id.Snap_Top_Text)
        NoGiftsText = findViewById<TextView>(R.id.no_gifts)


        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {

            CaptureBTN.setOnClickListener {
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
                startActivityForResult(intent, 123)
            }

            PickBTN.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 456)
            }

            SubmitBTN.setOnClickListener {
                hideandsubmit()
            }

            NoGiftsText.setOnClickListener {
                ShowDialog()
            }
        }
        else{
            setupPermissions()
        }
    }

    private fun ShowDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        val customDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .show()
        val btnOK = dialogView.findViewById<Button>(R.id.dialog_ok_btn)
        btnOK.setOnClickListener {
            customDialog.dismiss()
        }
    }

    private fun hideandsubmit() {
        saveImage()
    }


    fun saveImage() {
        val draw = SelectedImage.drawable as BitmapDrawable
        val bitmap = draw.bitmap
        var outStream: FileOutputStream? = null
        val sdCard = Environment.getExternalStorageDirectory()
        val dir = File(sdCard.absolutePath + "/Parent Box")
        dir.mkdirs()
        val fileName = String.format("%d.jpg", System.currentTimeMillis())
        val outFile = File(dir, fileName)
        outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
        outStream.flush()
        outStream.close()
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(outFile)
        sendBroadcast(intent)

        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.ImagePath, outFile.toString())
        editor.putBoolean(Constants.Task_Valid, true)
        editor.putString(Constants.Class,actname)
        editor.apply()

        if (actname=="OurTasks")
            TaskIntent = Intent(this, OurTasks::class.java)
        else if(actname == "ParentsTaskList")
            TaskIntent = Intent(this, ParentsTaskList::class.java)
        else if(actname == "RandomsTask") {
            TaskIntent = Intent(this, RandomsTask::class.java)
        }
        else
            TaskIntent = Intent(this, MainActivity::class.java)

        TaskIntent.putExtra("class","ChooseImage")
        startActivity(TaskIntent)
        finish()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == RESULT_OK){
            ButtonLayout.visibility = View.VISIBLE
            SelectedImage.visibility = View.VISIBLE
            SubmitBTN.visibility = View.VISIBLE
            PickBTN.visibility = View.GONE
            CaptureBTN.visibility = View.GONE
            ImageGift.visibility = View.GONE
            NoGiftsText.visibility = View.GONE
            TopText.visibility = View.VISIBLE
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
            SelectedImage.setImageBitmap(imageRounded)

        }else if (requestCode == 456 && resultCode == RESULT_OK){
            ButtonLayout.visibility = View.VISIBLE
            SelectedImage.visibility = View.VISIBLE
            SubmitBTN.visibility = View.VISIBLE
            PickBTN.visibility = View.GONE
            CaptureBTN.visibility = View.GONE
            ImageGift.visibility = View.GONE
            NoGiftsText.visibility = View.GONE
            var filepath : Uri
            filepath = data?.data!!
            var bitm : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            val bitmap : Bitmap = (bitm)
            val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(imageRounded)
            val paint = Paint()
            paint.isAntiAlias = true
            paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            canvas.drawRoundRect(RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                    200F, 200F, paint) // Round Image Corner 100 100 100 100
            SelectedImage.setImageBitmap(imageRounded)
            TopText.visibility = View.VISIBLE
        }
        else
        {
            Constants.setAlert(this,"Update","Please capture or select your gift picture")
        }
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


    private fun ShowPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need camera and storage permissions for image capture..")
        builder.setIcon(R.drawable.ic_report)
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
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun ShowDialogGift() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Have a gift ready?")
        builder.setIcon(R.drawable.ic_gift)
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
            ShowDialog2()
        }
        builder.show()
    }


    private fun ShowDialog2() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog2, null)
        val customDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .show()
        dialogView.setBackgroundColor(Color.parseColor("#00000000"))
        val btnProceed = dialogView.findViewById<Button>(R.id.dialog_proceed_btn)
        btnProceed.setOnClickListener {
            val freeintent = Intent(this,OurTasks::class.java)
            freeintent.putExtra("mode","free")
            freeintent.putExtra("class","ChooseImage")
            startActivity(freeintent)
            finish()
            customDialog.dismiss()
        }
    }

    override fun onStart() {
        if (!DialogShowed) {
            if (!SelectedImage.isVisible) {
                DialogShowed = true
                ShowDialogGift()
            }
        }
        super.onStart()
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
        super.onBackPressed()
    }
}
