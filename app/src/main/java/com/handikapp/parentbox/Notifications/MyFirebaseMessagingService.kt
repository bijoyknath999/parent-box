package com.handikapp.parentbox.Notifications


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.handikapp.parentbox.*
import com.handikapp.parentbox.Utils.AgeCheck
import com.handikapp.parentbox.Utils.Constants


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var numMessages = 0
    private var mAuth: FirebaseAuth? = null
    private var CurrentUserId: String? = null
    var user_id: String? = null
    var notificationTitle: String? = null
    var notificationBody: String? = null
    var AgeLimit: String? = null
    var Category: String? = null
    var Task_ID: String? = null
    var Task_Img_Url: String? = null
    var Task_Publisher_ID: String? = null


    lateinit var pendingIntent : PendingIntent
    lateinit var Notintent : Intent

    override fun onNewToken(token: String) {
        val mFUser = FirebaseAuth.getInstance().currentUser
        if (mFUser != null) {
            SendToServer(token)
        }
    }

    private fun SendToServer(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUser)
        userRef.child("token").setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.size > 0) {
            Log.d(Tag, "Message data payload: " + remoteMessage.data)
            user_id = remoteMessage.data.get("user_id")
            notificationTitle = remoteMessage.data.get("titleText")
            notificationBody = remoteMessage.data.get("messageText")
            AgeLimit = remoteMessage.data.get("age")
            Category = remoteMessage.data.get("category")
            Task_ID = remoteMessage.data.get("task_id")
            Task_Img_Url = remoteMessage.data.get("task_img_url")
            Task_Publisher_ID = remoteMessage.data.get("publisher_id")

        }
        mAuth = FirebaseAuth.getInstance()
        val mFirebaseUser = mAuth!!.currentUser
        if (mFirebaseUser != null) {
            CurrentUserId = mAuth!!.currentUser!!.uid
        }
        if (user_id != null) {
            validityuser()
        } else {
        }
    }

    private fun validityuser() {

        var lock : Boolean
        val sharedPreferences = getSharedPreferences("sharedPreferences_tasks", MODE_PRIVATE)
        lock = sharedPreferences.getBoolean(Constants.Lock, false)

        var age : Int
        val sharedPreferences2 = getSharedPreferences("sharedPreferences_child", MODE_PRIVATE)
        age = sharedPreferences2.getInt(Constants.ChildAge, 0)

        if (CurrentUserId == user_id) {
            Log.d(Tag, "User can't get notification, because user post this request.")
        } else {
            if (Category=="createtask") {
                if (AgeCheck.getCheck(AgeLimit!!.toInt(), age)) {
                    if (!lock) {
                        if(!notificationTitle!!.isEmpty()) {
                            sendNotification()
                        }
                    }
                }
            }
            else{
                if (!lock) {
                    if(notificationTitle != null) {
                        if (Category == "notify")
                        {
                            if (CurrentUserId != Task_Publisher_ID)
                            {
                                sendNotification()
                            }
                        }
                        else
                        {
                            sendNotification()
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification() {
        if (Category == "createtask")
        {
            Notintent = Intent(this, SingleTaskSelect::class.java)
            Notintent.putExtra("task_id", Task_ID)
            Notintent.putExtra("img_url", Task_Img_Url)
            Notintent.putExtra("mode", "free")
        }
        else if (Category == "notify")
        {
            Notintent = Intent(this, SingleTaskSelect::class.java)
            Notintent.putExtra("task_id", Task_ID)
            Notintent.putExtra("img_url", Task_Img_Url)
            Notintent.putExtra("publisher_id",Task_Publisher_ID)
            Notintent.putExtra("mode", "notify")
        }
        else if (Category == "taskselect")
        {
            Notintent = Intent(this, CreateTask::class.java)
            Notintent.putExtra("publisher", "parents")
        }
        else
        {
            Notintent  = Intent(this, MainActivity::class.java)
        }

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.app_icon_512)
        Notintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        pendingIntent = PendingIntent.getActivity(this, 0, Notintent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)
        val notificationBuilder = NotificationCompat.Builder(this, "default")
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                .setContentIntent(pendingIntent)
                .setContentInfo("Hello")
                .setColor(Color.parseColor("#252c4a"))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setNumber(++numMessages)
                .setSmallIcon(R.drawable.ic_notify)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_HIGH)


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val audioAttributes : AudioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            val channel = NotificationChannel(
                    "default", CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESC
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            channel.lockscreenVisibility = View.VISIBLE
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            assert(notificationManager != null)
            notificationManager!!.createNotificationChannel(channel)
        }
        assert(notificationManager != null)
        notificationManager!!.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_NAME = "notification"
        private const val CHANNEL_DESC = "Parent Box Task Notification"
        private const val Tag = "ParentBoxNotifyService"
    }
}
