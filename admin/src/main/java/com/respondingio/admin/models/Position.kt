package com.respondingio.admin.models

import com.google.firebase.database.Exclude

data class Position (
    @Exclude
    var ID: String? = null,
    var name: String? = null,
    var color: String? = null,
    var type: String? = null
) {

}