package com.respondingio.main.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.respondingio.main.R
import com.respondingio.main.managing.isColorDark
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

    fun setColor(hex: String): PositionChip {
        chipTop.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hex))
        return this
    }

    fun setColor(@ColorRes int: Int): PositionChip {
        chipTop.backgroundTintList = ColorStateList.valueOf(resources.getColor(int))
        return this
    }

    fun setImage(@DrawableRes int: Int?) {
        if (int == null) {
            chipImage.setImageDrawable(null)
            chipImage.visibility = View.GONE
            return
        }
        chipImage.setImageResource(int)
        chipImage.visibility = View.VISIBLE
    }

    fun setImageBackground(@ColorRes int: Int): PositionChip {
        chipImage.backgroundTintList = ColorStateList.valueOf(resources.getColor(int))
        return this
    }

    fun setImageBackground(hex: String): PositionChip {
        chipImage.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hex))
        return this
    }
}