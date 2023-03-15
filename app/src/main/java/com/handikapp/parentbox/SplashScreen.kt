package com.handikapp.parentbox

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.handikapp.parentbox.Utils.Constants
import com.timqi.sectorprogressview.ColorfulRingProgressView
import java.util.*
import kotlin.collections.ArrayList


class SplashScreen : AppCompatActivity() {

    var crpv: ColorfulRingProgressView? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var SplashText : TextView
    lateinit var handler : Handler
    lateinit var AdminRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        SplashText = findViewById(R.id.splash_text)

        if(user != null) {
            AdminRef = FirebaseDatabase.getInstance().reference.child("Admin")
            AdminRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var mail: String = snapshot.child("mail").value.toString()
                        var about: String = snapshot.child("aboutus").value.toString()
                        var giftingtips: String = snapshot.child("giftingtips").value.toString()
                        var tutorial: String = snapshot.child("tutorial").value.toString()

                        val sharedPreferences = getSharedPreferences(
                                "sharedPreferences_admin",
                                AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = sharedPreferences.edit()
                        editor.putString(Constants.AdminMail, mail)
                        editor.putString(Constants.ABoutUs, about)
                        editor.putString(Constants.GiftingTips, giftingtips)
                        editor.putString(Constants.Tutorial, tutorial)
                        editor.apply()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Constants.setAlert(this@SplashScreen,"Update","Error : "+error)
                }
            })
        }


        val arrayList = ArrayList<String>()//Creating an empty arraylist
        arrayList.add("Change the way you gift your child")
        arrayList.add("Parent Box is an intelligent way to gift your child. When you gift through ParentBox, children get clues to find the gift themselves which is more fun.")//Adding object in arraylist
        arrayList.add("Parent Box is an intelligent way to gift your child. When you gift through ParentBox, you delay gratification and build patience.")
        arrayList.add("Parent Box is an intelligent way to gift your child. When you gift through ParentBox, you teach children new tasks and make them more active by searching for the gift.")
        arrayList.add("Parent Box is an intelligent way to gift your child. When you gift through ParentBox, you teach children ‘good things come to those who wait’ and experience the joy of waiting.")
        arrayList.add("Parent Box is an intelligent way to gift your child. When you gift through ParentBox, the value of the gift is increased as the child waits for the gift.")
        arrayList.add("Parent Box is an intelligent way to gift your child. ParentBox, let you hide the gift and play a mind game of treasure hunt with your child.")
        val random = Random()
        val text : Int = random.nextInt(6)

        SplashText.text = arrayList.get(text)




        crpv = findViewById(R.id.crpv)

        crpv!!.animateIndeterminate()

        val sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        val firstTime = sharedPreferences.getBoolean("firstTime", true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }


        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

                if(firstTime)
            {
                val intent = Intent(this, IntroScreen::class.java)
                startActivity(intent)
                finish()
            }
            else{
                if (user != null) {
                    FirebaseMessaging.getInstance().subscribeToTopic("/topics/All")
                    Constants.getSelectedChild(this)
                    var lock : Boolean
                    val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
                    lock = sharedPreferences.getBoolean(Constants.Lock, false)
                    if (lock)
                    {
                        val mainIntent = Intent(this, LeftClass::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                    else
                    {
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }

                } else {
                    val signInIntent = Intent(this, LogInScreen::class.java)
                    startActivity(signInIntent)
                    finish()
                }
            } } , 3000)

    }

}