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
import java.text.SimpleDateFormat
import java.util.*

class RespondingChip constructor(context: Context, attr: AttributeSet? = null) : LinearLayout(context, attr) {

    val chipText: TextView by lazy { find<TextView>(R.id.responding_status) }
    val chipTime: TextView by lazy { find<TextView>(R.id.responding_time) }
    val chipTop: LinearLayout by lazy { find<LinearLayout>(R.id.responding_layout) }

    init {
        LayoutInflater.from(context).inflate(R.layout.responding_chip, this, true)
    }

    fun setText(string: String): RespondingChip {
        chipText.text = string
        return this
    }

    fun setTime(time: Long): RespondingChip {
        if (time == 0.toLong()) {
            chipTime.text = "NO TIME"
            return this
        }
        val sdf = SimpleDateFormat("HH:mm")
        val resultdate = Date(time)
        chipTime.text = sdf.format(resultdate)
        return this
    }

    fun setColor(hex: String): RespondingChip {
        chipTop.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hex))
        return this
    }

    fun setColor(@ColorRes int: Int): RespondingChip {
        chipTop.backgroundTintList = ColorStateList.valueOf(resources.getColor(int))
        return this
    }

    fun setImageBackground(@ColorRes int: Int): RespondingChip {
        chipTop.backgroundTintList = ColorStateList.valueOf(resources.getColor(int))
        return this
    }

    fun setImageBackground(hex: String): RespondingChip {
        chipTop.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hex))
        return this
    }

    fun setRespondingStatus(status: String?): RespondingChip {
        when (status.toString().toUpperCase()) {
            "STATION" -> {
                chipTop.setBackgroundResource(R.drawable.chip_orange_outline)
                chipText.setBackgroundResource(R.drawable.chip_orange_dark)
            }
            "SCENE" -> {
                chipTop.setBackgroundResource(R.drawable.chip_red_outline)
                chipText.setBackgroundResource(R.drawable.chip_red_dark)
            }
            "DELAYED" -> {
                chipTop.setBackgroundResource(R.drawable.chip_grey_outline)
                chipText.setBackgroundResource(R.drawable.chip_grey_dark)
            }
            else -> {
                chipTop.setBackgroundResource(R.drawable.chip_blue_outine)
                chipText.setBackgroundResource(R.drawable.chip_blue_dark)
            }
        }

        return this
    }

    fun onScene(): RespondingChip {
        chipTop.setBackgroundResource(R.drawable.chip_pink_outline)
        chipText.setBackgroundResource(R.drawable.chip_pink)
        return this
    }
}