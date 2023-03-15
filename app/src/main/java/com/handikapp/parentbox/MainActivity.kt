package com.handikapp.parentbox

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.handikapp.parentbox.Fragments.FragmentAddChild
import com.handikapp.parentbox.Fragments.FragmentHome
import com.handikapp.parentbox.Fragments.FragmentPlaymode
import com.handikapp.parentbox.Utils.Constants
import com.handikapp.parentbox.Utils.CountTimer
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_contact_us.*
import me.ibrahimsn.lib.SmoothBottomBar
import java.util.*


class MainActivity : AppCompatActivity(R.layout.activity_main){
    private lateinit var  mtoolbar: Toolbar
    private lateinit var  drawer_layout : DrawerLayout
    private lateinit var nav_view : NavigationView
    private lateinit var bottomBar : SmoothBottomBar
    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private lateinit var navViewheader : View
    lateinit var PImage : CircleImageView
    lateinit var PName : TextView
    var personEmail : String = ""
    val TAG = "MainActivity"
    lateinit var AdminRef: DatabaseReference
    lateinit var UserRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var doublebackexit = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomBar = findViewById(R.id.bottomBar)
        mtoolbar =  findViewById(R.id.main_page_toolbar)
        drawer_layout = findViewById(R.id.drawable_layout)
        nav_view = findViewById(R.id.navigation_view)
        mtoolbar.title = "Home"
        mtoolbar.setTitleTextColor(Color.WHITE)
        mtoolbar.overflowIcon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_three_dot)
        setSupportActionBar(mtoolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        navViewheader = nav_view.inflateHeaderView(R.layout.nav_header)
        PImage = navViewheader.findViewById(R.id.profile_image)
        PName = navViewheader.findViewById(R.id.profile_name)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()




        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            personEmail = acct.email!!
            val personPhoto: Uri? = acct.photoUrl
            val FinalURL = personPhoto.toString()
            var imageURL = FinalURL.replace("s96-c", "s384-c", true)
            PName.text = personName

            Picasso.get()
                    .load(imageURL)
                    .into(PImage)
        }


        AdminRef = FirebaseDatabase.getInstance().reference.child("Admin")
        AdminRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var mail: String = snapshot.child("mail").value.toString()
                    var about: String = snapshot.child("aboutus").value.toString()
                    var giftingtips: String = snapshot.child("giftingtips").value.toString()
                    var tutorial: String = snapshot.child("tutorial").value.toString()

                    val sharedPreferences = getSharedPreferences("sharedPreferences_admin", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString(Constants.AdminMail, mail)
                    editor.putString(Constants.ABoutUs, about)
                    editor.putString(Constants.GiftingTips, giftingtips)
                    editor.putString(Constants.Tutorial, tutorial)
                    editor.apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Constants.setAlert(this@MainActivity,"Update","Error : "+error)
            }
        })

        var currentUser = mAuth.currentUser!!.uid

        UserRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser)

        //Token Generate
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        UserRef.child("token").setValue(token)
                    }
                }


        supportFragmentManager.beginTransaction().replace(R.id.main_fragment, FragmentHome()).commit()

        bottomBar.itemActiveIndex = 0
        bottomBar.onItemSelected = {
            var pos = "$it"
            if (pos=="0")
            {
                supportFragmentManager.beginTransaction().replace(
                        R.id.main_fragment,
                        FragmentHome()
                ).commit()
            }
            else if (pos=="1")
            {
                supportFragmentManager.beginTransaction().replace(
                        R.id.main_fragment,
                        FragmentAddChild()
                ).commit()
            }
            else if (pos=="2")
            {
                supportFragmentManager.beginTransaction().replace(
                        R.id.main_fragment,
                        FragmentPlaymode()
                ).commit()
            }
        }




        actionBarDrawerToggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawer_layout,
                mtoolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        actionBarDrawerToggle.drawerArrowDrawable.color = Color.WHITE
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.about_us -> startActivity(Intent(this, AboutUs::class.java))
                R.id.your_tasks -> startActivity(Intent(this, YourTasks::class.java))
                R.id.your_childs -> startActivity(Intent(this, ChildsList::class.java))
                R.id.trophies_list -> startActivity(Intent(this, TrophyRedeem::class.java))
                R.id.Completed_task_list -> startActivity(Intent(this, CompletedTask::class.java))
                R.id.contact_us -> startActivity(Intent(this, ContactUs::class.java))
                R.id.gifting_tips -> startActivity(Intent(this, GiftingTips::class.java))
                R.id.learn_more -> startActivity(Intent(this, Tutorial::class.java))
                R.id.share_app -> {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Parent Box")
                    shareIntent.putExtra(
                            Intent.EXTRA_TEXT, "Parent Box Link : https://play.google.com/store/apps/details?id=$packageName")
                    startActivity(shareIntent, null)
                }




            }
            true
        }

        val sharedPreferences2 = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
        val editor2 = sharedPreferences2.edit()
        editor2.clear()
        editor2.commit()
        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()


        FirebaseMessaging.getInstance().subscribeToTopic("/topics/All")



    }

    fun setActionBarTitle(title: String) {
        mtoolbar.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun clearandstop()
    {
        val intent = Intent(this,CountTimer::class.java)
        stopService(intent)
        val sharedPreferences2 = getSharedPreferences("sharedPreferences_timer", MODE_PRIVATE)
        val editor2 = sharedPreferences2.edit()
        editor2.clear()
        editor2.commit()
        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }


    override fun onStart() {

        clearandstop()

            var currentuser = mAuth.currentUser!!.uid
            if (currentuser == null) {
                startActivity(Intent(this, LogInScreen::class.java))
                finish()
        }

        super.onStart()
    }


    override fun onStop() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
        {
            drawer_layout.close()
        }
        super.onStop()
    }

    override fun onBackPressed() {
        if(doublebackexit)
        {
            finishAffinity()
            System.exit(0)
        }
        doublebackexit = true
        Constants.setAlert(this,"Warning","Please click BACK again to exit")
        Handler().postDelayed({
            doublebackexit = false
        },
        2000)
    }
}