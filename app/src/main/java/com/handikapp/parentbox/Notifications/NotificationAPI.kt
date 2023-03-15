package com.handikapp.parentbox.Notifications

import com.handikapp.parentbox.Utils.Constants.CONTENT_TYPE
import com.handikapp.parentbox.Utils.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): retrofit2.Response<ResponseBody>
}