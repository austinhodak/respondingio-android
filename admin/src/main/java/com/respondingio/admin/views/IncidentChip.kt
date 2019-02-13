package com.respondingio.admin.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.respondingio.admin.R
import com.respondingio.admin.managing.isColorDark
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor

@Suppress("FunctionName")
class IncidentChip constructor(context: Context, attr: AttributeSet? = null) : ConstraintLayout(context, attr) {

    val incidentText: TextView by lazy { find<TextView>(R.id.incidentText) }
    val incidentImage: ImageView by lazy { find<ImageView>(R.id.incidentImage) }
    val incidentTop: ConstraintLayout by lazy { find<ConstraintLayout>(R.id.incidentTop) }

    init {
        LayoutInflater.from(context).inflate(R.layout.incident_chip, this, true)
    }

    fun setText(string: String) {
        incidentText.text = string
    }

    fun setColor(hex: String): IncidentChip {
        incidentTop.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hex))

        if (!Color.parseColor(hex).isColorDark(0.25)) {
            incidentText.textColor = Color.BLACK
            incidentImage.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        } else {
            incidentText.textColor = Color.WHITE
            incidentImage.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        }

        return this
    }

    fun setColor(@ColorRes int: Int): IncidentChip {
        incidentTop.backgroundTintList = ColorStateList.valueOf(resources.getColor(int))

        if (!int.isColorDark(0.25)) {
            incidentText.textColor = Color.BLACK
            incidentImage.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
        } else {
            incidentText.textColor = Color.WHITE
            incidentImage.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        }

        return this
    }

    fun setImage(@DrawableRes int: Int?) {
        if (int == null) {
            incidentImage.setImageDrawable(null)
            incidentImage.visibility = View.GONE
            return
        }
        incidentImage.setImageResource(int)
        incidentImage.visibility = View.VISIBLE
    }

    fun EMS(): IncidentChip {
        setColor(R.color.md_blue_800)
        setImage(R.drawable.star_of_life)
        return this
    }

    fun FIRE(): IncidentChip {
        setColor(R.color.md_red_500)
        setImage(R.drawable.fire_1)
        return this
    }

    fun MVA(): IncidentChip {
        setColor(R.color.md_orange_600)
        setImage(R.drawable.mva_1)
        return this
    }

    fun updateType(type: String) {
        when (type.toUpperCase()) {
            "FIRE" -> setImage(R.drawable.fire_1)
            "MEDICAL" -> setImage(R.drawable.star_of_life)
            "CAR ACCIDENT" -> setImage(R.drawable.mva_1)
            "WEATHER" -> setImage(R.drawable.icons8_cloud_lightning_96)
            "OTHER",
            "HAZARD" -> setImage(R.drawable.ic_report_problem_24dp)
            else -> setImage(null)
        }
    }
}