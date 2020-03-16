package com.respondingio.main.models

import com.mapbox.mapboxsdk.geometry.LatLng
import com.respondingio.main.R
import java.io.Serializable

data class NotificationIncident (
    var initialText: String?,
    var location: Address?,
    var isActive: Boolean?,
    var timestamp: String?,
    var callType: String?,
    var callTypeBasic: String?
) : Serializable {
    fun getIcon(): Int {
        return when (callTypeBasic) {
            "EMS" -> R.drawable.star_of_life
            "FIRE" -> R.drawable.fire_1
            "MVA" -> R.drawable.icons8_traffic_accident_96
            else -> R.drawable.icons8_fire_alarm_100
        }
    }

    fun getColor(): Int {
        return when (callTypeBasic) {
            "EMS" -> R.color.md_blue_500
            "FIRE" -> R.color.md_red_400
            "MVA" -> R.color.md_orange_500
            else -> R.color.md_blue_500
        }
    }
}

data class Address (
    var fromText: String?,
    var coordinates: Coords?
) : Serializable

data class Coords (
    var longitude: Double?,
    var latitude: Double?
) : Serializable {
    fun toLatLng(): LatLng {
        return LatLng(latitude!!, longitude!!)
    }
}