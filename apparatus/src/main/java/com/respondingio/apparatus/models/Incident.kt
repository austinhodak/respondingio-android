package com.respondingio.apparatus.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Incident (
    var key: String? = null,
    var incidentID: String? = null,
    var isActive: Boolean? = false,
    var type: Type? = null,
    var timestamp: Timestamp? = null,
    var alert: Alert? = null,
    var remarks: ArrayList<String>? = null,
    var address: Address? = null,
    var units: ArrayList<Unit>? = null
) {

    data class Type (
        var CADCode: String? = null,
        var name: String? = null,
        var color: String? = null,
        var priority: String? = "UNKN"
    )

    data class Alert (
        var severity: String? = null,
        var text: String? = null
    )

    data class Address (
        var fromText: String? = null
    )

    data class Unit (
        var unitName: String? = null,
        var reference: DocumentReference? = null,
        var unitColor: String? = null
    )
}