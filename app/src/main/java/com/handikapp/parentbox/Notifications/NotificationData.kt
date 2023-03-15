package com.handikapp.parentbox.Notifications

data class NotificationData(
    val titleText: String,
    val messageText: String,
    val task_id: String,
    val task_img_url: String,
    val category: String,
    val user_id: String,
    val publisher_id: String,
    val age: Int

)