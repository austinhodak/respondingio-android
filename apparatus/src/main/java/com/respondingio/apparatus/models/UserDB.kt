package com.respondingio.apparatus.models

data class UserDB (
    var name: Name? = null,
    var responding: Response? =  null
) {

    data class Response (
        var agency: String? = null,
        var station: String? = null,
        var respondingTo: String? = null,
        var time: String? = null
    )

    data class Name (
        var firstName: String? = null,
        var lastName: String? = null
    ) {
        override fun toString(): String {
            return "$firstName $lastName"
        }
    }
}