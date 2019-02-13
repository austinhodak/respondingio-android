package com.respondingio.main.models

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class Agency (
    var path: String = "",
    var agencyID: String? = null,
    var agencyName: String? = null
) {

    suspend fun getActiveIncidents(): List<Incident> {
        return suspendCoroutine {
            FirebaseFirestore.getInstance().document(path).collection("incidents").whereEqualTo("isActive", true).get().addOnSuccessListener { list ->
                it.resume(list.toObjects(Incident::class.java))
            }
        }
    }
}

