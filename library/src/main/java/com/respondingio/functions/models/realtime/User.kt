package com.respondingio.functions.models.realtime

data class User (
    var userID: String? = null,
    var online: Boolean? = false,
    var name: Name? = null,
    var responding: Responding? = null
) {

    fun isUserResponding() : Boolean {
        return responding != null
    }

    data class Name (
        var firstName: String? = null,
        var lastName: String? = null
    ) {
        override fun toString(): String {
            return "$firstName $lastName"
        }
    }

    data class Responding (
        var agency: String? = null,
        var station: String? = null,
        var timestamp: Long? = null,
        var respondingTo: String? = null,
        var respondingToType: String? = null
    )
}