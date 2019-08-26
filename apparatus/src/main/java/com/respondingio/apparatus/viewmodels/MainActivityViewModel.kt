package com.respondingio.apparatus.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.apparatus.models.Apparatus
import com.respondingio.apparatus.models.Incident
import com.respondingio.apparatus.models.UserDB
import com.respondingio.apparatus.onboarding.ApparatusPicker
import com.respondingio.apparatus.utils.Auth
import com.respondingio.apparatus.utils.Firestore
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    val activeIncidentData = MutableLiveData<IncidentListModel>()
    val apparatusData = MutableLiveData<Apparatus>()
    val respondingData = MutableLiveData<RespondingListModel>()

    fun getActiveIncidents(agencyID: String? = null) {
        val data = IncidentListModel()
        //If agencyID is not provided, check all users agencies.
        if (agencyID == null) {
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                for (agency in Firestore.getUser(Auth.getUser()!!).agencies!!.keys) {
                    FirebaseFirestore.getInstance().collection("agencies").document(agency).collection("incidents").whereEqualTo("isActive", true).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        for (snapshot in querySnapshot!!.documents) {
                            val incident = snapshot.toObject(Incident::class.java) as Incident
                            incident.key = snapshot.id
                            val prev = data.activeIncidentsList.find { it.key == incident.key }
                            if (prev == null) {
                                data.activeIncidentsList.add(incident)
                            } else {
                                val index = data.activeIncidentsList.indexOf(prev)
                                data.activeIncidentsList.removeAt(index)
                                data.activeIncidentsList.add(index, incident)
                            }

                        }

                        data.activeIncidentsList.sortByDescending { it.timestamp }
                        activeIncidentData.postValue(data)
                    }
                }
            }
        } else {
            FirebaseFirestore.getInstance().collection("agencies").document(agencyID).collection("incidents").whereEqualTo("isActive", true).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                data.activeIncidentsList.clear()

                for (snapshot in querySnapshot!!.documents) {
                    data.activeIncidentsList.add(snapshot.toObject(Incident::class.java) as Incident)
                }

                data.activeIncidentsList.sortByDescending { it.timestamp }
                activeIncidentData.postValue(data)

            }
        }
    }

    fun loadApparatus(apparatusID: String) {
        FirebaseFirestore.getInstance().collection("apparatus").document(apparatusID).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            apparatusData.postValue(documentSnapshot?.toObject(Apparatus::class.java))
        }
    }

    fun loadResponding(agencyID: String) {
        FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agencyID).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val respondingListModel = RespondingListModel()

                for (item in p0.children) {
                    respondingListModel.respondingList.add(item.getValue(UserDB::class.java)!!)
                }

                respondingListModel.respondingList.sortBy { it.name.toString() }

                respondingData.postValue(respondingListModel)
            }
        })
    }
}

data class IncidentListModel (
    var activeIncidentsList: ArrayList<Incident> = ArrayList()
)

data class RespondingListModel (
    var respondingList: MutableList<UserDB> = ArrayList()
)