package com.respondingio.functions.models.firestore

import com.google.firebase.firestore.FirebaseFirestore

val defaultResponseOptions: List<Agency.ResponseOption> = listOf(
    Agency.ResponseOption("Station", "STATION"),
    Agency.ResponseOption("Scene", "SCENE"),
    Agency.ResponseOption("Delayed", "DELAYED"),
    Agency.ResponseOption("Unavailable", "UNAVAILABLE")
)

data class Agency (
    var default: Boolean? = null,
    var agencyID: String? = null,
    var stations: List<Station>? = null,
    var path: String = "",
    var agencyName: String? = null,
    var shortName: String? = agencyName,
    var responseOptions: List<ResponseOption>? = defaultResponseOptions
) {

    fun loadStations() {
        FirebaseFirestore.getInstance().collection("agencies").document(agencyID!!).collection("stations").get().addOnSuccessListener {
            stations = it.toObjects(Station::class.java)
        }
    }

    data class Station (
        var stationName: String? = null
    )

    data class ResponseOption (
        var name: String? = null,
        var type: String? = null
    )
}