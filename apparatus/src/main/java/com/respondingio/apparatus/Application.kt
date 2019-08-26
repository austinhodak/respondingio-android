package com.respondingio.apparatus

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class Application : Application() {

    override fun onCreate() {
        super.onCreate()


        initFirebase()
    }

    private fun initFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}