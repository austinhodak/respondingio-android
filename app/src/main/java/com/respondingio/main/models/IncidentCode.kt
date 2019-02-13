package com.respondingio.main.models

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.io.Serializable

@IgnoreExtraProperties
data class IncidentCode (
    var dispatchCode: String? = null,
    @PropertyName("userCode")
    var userFacingName: String? = dispatchCode,
    var color: String? = null,
    var priority: String? = "AUTO",
    var type: String? = null,
    var reference: DatabaseReference? = null,
    var matchExact: Boolean? = false
) : Serializable

enum class Priority (key: String) {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW")
}