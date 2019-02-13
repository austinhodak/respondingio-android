package com.respondingio.main.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.main.models.AgencyUser
import com.respondingio.main.models.User
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Firestore {

    suspend fun getUser(firebaseUser: FirebaseUser): User {
        val mFirestore = FirebaseFirestore.getInstance()

        return suspendCoroutine {
            mFirestore.collection("users")
                .document(firebaseUser.uid)
                .get().addOnSuccessListener { snapshot ->
                    it.resume(snapshot.toObject(User::class.java) as User)
                }
        }
    }

}