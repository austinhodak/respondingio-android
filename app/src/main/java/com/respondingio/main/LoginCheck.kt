package com.respondingio.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.respondingio.main.home.MainActivity
import com.respondingio.main.utils.Auth
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class LoginCheck : AppCompatActivity() {

    private val RC_SIGN_IN = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Auth.isUserLoggedIn()) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN)
        } else {
            launchMain()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                launchMain()
                finish()
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    toast("You must log in.")
                    return
                }

                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    toast("No internet connection.")
                    return
                }

                toast("Error")
            }
        }
    }

    private fun launchMain() {
        Auth.updateOnlineStatus(this)
        startActivity<MainActivity>()
        finish()
    }
}