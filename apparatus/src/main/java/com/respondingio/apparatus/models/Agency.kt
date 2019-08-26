package com.respondingio.apparatus.models

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class Agency (
    var path: String = "",
    var agencyID: String? = null,
    var agencyName: String? = null,
    var agencyType: HashMap<String, Boolean>? = null,
    var county: County? = null
) {

    suspend fun getActiveIncidents(): List<Incident> {
        return suspendCoroutine {
            FirebaseFirestore.getInstance().document(path).collection("incidents").whereEqualTo("isActive", true).get().addOnSuccessListener { list ->
                it.resume(list.toObjects(Incident::class.java))
            }
        }
    }

    data class County (
        var countyCode: String? = null,
        var state: String? = null,
        var countyName: String? = null
    ) {
        override fun toString(): String {
            if (state != null && countyCode != null) {
                return "$state $countyCode"
            } else if (state == null && countyCode != null) {
                return countyCode!!
            } else if (state != null) {
                return state!!
            }
            return super.toString()
        }

        fun countyName(): String {
            return if (countyName?.contains("County") == true) {
                "$countyName, $state"
            } else {
                "$countyName County, $state"
            }
        }
    }
}

