package com.respondingio.apparatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.respondingio.apparatus.views.ToggleButton

class StatusChangeDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateUI()
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_status_change, container, false)
        updateUI()
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_24dp)
        toolbar.setNavigationOnClickListener { dismiss() }

        val inServiceBTN = view.findViewById<ToggleButton>(R.id.inServiceBTN)
        val inStationBTN = view.findViewById<ToggleButton>(R.id.inStationBTN)
        val respondingBTN = view.findViewById<ToggleButton>(R.id.respondingBTN)
        val onSceneBTN = view.findViewById<ToggleButton>(R.id.onSceneBTN)
        val returningBTN = view.findViewById<ToggleButton>(R.id.returningBTN)

        inServiceBTN.checkedText = "IN SERVICE"
        inServiceBTN.checkedColor = R.color.md_green_700

        inStationBTN.checkedText = "IN STATION"
        inStationBTN.checkedColor = R.color.md_blue_700

        respondingBTN.checkedColor = R.color.md_red_700

        onSceneBTN.checkedColor = R.color.md_pink_700

        returningBTN.checkedColor = R.color.md_orange_700

        return view
    }


    override fun onStart() {
        super.onStart()
        updateUI()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    private fun updateUI() {
        val decorView = dialog?.window?.decorView
        decorView?.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }
}