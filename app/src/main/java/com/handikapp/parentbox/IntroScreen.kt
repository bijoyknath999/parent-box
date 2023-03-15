package com.handikapp.parentbox

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment.Companion.newInstance
import com.github.appintro.AppIntroFragment
import com.google.android.gms.dynamic.FragmentWrapper
import com.handikapp.parentbox.Fragments.FragmentIntroTutorial
import com.handikapp.parentbox.Utils.Constants


class IntroScreen: AppIntro() {
    private val RECORD_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addSlide(
            AppIntroFragment.newInstance(
                "Welcome to Parent Box!",
                "when you gift through Parent Box, you teach children something new, build their patience and let them have some fun",
                imageDrawable = R.drawable.mom_child,
                backgroundDrawable = R.color.quizbgColor
            )
        )

        addSlide(FragmentIntroTutorial())





    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        setupPermissions()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        setupPermissions()
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
        else
        {
            val sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("firstTime", false)
            editor.apply()
            var intent = Intent(this, LogInScreen::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
                    val sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("firstTime", false)
                    editor.apply()
                    var intent = Intent(this, LogInScreen::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("firstTime", false)
                    editor.apply()
                    var intent = Intent(this, LogInScreen::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

}