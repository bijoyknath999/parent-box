package com.handikapp.parentbox

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.handikapp.parentbox.FirebaseDatabase.UsersModel
import com.handikapp.parentbox.Utils.Constants
import com.shobhitpuri.custombuttons.GoogleSignInButton
import com.squareup.picasso.Picasso

class LogInScreen : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 120
    }
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var progressDialog: ProgressDialog




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_screen)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading.....")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.max = 100
        progressDialog.setCancelable(false)


        val Button = findViewById<GoogleSignInButton>(R.id.SignInBtn)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()

        Button.setOnClickListener {
            progressDialog.show()
            SignIn()
        }


    }


    private fun SignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            } else {
                progressDialog.dismiss()
                Log.w("SignInActivity", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                     FirebaseMessaging.getInstance().subscribeToTopic("/topics/All")
                    // Sign in success, update UI with the signed-in user's information
                    Constants.setAlert(this,"Update","Sign In Successfully")
                    val intent = Intent(this, AddChild::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    progressDialog.dismiss()

                    // If sign in fails, display a message to the user.
                    Constants.setAlert(this,"Update","Sign In Failed")
                }
            }
    }

    override fun onStart() {
        val user = mAuth.currentUser

        if (user != null) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
        super.onStart()
    }
}