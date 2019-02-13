package com.respondingio.admin.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.admin.models.Incident
import com.respondingio.admin.utils.Auth
import com.respondingio.admin.utils.Firestore
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    val activeIncidentData = MutableLiveData<IncidentListModel>()

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
                for (snapshot in querySnapshot!!.documents) {
                    data.activeIncidentsList.add(snapshot.toObject(Incident::class.java) as Incident)
                }

                data.activeIncidentsList.sortByDescending { it.timestamp }
                activeIncidentData.postValue(data)
            }
        }
    }
}

data class IncidentListModel(
    var activeIncidentsList: ArrayList<Incident> = ArrayList()
)