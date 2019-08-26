package com.respondingio.apparatus.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.Button
import androidx.annotation.ColorRes
import com.respondingio.apparatus.R

class ToggleButton(context: Context, attributes: AttributeSet) : Button(context, attributes) {
    var isChecked = false
    var checkedText = text ?: ""
    var unCheckedText = text ?: ""

    @ColorRes var checkedColor: Int = R.color.md_blue_600
    @ColorRes var unCheckedColor: Int = R.color.md_grey_850

    fun setCheck(checked: Boolean) {
        isChecked = checked
         if (checked) {
             text = checkedText
             backgroundTintList = ColorStateList.valueOf(resources.getColor(checkedColor))
        } else {
             text = unCheckedText
             backgroundTintList = ColorStateList.valueOf(resources.getColor(unCheckedColor))
        }
    }

    private fun toggleCheck() {
        if (isChecked) {
            setCheck(false)
        } else {
            setCheck(true)
        }
    }



    init {
        setOnClickListener {
            toggleCheck()
        }
    }
}