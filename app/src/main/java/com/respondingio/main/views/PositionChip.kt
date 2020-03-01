package com.respondingio.main.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.respondingio.main.R
import com.respondingio.main.managing.isColorDark
import dev.jorgecastillo.androidcolorx.library.darken
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor

class PositionChip constructor(context: Context, attr: AttributeSet? = null) : LinearLayout(context, attr) {

    val chipText: TextView by lazy { find<TextView>(R.id.chip_text) }
    val chipImage: ImageView by lazy { find<ImageView>(R.id.chip_image) }
    val chipTop: LinearLayout by lazy { find<LinearLayout>(R.id.chip_top) }

    init {
        LayoutInflater.from(context).inflate(R.layout.responding_position_chip, this, true)
    }

    fun setText(string: String): PositionChip {
        chipText.text = string
        return this
    }

    fun setType(string: String?): PositionChip {
        if (string.isNullOrEmpty()) return this

        when (string.toUpperCase()) {
            "FIRE" -> setImage(R.drawable.fire_1)
            "EMS" -> setImage(R.drawable.star_of_life)
            "POLICE" -> setImage(R.drawable.icons8_police_badge)
            "OFFICER" -> setImage(R.drawable.icons8_firefighter)
            "ADMINISTRATIVE" -> setImage(R.drawable.icons8_businessman)
            "JUNIOR" -> setImage(R.drawable.icons8_babys_room)
            "SUPPORT" -> setImage(R.drawable.icons8_fire_hydrant)
            "DRIVER" -> setImage(R.drawable.icons8_steering_wheel_100)
            else -> setImage(R.drawable.icons8_fire_hydrant)
        }

        return this
    }

    fun setColor(hex: String?): PositionChip {
        val color = Color.parseColor(hex ?: "#F44336")
        val drawable: GradientDrawable = chipTop.background as GradientDrawable
        drawable.setColor(color)
        setImageBackground(color.darken(10))
        return this
    }

    fun setColor(@ColorRes int: Int): PositionChip {
        val drawable: GradientDrawable = chipTop.background as GradientDrawable
        drawable.setColor(resources.getColor(int))
        //chipTop.backgroundTintList = ColorStateList.valueOf(resources.getColor(int))
        return this
    }

    fun setImage(@DrawableRes int: Int?): PositionChip {
        if (int == null) {
            chipImage.setImageDrawable(null)
            chipImage.visibility = View.GONE
            return this
        }
        chipImage.setImageResource(int)
        chipImage.visibility = View.VISIBLE
        return this
    }

    fun setImageBackground(@ColorInt int: Int): PositionChip {
        chipImage.backgroundTintList = ColorStateList.valueOf(int)
        val drawable: GradientDrawable = chipTop.background as GradientDrawable
        drawable.setStroke(3, int)
        return this
    }

    fun setImageBackground(hex: String): PositionChip {
        chipImage.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hex))
        return this
    }
}