package com.handikapp.parentbox

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_contact_us.*


class ContactUs : AppCompatActivity() {
    var AdminMail : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        //ad
        val bitmap: Bitmap = (resources.getDrawable(R.drawable.app_logo_1024) as BitmapDrawable).bitmap
        val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(imageRounded)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(
                RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
                200F, 200F, paint) // Round Image Corner 100 100 100 100
        contact_image.setImageBitmap(imageRounded)
        val sharedPreferences =
            getSharedPreferences("sharedPreferences_admin", AppCompatActivity.MODE_PRIVATE)
        AdminMail = sharedPreferences.getString(Constants.AdminMail, "")!!
        if (!AdminMail.isEmpty()) {
            contact_mail.text = AdminMail
            contact_btn.setOnClickListener {

                val email = Intent(Intent.ACTION_SENDTO)
                email.data = Uri.parse("mailto:"+AdminMail)
                startActivity(Intent.createChooser(email, "Choose an Email client :"))

            }
        }
    }
}