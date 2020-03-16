package com.respondingio.main.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.respondingio.main.models.NotificationIncident
import org.jetbrains.anko.*
import org.json.JSONObject

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("TEST", remoteMessage.data.toString())

        remoteMessage.data.isNotEmpty().let {
            startActivity(intentFor<IncidentPopupActivity>("incident" to Gson().fromJson(remoteMessage.data["incident"], NotificationIncident::class.java)).newTask().clearTop())
        }
    }
}