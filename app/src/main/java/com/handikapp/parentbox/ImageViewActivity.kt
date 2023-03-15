package com.handikapp.parentbox

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso

class ImageViewActivity : AppCompatActivity() {
    var imageViewer: ZoomageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view_activity)

        imageViewer = findViewById(R.id.image_viewer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        if (intent.hasExtra("img_url")) {
            val url = intent.getStringExtra("img_url")
            Picasso.get()
                    .load(url)
                    .into(imageViewer)
        }
    }

}