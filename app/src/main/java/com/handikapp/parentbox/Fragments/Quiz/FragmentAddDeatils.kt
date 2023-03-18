package com.handikapp.parentbox.Fragments.Quiz

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentResolver
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
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.handikapp.parentbox.*
import com.handikapp.parentbox.R
import com.handikapp.parentbox.RecyclerView.Tasks
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.IOnBackPressed
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_child.*
import kotlinx.android.synthetic.main.activity_create_task.*
import kotlinx.android.synthetic.main.fragment_add_deatils.*
import kotlinx.android.synthetic.main.fragment_add_deatils.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentAddDeatils.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAddDeatils : Fragment(), IOnBackPressed {
    private var doublebackexit = false
    private var DialogShowUp: Boolean = false
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var AddQuestionBTN : Button
    private lateinit var TaskTitle : EditText
    private lateinit var TaskDescription : EditText
    private lateinit var TaskImage : ImageView
    private lateinit var TaskText : TextView
    private lateinit var TaskText2 : TextView

    private lateinit var progressDialog: ProgressDialog


    private lateinit var Taskreference : DatabaseReference
    private lateinit var database : FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    var currentphoto : String = ""
    private lateinit var bmp : Bitmap
    private var mImageUri: Uri? = null
    private var mStorageRef: StorageReference? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mUploadTask: StorageTask<*>? = null
    private lateinit var arrayByte: ByteArray
    var Publisher : String = ""
    var ClassName : String = ""
    var Child_Age : Int = 0
    lateinit var childRef: DatabaseReference
    var TotalChild: Int = 0
    lateinit var imagefile : File
    lateinit var Imguri : Uri

    private val RECORD_REQUEST_CODE = 101
    private var permissionsRequired = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var downloadUri : String = ""


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_deatils, container, false)


        database = FirebaseDatabase.getInstance()
        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        Taskreference = database.getReference("Tasks")
        (activity as CreateTask).setActionBarTitle("Create Task")

        if ((activity as CreateTask).intent.hasExtra("publisher")) {
            Publisher = (activity as CreateTask).intent.getStringExtra("publisher")!!
        }

        if ((activity as CreateTask).intent.hasExtra("class")) {
            ClassName = (activity as CreateTask).intent.getStringExtra("class")!!
        }


        var sharedPreferences = activity?.getSharedPreferences("sharedPreferences_child", AppCompatActivity.MODE_PRIVATE)
        TotalChild = sharedPreferences!!.getInt(Constants.TotalChild,0)
        if (Publisher != "admin")
        {
            Child_Age = sharedPreferences.getInt(Constants.ChildAge, 0)
        }
        else
        {
            Child_Age = 0
        }

        AddQuestionBTN = view.findViewById(R.id.create_task_question_btn)
        TaskTitle = view.findViewById(R.id.create_task_title)
        TaskDescription = view.findViewById(R.id.create_task_description)
        TaskImage = view.findViewById(R.id.create_task_image)
        TaskText = view.findViewById(R.id.create_task_image_textt)
        TaskText2 = view.findViewById(R.id.create_task_image_textt2)


        TaskTitle.setOnFocusChangeListener { view, b ->
            if (b)
            {
                TaskTitle.hint = ""
            }
            else
            {
                TaskTitle.hint = "Give a name for the task you want your kids to learn"
            }
        }

        TaskDescription.setOnFocusChangeListener { view, b ->
            if (b)
            {
                TaskDescription.hint = ""
            }
            else
            {
                TaskDescription.hint = "Enter content that you want to teach your kids: Type or copy paste from a website"
            }
        }

        val currentUser = mAuth.currentUser?.uid
        childRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser!!)




        mStorageRef = FirebaseStorage.getInstance().getReference("TaskPictures")
        TaskImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                ShowImageDialog()
            }
            else
            {
                setupPermissions()
            }
        }
        AddQuestionBTN.setOnClickListener{
            if (downloadUri.isEmpty()) {
                uploadFile()
            }
            else
            {
                Toast.makeText(activity,"download link is here",Toast.LENGTH_LONG).show()
                SendData()
            }
        }

        view.create_task_layout_main.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val rec = Rect()
            view.create_task_layout_main.getWindowVisibleDisplayFrame(rec)
            val sheight = view.create_task_layout_main.rootView.height
            val keyb = sheight - rec.bottom

            if (keyb > sheight * 0.15) {
                view.create_task_question_btn.visibility = View.GONE
            } else {
                view.create_task_question_btn.visibility = View.VISIBLE
            }
        })

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Loading.....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.max = 100
        progressDialog.setCancelable(false)

        return view
    }

    private fun SendData()
    {
        val currentUser = mAuth.currentUser?.uid
        if (currentUser!=null)
        {
            var title = TaskTitle.text.toString().trim()
            var description = TaskDescription.text.toString().trim()
            var id = Taskreference.push().key
            val sharedPreferences = activity?.getSharedPreferences("sharedPreferences_createtask", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putString(Constants.CreateTaskID, id)
            editor?.putString(Constants.CreateTaskImageUrl, downloadUri)
            editor?.apply()
            if (TextUtils.isEmpty(title))
            {
                progressDialog.dismiss()
                TaskTitle.error = "Title is empty!!!"
            }
            else if (downloadUri.isEmpty() && description.isEmpty())
            {
                progressDialog.dismiss()
                Constants.setAlert(requireContext(),"Warning","PLease write your content or upload your content image.")
            }
            else
            {
                val currentTimestamp = System.currentTimeMillis()
                var task = Tasks(downloadUri, title, description, id!!, Child_Age, Publisher, currentUser,
                    currentTimestamp.toString()
                )
                Taskreference.child(id).setValue(task)
                    .addOnSuccessListener {

                        progressDialog.dismiss()
                        val bundle = Bundle()
                        bundle.putString("id", id)
                        bundle.putString("no", "1")
                        bundle.putString("age", Child_Age.toString())
                        bundle.putString("tasktitle", title)
                        val transaction = (activity as CreateTask).supportFragmentManager.beginTransaction()
                        val frag = FragmentQuestion1()
                        frag.arguments = bundle
                        transaction.replace(R.id.create_task_fragment, frag)
                        transaction.addToBackStack(null)
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        transaction.commit()
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Constants.setAlert(requireContext(),"Update","Error : "+it)
                    }
            }
        }

        else
        {
            progressDialog.dismiss()
            Constants.setAlert(requireContext(),"Update","Error : Restart App")
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 456 && resultCode == RESULT_OK
                && data != null && data.data != null) {
            mImageUri = data.data

            TaskText.visibility = View.GONE
            TaskText2.visibility = View.VISIBLE
            Picasso.get().load(mImageUri).into(TaskImage)
        }
        else if (requestCode == 123 && resultCode == RESULT_OK)
        {
            val f: File = File(currentphoto)
            mImageUri = Uri.fromFile(f)

            TaskText.visibility = View.GONE
            TaskText2.visibility = View.VISIBLE

            val bitmap : Bitmap = (handleSamplingAndRotationBitmap(requireContext(), mImageUri)!!)

            TaskImage.setImageBitmap(bitmap)
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

    private fun uploadFile() {
        if (mImageUri != null) {
            val draw = TaskImage.drawable as BitmapDrawable
            val bitmap = draw.bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream)
            val byteArray = stream.toByteArray()
            arrayByte = byteArray
            val fileReference = mStorageRef!!.child(System.currentTimeMillis()
                    .toString() + "." + getFileExtension(mImageUri!!))
            mUploadTask = fileReference.putBytes(arrayByte)
                    .addOnSuccessListener { taskSnapshot ->
                        Constants.setAlert(requireContext(),"Update","Upload Successfully!!")
                        fileReference.downloadUrl.addOnCompleteListener {task->
                            if (task.isSuccessful) {
                                downloadUri = task.result.toString()
                                SendData()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Constants.setAlert(requireContext(),"Update","Error : "+e.message)}
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                        progressDialog.show()

                    }
        } else {
            val builder3 = AlertDialog.Builder(requireActivity())
            builder3.setTitle("Are you want to upload picture?")
            builder3.setMessage("No picture selected.....")
            builder3.setIcon(R.drawable.ic_report)
            builder3.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
            }
            builder3.setNegativeButton("No") { dialog, which ->
                SendData()
                progressDialog.show()

            }
            builder3.show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = activity?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }


    override fun onStart() {
        if(Publisher != "admin") {
            if (!DialogShowUp) {
                if (Child_Age == 0) {
                    DialogShowUp = true
                    ShowDialog()
                } else {
                    if (TotalChild > 1) {
                        DialogShowUp = true
                        if (ClassName != "SelectChild")
                        {
                            ShowDialog2()
                        }
                    }
                }
            }
        }
        super.onStart()
    }

    private fun ShowDialog() {

        childRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("Childs"))
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setCancelable(true)
                            .setOnCancelListener {
                                startActivity(Intent(activity, MainActivity::class.java))
                                activity?.finish()
                            }
                            .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please select child"
                    btn.setOnClickListener {
                        val intent = Intent(activity,SelectChild::class.java)
                        intent.putExtra("class","CreateTask")
                        startActivity(intent)
                    }
                    customDialog.show()
                }
                else
                {
                    val dialogView = layoutInflater.inflate(R.layout.child_selection_warning, null)
                    val customDialog = AlertDialog.Builder(requireContext())
                            .setView(dialogView)
                            .setCancelable(true)
                            .setOnCancelListener {
                                startActivity(Intent(activity, MainActivity::class.java))
                                activity?.finish()
                            }
                            .show()
                    val btn = dialogView.findViewById<Button>(R.id.child_warning_dialog_btn)
                    var text = dialogView.findViewById<TextView>(R.id.child_warning_text)
                    text.text = "Please, Add your child first"
                    btn.setOnClickListener {
                        val intent = Intent(activity, AddChild::class.java)
                        activity?.startActivity(intent)

                    }
                    customDialog.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun ShowDialog2() {
        var name : String = ""
        val sharedPreferences = activity?.getSharedPreferences("sharedPreferences_child", AppCompatActivity.MODE_PRIVATE)
        name = sharedPreferences?.getString(Constants.ChildName,"")!!
        val builder3 = AlertDialog.Builder(requireContext())
        builder3.setTitle("Is it for "+name+ "?")
        builder3.setIcon(R.drawable.ic_report)
        builder3.setCancelable(true)
        builder3.setOnCancelListener {
            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }
        builder3.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
        }

        builder3.setNegativeButton("No") { dialog, which ->
            val ChildIntent = Intent(activity,SelectChild::class.java)
            ChildIntent.putExtra("class","CreateTask")
            startActivity(ChildIntent)
        }
        builder3.show()
    }

    private fun ShowImageDialog()
    {
        val dialogView = layoutInflater.inflate(R.layout.choose_image_dialog, null)
        val customDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .setOnCancelListener {
                    it.dismiss()
                }
                .show()
        val Capbtn = dialogView.findViewById<Button>(R.id.dialog_capture_btn)
        val Pickbtn = dialogView.findViewById<Button>(R.id.dialog_pick_btn)

        Capbtn.setOnClickListener {
            var fileName = String.format("%d.jpg", System.currentTimeMillis())
            var currentSto : File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            imagefile = File.createTempFile(fileName, "jpg", currentSto)
            currentphoto = imagefile.absolutePath
            Imguri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.handikapp.parentbox.fileprovider",
                    imagefile
            )
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Imguri)
            startActivityForResult(intent, 123)
            customDialog.dismiss()
        }
        Pickbtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 456)
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun ShowPermissionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Need camera and storage permissions for image capture..")
        builder.setIcon(R.drawable.ic_report)
        builder.setPositiveButton("Allow") { dialog, which ->

            setupPermissions()

        }

        builder.setNegativeButton("Exit") { dialog, which ->
            activity?.finish()
            System.exit(0)
        }
        builder.show()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA,
        )

        val permission2 = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

        val permission3 = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission4 = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES,
            )
            if (permission2 != PackageManager.PERMISSION_GRANTED && permission != PackageManager.PERMISSION_GRANTED
                && permission3 != PackageManager.PERMISSION_GRANTED && permission4 != PackageManager.PERMISSION_GRANTED) {
                makeRequest()
            }

        } else {
            if (permission2 != PackageManager.PERMISSION_GRANTED && permission != PackageManager.PERMISSION_GRANTED
                && permission3 != PackageManager.PERMISSION_GRANTED) {
                makeRequest()
            }
        }
    }
    private fun makeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES),
                RECORD_REQUEST_CODE
            )
        }
        else
        {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE
            )
        }
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
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if(doublebackexit)
        {
            activity?.finish()
            System.exit(0)
        }
        return doublebackexit

        doublebackexit = false
        Constants.setAlert(requireContext(),"Warning","Please click BACK again to exit")
        Handler().postDelayed({
            doublebackexit = false
        },
                2000)


    }

}