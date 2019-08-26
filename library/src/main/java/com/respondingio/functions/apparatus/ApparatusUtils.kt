package com.respondingio.functions.apparatus

import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.functions.models.firestore.Apparatus
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApparatusUtils {

    suspend fun getApparatusByID (apparatusID: String): Apparatus {
        return suspendCoroutine { coro ->
            FirebaseFirestore.getInstance().collection("apparatus").document(apparatusID).get().addOnSuccessListener {
                coro.resume(it.toObject(Apparatus::class.java)!!)
            }
        }
    }

    suspend fun getAllAgencyApparatus (agencyID: String): List<Apparatus> {
        return suspendCoroutine { coro ->
            FirebaseFirestore.getInstance().collection("apparatus").whereEqualTo("agencyID", agencyID).orderBy("apparatusName").get().addOnSuccessListener {
                coro.resume(it.toObjects(Apparatus::class.java))
            }
        }
    }
}