package com.respondingio.admin.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.admin.models.Agency
import com.respondingio.admin.models.AgencyUser
import com.respondingio.admin.models.User
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

    suspend fun getAgency(agencyID: String): Agency {
        val mFirestore = FirebaseFirestore.getInstance()

        return suspendCoroutine {
            mFirestore.collection("agencies")
                .document(agencyID)
                .get().addOnSuccessListener { snapshot ->
                    it.resume(snapshot.toObject(Agency::class.java) as Agency)
                }
        }
    }

}