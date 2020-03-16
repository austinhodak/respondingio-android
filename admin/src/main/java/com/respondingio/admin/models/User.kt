package com.respondingio.admin.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.admin.utils.Firestore
import com.respondingio.functions.models.realtime.Position
import com.respondingio.functions.utils.Realtime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @property agencies List of agencies this user belongs to, one agency will have boolean true, which is primary.
 */

data class User (
    @DocumentId
    var UID: String? = null,
    var name: Name? = null,
    var email: String? = null,
    var agencies: HashMap<String, Boolean>? = null,
    var positions: HashMap<String, ArrayList<String>>? = null
) {

    fun getPrimaryAgencyID(): String? {
        if (agencies == null) return null
        for (agency in agencies!!.iterator()) {
            if (agency.value) return agency.key
        }

        //If all false, return first agency.
        return agencies!!.entries.iterator().next().key
    }

    fun getPositionsList(agencyID: String): ArrayList<String>? {
        return positions?.get(agencyID);
    }

    fun getPositionsList(vararg agencyIDs: String): ArrayList<String>? {
        val mList = ArrayList<String>()
        for (i in agencyIDs) {
            mList.addAll(positions?.get(i)!!)
        }
        return mList
    }

    fun getAgencyIDs(): Array<String> {
        return agencies!!.keys.toTypedArray()
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

    fun updateUserPositions(updatedList: List<Position>, agencyID: String) {
        val childUpdates = HashMap<String, Any>()
        for (i in updatedList) {
            childUpdates["/${i.ID}"] = true
        }

        Realtime.primary().getReference("users/$UID/positions/$agencyID").updateChildren(childUpdates)
        Firestore.i().collection("users").document(UID!!).update(mapOf(
            "positions.$agencyID" to updatedList.map { it.ID }
        ))
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