package com.respondingio.main

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.mapboxsdk.Mapbox
import com.respondingio.functions.agencies.AgencyUtils

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
        Mapbox.getInstance(applicationContext, "pk.eyJ1IjoiYWhvZGFrIiwiYSI6ImNpcTcwMGJibjAwaWNmbWtreDA2MXpkOXgifQ.vxl9fB06MPmMGuvE4hXotA")
    }

    private fun initFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance("https://responding-io-agency.firebaseio.com/").setPersistenceEnabled(true)
    }
}