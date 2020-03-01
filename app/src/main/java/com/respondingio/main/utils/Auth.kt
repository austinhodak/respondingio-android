package com.respondingio.main.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.respondingio.functions.utils.Time

object Auth {

    fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun getUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    @SuppressLint("HardwareIds")
    fun updateOnlineStatus(context: Context) {
        Log.d("DEVICE", "${android.os.Build.MODEL} - ${android.os.Build.DEVICE} - ${android.os.Build.BRAND} - ${android.os.Build.DISPLAY} - ${android.os.Build.ID} - ${android.os.Build.MANUFACTURER}")

        FirebaseDatabase.getInstance().getReference("users/${getUser()?.uid}/devices/${getDeviceInfo()}").updateChildren(mapOf(
            "online" to true,
            "lastOnline" to Time.getCurrentUTC(),
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                "serial" to android.os.Build.getSerial()
            } else {
                "serial" to android.os.Build.SERIAL
            }
        ))
        FirebaseDatabase.getInstance().getReference("users/${getUser()?.uid}/devices/${getDeviceInfo()}").onDisconnect().updateChildren(mapOf(
            "online" to false,
            "lastOffline" to Time.getCurrentUTC()
        ))

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val token = it.token
            FirebaseDatabase.getInstance().getReference("users/${getUser()?.uid}/devices/${getDeviceInfo()}").updateChildren(mapOf(
                "messageToken" to token
            ))
        }
    }

    private fun getDeviceInfo(): String {
        // Replace periods with dashes as Firebase URL cannot contain periods.
        return android.os.Build.MODEL.replace(".","-")
    }


}

