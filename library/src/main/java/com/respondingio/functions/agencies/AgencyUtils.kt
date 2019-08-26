package com.respondingio.functions.agencies

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.functions.models.firestore.Agency
import com.respondingio.functions.models.firestore.User

object AgencyUtils {

    val agencies: MutableList<Agency> = ArrayList()
    val agenciesIDs: MutableList<String> = ArrayList()

    fun loadUserAgencies(userID: String) {
        agencies.clear()
        agenciesIDs.clear()
        FirebaseDatabase.getInstance().getReference("users").child(userID).child("agencies").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snap in p0.children) {
                    Log.d("AGENCIES", snap.key!!)
                    agenciesIDs.add(snap.key!!)
                }
            }
        })
        FirebaseFirestore.getInstance().collection("users").document(userID).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            for (agency in user?.agencies!!) {
                FirebaseFirestore.getInstance().collection("agencies").document(agency.key).get().addOnSuccessListener { agency ->
                    val agencyObject = agency.toObject(Agency::class.java)!!
                    agencyObject.agencyID = agency.id
                    agencies.add(agencyObject)
                    agencies.sortBy { it.agencyName }
                }
            }
        }
    }
}