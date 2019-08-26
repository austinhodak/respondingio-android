package com.respondingio.functions.models.firestore

import android.content.Context
import com.respondingio.functions.R

data class Apparatus (
    var agencyID: String? = null,
    var apparatusID: String? = null,
    var apparatusName: String? = null,
    var apparatusCode: String? = null,
    var selected: Boolean? = false,
    var status: ApparatusStatus? = ApparatusStatus()
)

data class ApparatusStatus (
    var inService: Boolean = true,
    var inStation: Boolean = true,
    var responding: Boolean = false,
    var returning: Boolean = false,
    var onscene: Boolean = false
) {
    fun statusString(): String {
        if (onscene) {
            return "On Scene"
        }

        if (responding) {
            return "Responding"
        }

        if (returning) {
            return "Returning"
        }

        if (inService && inStation) {
            return "In Service • In Station"
        }

        if (!inService && inStation) {
            return "Out of Service • In Station"
        }

        if (inService && !inStation) {
            return "In Service • Out of Station"
        }

        if (!inService && !inStation) {
            return "Out of Service • Out of Station"
        }
        return super.toString()
    }

    fun getColor(context: Context): Int {
        if (onscene) {
            return context.resources.getColor(R.color.md_pink_500)
        }

        if (responding) {
            return context.resources.getColor(R.color.md_red_500)
        }

        if (returning) {
            return context.resources.getColor(R.color.md_green_500)
        }

        if (inService && inStation) {
            return context.resources.getColor(R.color.md_grey_850)
        }

        if (!inService && inStation) {
            return context.resources.getColor(R.color.md_orange_500)
        }

        if (inService && !inStation) {
            return context.resources.getColor(R.color.md_blue_500)
        }

        if (!inService && !inStation) {
            return context.resources.getColor(R.color.md_orange_500)
        }

        return context.resources.getColor(R.color.md_grey_850)
    }
}