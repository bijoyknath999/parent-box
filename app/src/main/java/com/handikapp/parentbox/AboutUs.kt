package com.handikapp.parentbox

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.handikapp.parentbox.Utils.Constants
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_contact_us.*

class AboutUs : AppCompatActivity() {
    var AboutUSText : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)



        val bitmap: Bitmap = (resources.getDrawable(R.drawable.app_logo_1024) as BitmapDrawable).bitmap
        val imageRounded = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(imageRounded)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(
            RectF(10F, 10F, bitmap.width.toFloat(), bitmap.height.toFloat()),
            200F, 200F, paint) // Round Image Corner 100 100 100 100
        about_image.setImageBitmap(imageRounded)
        val sharedPreferences =
            getSharedPreferences("sharedPreferences_admin", AppCompatActivity.MODE_PRIVATE)
        AboutUSText = sharedPreferences.getString(Constants.ABoutUs, "")!!
        if (!AboutUSText.isEmpty()) {
            val face2 = Typeface.createFromAsset(assets, "fonts/Abel-Regular.ttf")
            about_text.typeface = face2
            about_text.text = AboutUSText
        }

    }
}