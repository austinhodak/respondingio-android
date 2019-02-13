package com.respondingio.admin.models

data class AgencyUser (
    var name: Name? = null
) {

    data class Name (
        var firstName: String? = null,
        var lastName: String? = null
    )
}