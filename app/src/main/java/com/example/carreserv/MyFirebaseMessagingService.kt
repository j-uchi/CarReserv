package com.example.carreserv

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token);
    }



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(
            TAG,
            "From: " + remoteMessage.getFrom()
        )

        // Check if message contains a data payload.
        if (remoteMessage.getData().size > 0) {
            Log.d(
                TAG,
                "Message data payload: " + remoteMessage.getData()
            )
            if ( /* Check if data needs to be processed by long running job */true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }
        }

        // Check if message contains a notification payload.
        var messageBody = ""
        if (remoteMessage.getNotification() != null) {
            messageBody = remoteMessage.getNotification()!!.getBody().toString()
            Log.d(
                TAG,
                "Message Notification Body: $messageBody"
            )
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(messageBody)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        //String channelId = getString(R.string.default_notification_channel_id);
        val channelId = "001"
        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,
            channelId
        ) //.setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Push1 App")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager!!.createNotificationChannel(channel)
        }
        notificationManager!!.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}