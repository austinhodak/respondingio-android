package com.respondingio.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.respondingio.functions.RespondingUtils
import com.respondingio.functions.agencies.AgencyUtils
import com.respondingio.functions.models.firestore.Agency
import com.respondingio.main.utils.Auth
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.toast
import android.R.id
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.respond_bottom_sheet.*
import org.jetbrains.anko.support.v4.act


class RespondDialog : BottomSheetDialogFragment() {

    private var agencyRadioGroup: RadioGroup? = null
    private var respondingToGroup: RadioGroup? = null

    private var selectedAgency: Agency? = null
    private var selectedTo: String? = null

    private var selectedOption: Agency.ResponseOption? = null
    private var responseOptions: MutableMap<Int, Agency.ResponseOption> = HashMap()

    private var agencies: MutableMap<Int, Agency> = HashMap()

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.respond_bottom_sheet, null)
        dialog.setContentView(contentView)
        contentView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        agencyRadioGroup = contentView.find(R.id.agency_group)
        respondingToGroup = contentView.find(R.id.responding_layout)

        agencyRadioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            if (selectedAgency != agencies[checkedId]) {
                respondingToGroup!!.removeAllViews()
            }

            responseOptions.clear()

            selectedAgency = agencies[checkedId]

            for (option in selectedAgency?.responseOptions!!) {
                val radioButton = getRadioButton(option.name!!)
                respondingToGroup!!.addView(radioButton)
                responseOptions[radioButton.id] = option
            }

            if (selectedTo != null) {
                respond()
            }
        }

        respondingToGroup!!.setOnCheckedChangeListener { group, checkedId ->
            selectedTo = (contentView.find<RadioButton>(checkedId)).text.toString().toUpperCase()
            selectedOption = responseOptions[checkedId]

            if (selectedAgency != null) {
                respond()
            }
        }

        for (agency in AgencyUtils.agencies) {
            val radioButton = getRadioButton(agency.shortName!!)
            agencyRadioGroup!!.addView(radioButton)

            agencies[radioButton.id] = agency

            if (AgencyUtils.agencies.size == 1)
            radioButton.isChecked = true
        }

        contentView?.find<ImageView>(R.id.close_button)?.setOnClickListener {
            dismiss()
        }
    }

    private fun respond() {
        RespondingUtils.markUserResponding(Auth.getUser()!!.uid, selectedTo!!, selectedAgency!!.agencyID!!, selectedOption!!).addOnSuccessListener {
            dismiss()
        }
    }

    private fun getRadioButton(text: String) : RadioButton {
        val r = activity!!.resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            r.displayMetrics
        ).toInt()

        val radioButton = RadioButton(activity)
        radioButton.text = text
        radioButton.layoutDirection = View.LAYOUT_DIRECTION_RTL
        val layoutParams =
            RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 0, px)
        radioButton.layoutParams = layoutParams
        return radioButton
    }
}