package com.respondingio.admin

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.respondingio.functions.utils.Realtime

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        initFirebase()
    }

    private fun initFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        Realtime.agencyData().setPersistenceEnabled(true)
    }
}