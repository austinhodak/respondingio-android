package com.respondingio.functions.incidents

import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.functions.models.firestore.Apparatus
import com.respondingio.functions.models.firestore.Incident
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object IncidentUtils {

    suspend fun getIncidentByID (agencyID: String, incidentID: String): Incident {
        return suspendCoroutine { coro ->
            FirebaseFirestore.getInstance().collection("agencies").document(agencyID).collection("incidents").document(incidentID).get().addOnSuccessListener {
                coro.resume(it.toObject(Incident::class.java)!!)
            }
        }
    }
}