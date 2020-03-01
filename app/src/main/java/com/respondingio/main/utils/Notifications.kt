package com.respondingio.main.utils

import com.google.firebase.messaging.FirebaseMessaging

object Notifications {

   fun updateTopics(agencies: HashMap<String, Boolean>) {
       for (agency in agencies) {
           val agencyID = agency.key
           FirebaseMessaging.getInstance().subscribeToTopic(agencyID).addOnCompleteListener {

           }
       }
   }
}

