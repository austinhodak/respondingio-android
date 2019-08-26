package com.respondingio.apparatus.models

data class AgencyUser (
    var name: Name? = null
) {

    data class Name (
        var firstName: String? = null,
        var lastName: String? = null
    )
}