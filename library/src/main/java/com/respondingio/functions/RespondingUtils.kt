package com.respondingio.functions

import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.respondingio.functions.models.firestore.Agency
import com.respondingio.functions.models.realtime.User
import com.respondingio.functions.utils.Time

object RespondingUtils {

    fun markUserResponding(userID: String, respondingTo: String, agencyID: String, option: Agency.ResponseOption): Task<Void> {
        val respondingObject = User.Responding (
            respondingTo = respondingTo,
            respondingToType = option.type?.toUpperCase(),
            agency = agencyID,
            timestamp = Time.getCurrentUTC()
        )

        return FirebaseDatabase.getInstance().getReference("users").child(userID).child("responding").setValue(respondingObject)
    }

    fun markUserOnScene(userID: String): Task<Void> {
        return FirebaseDatabase.getInstance().getReference("users").child(userID).child("responding").updateChildren(
            mapOf(
                "onScene" to true,
                "onSceneTime" to Time.getCurrentUTC()
            ))
    }

    fun markUserResponding(userID: String, responding: User.Responding): Task<Void> {
        return FirebaseDatabase.getInstance().getReference("users").child(userID).child("responding").setValue(
            responding
        )
    }

    fun clearAgencyRespondingUser(agencyID: String) {
        FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agencyID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.hasChildren()) {
                    return
                } else {
                    for (snap in p0.children) {
                        //val user = snap.getValue(User::class.java)!!
                        FirebaseDatabase.getInstance().getReference("users/${snap.key}/responding").removeValue()
                    }
                }
            }
        })
    }

}