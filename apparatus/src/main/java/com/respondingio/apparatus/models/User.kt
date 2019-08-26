package com.respondingio.apparatus.models

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @property agencies List of agencies this user belongs to, one agency will have boolean true, which is primary.
 */

data class User (
    var name: Name? = null,
    var email: String? = null,
    var agencies: HashMap<String, Boolean>? = null
) {

    fun getPrimaryAgencyID(): String? {
        if (agencies == null) return null
        for (agency in agencies!!.iterator()) {
            if (agency.value) return agency.key
        }

        //If all false, return first agency.
        return agencies!!.entries.iterator().next().key
    }

    suspend fun getAgency(agencyID: String? = null): Agency {
        return suspendCoroutine {
            FirebaseFirestore.getInstance().collection("agencies").document(agencyID ?: getPrimaryAgencyID() ?: return@suspendCoroutine).get().addOnSuccessListener { snap ->
                val agency = snap.toObject(Agency::class.java) as Agency
                agency.path = snap.reference.path
                agency.agencyID = snap.id
                it.resume(agency)
            }
        }
    }

    data class Name (
        var firstName: String? = null,
        var lastName: String? = null
    ) {
        override fun toString(): String {
            return "$firstName $lastName"
        }
    }
}