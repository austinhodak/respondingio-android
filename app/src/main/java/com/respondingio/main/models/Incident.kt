package com.respondingio.main.models

import com.google.firebase.Timestamp

data class Incident (
    var key: String? = null,
    var incidentID: String? = null,
    var isActive: Boolean? = false,
    var type: Type? = null,
    var timestamp: Timestamp? = null
) {

    data class Type (
        var CADCode: String? = null,
        var name: String? = null
    )
}