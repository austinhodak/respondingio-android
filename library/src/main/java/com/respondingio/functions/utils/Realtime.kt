package com.respondingio.functions.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Realtime {

    fun primary(): FirebaseDatabase { return FirebaseDatabase.getInstance("https://responding-io-6fa8e.firebaseio.com/") }

    fun agencyData(): FirebaseDatabase { return FirebaseDatabase.getInstance("https://responding-io-agency.firebaseio.com/") }

    fun tables(): FirebaseDatabase { return FirebaseDatabase.getInstance("https://responding-io-tables.firebaseio.com/") }

}