package com.respondingio.main

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.respondingio.functions.agencies.AgencyUtils

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}