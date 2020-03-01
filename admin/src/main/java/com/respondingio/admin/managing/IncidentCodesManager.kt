package com.respondingio.admin.managing

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.afollestad.materialdialogs.utils.MDUtil.isColorDark
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.respondingio.admin.R
import com.respondingio.admin.models.IncidentCode
import com.respondingio.admin.views.IncidentChip
import kotlinx.android.synthetic.main.dialog_incident_code.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor


class IncidentCodesManager : AppCompatActivity() {

    val mList: RecyclerView by lazy { find<RecyclerView>(R.id.codesRV) }
    val mFAB: FloatingActionButton by lazy { find<FloatingActionButton>(R.id.codesFAB) }
    lateinit var mAdapter: SlimAdapter
    val mData: MutableList<IncidentCode> = ArrayList()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_codes_manager)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_close_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        mList.layoutManager = LinearLayoutManager(this)
        mAdapter = SlimAdapter.create().attachTo(mList).register(R.layout.item_incident_code) { code: IncidentCode, injector ->
            injector.text(R.id.agencyName, code.userFacingName)
            if (code.matchExact == true) {
                injector.text(R.id.agencySubtitle, "(E) ${code.dispatchCode}")
            } else {
                injector.text(R.id.agencySubtitle, code.dispatchCode)
            }
            injector.text(R.id.codePriority, code.priority)
            val top = injector.findViewById<ConstraintLayout>(R.id.codeTop)
            top.setBackgroundColor(Color.parseColor(code.color))

            when (code.type?.toUpperCase()) {
                "FIRE" -> injector.image(R.id.codeImage, R.drawable.fire_1)
                "MEDICAL" -> injector.image(R.id.codeImage, R.drawable.star_of_life)
                "CAR ACCIDENT" -> injector.image(R.id.codeImage, R.drawable.mva_1)
                "WEATHER" -> injector.image(R.id.codeImage, R.drawable.icons8_cloud_lightning_96)
                "OTHER",
                "HAZARD" -> injector.image(R.id.codeImage, R.drawable.ic_report_problem_24dp)
                else -> injector.image(R.id.codeImage, null)
            }

            if (!Color.parseColor(code.color).isColorDark(0.25)) {
                injector.textColor(R.id.agencyName, Color.BLACK)
                injector.textColor(R.id.agencySubtitle, Color.BLACK)
                injector.textColor(R.id.codePriority, Color.BLACK)
                (injector.findViewById<ImageView>(R.id.codeImage)).setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)
            } else {
                injector.textColor(R.id.agencyName, Color.WHITE)
                injector.textColor(R.id.agencySubtitle, Color.WHITE)
                injector.textColor(R.id.codePriority, Color.WHITE)
                (injector.findViewById<ImageView>(R.id.codeImage)).setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            }

            injector.clicked(R.id.codeTop) {
                val dialog = IncidentCodeDialog()
                val bundle = Bundle()
                bundle.putSerializable("code", code)
                bundle.putString("codeKey", code.reference!!.path.toString())
                dialog.arguments = bundle
                val ft = supportFragmentManager.beginTransaction()
                dialog.show(ft, "CODE")
            }

            injector.longClicked(R.id.codeTop) {
                val myItems = listOf("Duplicate", "Delete")
                MaterialDialog(this).show {
                    listItems(items = myItems) { dialog, index, text ->
                        when (index) {
                            0 -> {
                                val dialog = IncidentCodeDialog()
                                val bundle = Bundle()
                                bundle.putSerializable("code", code)
                                bundle.putString("codeKey", FirebaseDatabase.getInstance().getReference("incidentCodes/NDV5qtDMjIHaSQszyKxU").push().path.toString())
                                dialog.arguments = bundle
                                val ft = supportFragmentManager.beginTransaction()
                                dialog.show(ft, "CODE")
                            }
                            1 -> {
                                code.reference?.removeValue()
                            }
                        }
                    }
                }
                false
            }
        }

        loadCodes("NDV5qtDMjIHaSQszyKxU")

        mFAB.setOnClickListener {
            Log.d("FAB", "CLICK")
            val dialog = IncidentCodeDialog()
            val ft = supportFragmentManager.beginTransaction()
            dialog.show(ft, "CODE")
        }
    }

    private fun loadCodes(agencyID: String) {
        FirebaseDatabase.getInstance().getReference("incidentCodes").child(agencyID).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                mData.clear()

                for (item in p0.children) {
                    if (item != null && item.exists()) {
                        val code = item.getValue(IncidentCode::class.java)!!
                        code.reference = item.ref
                        mData.add(code)
                    }
                }

                mData.sortBy { it.userFacingName }
                mAdapter.updateData(mData)
            }

        })
    }

    class IncidentCodeDialog : DialogFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(DialogFragment.STYLE_NORMAL, R.style.IncidentCodeDialog)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            super.onCreateView(inflater, container, savedInstanceState)
            val view = inflater.inflate(R.layout.dialog_incident_code, container, false)

            Log.d("DIALOG", "SHOW")

            val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
            toolbar.setNavigationIcon(R.drawable.ic_close_24dp)
            toolbar.setNavigationOnClickListener { dismiss() }

            return view
        }

        private var selectedColor: String = "#F44336"
        private var selectedType: String? = null
        private var selectedPriority: String? = "Auto"

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            (view.findViewById<IncidentChip>(R.id.editCodeChip)).setColor(R.color.md_grey_500).setImage(null)

            if (arguments != null) {
                fillFields(arguments!!.getString("codeKey"), arguments!!.getSerializable("code") as IncidentCode)
            }

            val PRIMARY_COLORS = intArrayOf(
                parseColor("#F44336"), parseColor("#E91E63"), parseColor("#9C27B0"),
                parseColor("#673AB7"), parseColor("#3F51B5"), parseColor("#2196F3"),
                parseColor("#03A9F4"), parseColor("#00BCD4"), parseColor("#009688"),
                parseColor("#4CAF50"), parseColor("#8BC34A"), parseColor("#CDDC39"),
                parseColor("#FFEB3B"), parseColor("#FFC107"), parseColor("#FF9800"),
                parseColor("#FF5722"), parseColor("#795548"), parseColor("#9E9E9E"),
                parseColor("#607D8B"), parseColor("#1b1b1b")
            )

            editCodeColor?.setOnClickListener {
                MaterialDialog(requireContext()).show {
                    title(text = "Pick Color")
                    colorChooser(PRIMARY_COLORS, initialSelection = parseColor(selectedColor)) { dialog, color ->
                        selectedColor = ("#" + Integer.toHexString(color).substring(2))
                        (it as Button).backgroundTintList = ColorStateList.valueOf(parseColor(("#" + Integer.toHexString(color).substring(2))))
                        (view.findViewById<IncidentChip>(R.id.editCodeChip)).setColor(("#" + Integer.toHexString(color).substring(2)))

                        if (!color.isColorDark(0.25)) {
                            it.textColor = Color.BLACK
                        } else {
                            it.textColor = Color.WHITE
                        }
                    }
                    positiveButton(text = "Select")
                }
            }

            editCodePriorityButton?.setOnClickListener {
                val myItems = listOf("HIGH", "MEDIUM", "LOW", "AUTO")

                MaterialDialog(requireContext()).show {
                    listItemsSingleChoice(items = myItems, initialSelection = myItems.indexOf(selectedPriority)) { dialog, index, text ->
                        selectedPriority = text.toString()
                        view.findViewById<Button>(R.id.editCodePriorityButton)?.text = text.toString().toUpperCase()
                    }
                }
            }

            editCodeTypeButton?.setOnClickListener {
                val myItems = listOf("FIRE", "MEDICAL", "CAR ACCIDENT", "WEATHER", "HAZARD", "OTHER")

                MaterialDialog(requireContext()).show {
                    listItemsSingleChoice(items = myItems, initialSelection = myItems.indexOf(selectedType)) { dialog, index, text ->
                        selectedType = text.toString()
                        view.findViewById<Button>(R.id.editCodeTypeButton)?.text = text.toString().toUpperCase()
                        (view.findViewById<IncidentChip>(R.id.editCodeChip)).updateType(text.toString())
                    }
                }
            }

            codeSaveFAB?.setOnClickListener {
                val key = if (arguments?.containsKey("codeKey") == true) {
                    FirebaseDatabase.getInstance().getReference(arguments?.getString("codeKey")!!)
                } else {
                    FirebaseDatabase.getInstance().getReference("incidentCodes").child("NDV5qtDMjIHaSQszyKxU").push()
                }

                if (editCodeUserText?.text?.isEmpty() == true) {
                    editCodeUserText?.text = editCodeDispatchCode.text
                }

                val code = IncidentCode(
                    matchExact = editCodeMatchExactSwitch?.isChecked,
                    color = selectedColor,
                    userFacingName = editCodeUserText?.text?.toString(),
                    dispatchCode = editCodeDispatchCode.text.toString(),
                    type = selectedType?.toUpperCase(),
                    priority = selectedPriority
                )
                key.setValue(code).addOnSuccessListener {
                    dismiss()
                }
            }
        }

        var codePath = ""

        private fun fillFields(codeID: String, code: IncidentCode) {
            codePath = codeID
            editCodeDispatchCode?.setText(code.dispatchCode!!, TextView.BufferType.EDITABLE)
            editCodeUserText?.setText(code.userFacingName!!, TextView.BufferType.EDITABLE)

            selectedColor = code.color!!
            (editCodeColor as Button).backgroundTintList = ColorStateList.valueOf(parseColor(code.color!!))
            (view?.findViewById<IncidentChip>(R.id.editCodeChip))?.setColor(code.color!!)

            if (!Color.parseColor(code.color).isColorDark(0.25)) {
                editCodeColor.textColor = Color.BLACK
            } else {
                editCodeColor.textColor = Color.WHITE
            }

            selectedType = code.type
            view?.findViewById<Button>(R.id.editCodeTypeButton)?.text = code.type!!.toUpperCase()
            (view?.findViewById<IncidentChip>(R.id.editCodeChip))?.updateType(code.type!!)

            selectedPriority = code.priority
            view?.findViewById<Button>(R.id.editCodePriorityButton)?.text = code.priority!!.toUpperCase()
        }

        override fun onStart() {
            super.onStart()
            if (dialog != null) {
                dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }

        fun parseColor(string: String): Int {
            return Color.parseColor(string)
        }
    }


}

fun Int.isColorDark(threshold: Double = 0.5): Boolean {
    if (this == Color.TRANSPARENT) {
        return false
    }
    val darkness =
        1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= threshold
}
