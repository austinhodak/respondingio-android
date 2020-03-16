package com.respondingio.functions.utils

import com.google.firebase.database.*
import com.respondingio.functions.models.realtime.Position
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Realtime {

    fun primary(): FirebaseDatabase { return FirebaseDatabase.getInstance("https://responding-io-6fa8e.firebaseio.com/") }

    fun agencyData(): FirebaseDatabase { return FirebaseDatabase.getInstance("https://responding-io-agency.firebaseio.com/") }

    fun tables(): FirebaseDatabase { return FirebaseDatabase.getInstance("https://responding-io-tables.firebaseio.com/") }

    suspend fun getPositions(agencyID: String): ArrayList<Position> {
        return suspendCoroutine {
            Realtime.agencyData().reference.child("${agencyID}/positions").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val mList = ArrayList<Position>()
                    for (item in p0.children) {
                        val position = item.getValue(Position::class.java)
                        position?.ID = item.key
                        mList.add(position!!)
                    }
                    it.resume(mList)
                }
            })
        }
    }
}