package com.respondingio.main.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.functions.agencies.AgencyUtils
import com.respondingio.functions.models.firestore.Agency
import com.respondingio.functions.models.firestore.Incident
import com.respondingio.functions.models.realtime.User
import com.respondingio.main.utils.Auth
import com.respondingio.main.utils.Firestore
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    val activeIncidentData = MutableLiveData<IncidentListModel>()
    val userData = MutableLiveData<User>()
    val respondingData = MutableLiveData<RespondingListModel>()

    val agencies = MutableLiveData<MutableList<Agency>>()

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

    fun getRealtimeUserData(userID: String) {
        FirebaseDatabase.getInstance().getReference("users").child(userID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) userData.postValue(p0.getValue(User::class.java))
            }
        })
    }

    fun loadResponding(agencyID: String) {
        FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agencyID).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val respondingListModel = RespondingListModel()

                for (item in p0.children) {
                    val user = item.getValue(User::class.java)!!
                    user.userID = item.key
                    respondingListModel.respondingList.add(user)
                }

                respondingListModel.respondingList.sortBy { it.name.toString() }

                respondingData.postValue(respondingListModel)
            }
        })
    }

    fun loadUserAgencies(userID: String) {
        val list: MutableList<Agency> = ArrayList()
        FirebaseFirestore.getInstance().collection("users").document(userID).get().addOnSuccessListener {
            val user = it.toObject(com.respondingio.functions.models.firestore.User::class.java)
            for (agency in user?.agencies!!) {
                FirebaseFirestore.getInstance().collection("agencies").document(agency.key).get().addOnSuccessListener { agency ->
                    val agencyObject = agency.toObject(Agency::class.java)!!
                    agencyObject.agencyID = agency.id

                    Log.d("AGENCYVIEW", agencyObject.toString())
                    list.add(agencyObject)
                    list.sortBy { it.agencyName }

                    agencies.postValue(list)
                }
            }
        }
    }
}

data class IncidentListModel(
    var activeIncidentsList: ArrayList<Incident> = ArrayList()
)

data class RespondingListModel (
    var respondingList: MutableList<User> = ArrayList()
)