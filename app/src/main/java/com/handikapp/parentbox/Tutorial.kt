package com.handikapp.parentbox

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.handikapp.parentbox.Utils.Constants

class  Tutorial : AppIntro() {

    var ActName : String = ""
    private val RECORD_REQUEST_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("class")) {
            ActName = intent.getStringExtra("class")!!
        }

        isWizardMode = true
        addSlide(
                AppIntroFragment.newInstance(
                        "Step 1",
                        "Take a picture of the gift using ParentBox.",
                        imageDrawable = R.drawable.con_ss1,
                        backgroundDrawable = R.color.quizbgColor
                )
        )

        addSlide(
                AppIntroFragment.newInstance(
                        "Step 2",
                        "Hide gift in a safe place and enter clues inside ParentBox to find it.",
                        imageDrawable = R.drawable.con_ss2,
                        backgroundDrawable = R.color.quizbgColor
                )
        )

        addSlide(
                AppIntroFragment.newInstance(
                        "Step 3",
                        "Select 2 tasks for the challenge (select from pre-existing tasks or create your own. Use links, image etc and create your own task!)",
                        imageDrawable = R.drawable.con_ss3,
                        backgroundDrawable = R.color.quizbgColor
                )
        )

        addSlide(
                AppIntroFragment.newInstance(
                        "Step 4",
                        "Choose how long the child has to wait for the gift and set the timer (The longer the child waits, the more delayed the gratification). Lock the challenge and give the phone to the child.",
                        imageDrawable = R.drawable.con_ss4,
                        backgroundDrawable = R.color.quizbgColor
                )
        )

        addSlide(
                AppIntroFragment.newInstance(
                        "Step 5",
                        "Child completes task1 which unlocks the picture of the gift. Now the child has to wait for the 2nd task.",
                        imageDrawable = R.drawable.con_ss5,
                        backgroundDrawable = R.color.quizbgColor
                )
        )

        addSlide(
                AppIntroFragment.newInstance(
                        "Step 6",
                        "The app notifies when the 2nd task is available after the set time. The child can then complete the task which will reveal the clues. The child now uses the clues to find the gift.",
                        imageDrawable = R.drawable.con_ss6,
                        backgroundDrawable = R.color.quizbgColor
                )
        )
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        if (ActName == "intro")
        {
            setupPermissions()
        }
        else
        {
            finish()
        }
        super.onSkipPressed(currentFragment)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        if (ActName == "intro")
        {
            setupPermissions()
        }
        else
        {
            finish()
        }
        super.onDonePressed(currentFragment)
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