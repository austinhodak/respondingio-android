package com.respondingio.main.utils

import com.google.firebase.messaging.FirebaseMessaging
import com.respondingio.main.BuildConfig

object Notifications {

   fun updateTopics(agencies: HashMap<String, Boolean>) {
       for (agency in agencies) {
           val agencyID = agency.key
           if (BuildConfig.DEBUG) {
               FirebaseMessaging.getInstance().subscribeToTopic("$agencyID-DEBUG")
               FirebaseMessaging.getInstance().subscribeToTopic(agencyID)
           } else {
               FirebaseMessaging.getInstance().unsubscribeFromTopic("$agencyID-DEBUG")
               FirebaseMessaging.getInstance().subscribeToTopic(agencyID)
           }
       }
   }
}

