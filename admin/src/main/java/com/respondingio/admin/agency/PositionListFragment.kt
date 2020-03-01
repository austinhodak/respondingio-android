package com.respondingio.admin.agency


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode.WRAP_CONTENT
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.respondingio.admin.R
import com.respondingio.admin.models.Position
import com.respondingio.functions.utils.Realtime
import kotlinx.android.synthetic.main.fragment_position_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

class PositionListFragment : Fragment() {

    lateinit var mAdapter: SlimAdapter
    lateinit var agencyID: String
    var mData: MutableList<Position> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_position_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        agencyID = (requireActivity() as AgencyDetailActivity).agencyID

        positionList?.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = SlimAdapter.create().attachTo(requireActivity().findViewById(R.id.positionList)).register(R.layout.item_position) { item: Position, injector ->
            injector.text(R.id.positionName, item.name)
            injector.text(R.id.positionSubtitle, item.type)
            val bar = injector.findViewById<View>(R.id.view3)
            bar.setBackgroundColor(Color.parseColor(item.color))
        }.updateData(mData)

        loadPositions()

        positionListAddFAB.onClick {
            var selectedType = ""
            var selectedTypeI = -1
            @ColorInt var selectedColor = -1

            val dialog = MaterialDialog(requireActivity(), BottomSheet(WRAP_CONTENT)).show {
                customView(R.layout.position_list_add_dialog)
                noAutoDismiss()
                positiveButton(text = "ADD") { dialog ->
                    val dialogLayout = dialog.getCustomView()
                    val positionAddName = dialogLayout.find<EditText>(R.id.positionAddName)

                    if (!positionAddName.text.isNullOrEmpty() && selectedType.isNotEmpty()) {
                        val pushKey = Realtime.agencyData().getReference("${agencyID}/positions").push().key
                        val position = Position(
                            name = positionAddName.text.toString(),
                            type = selectedType,
                            color = String.format("#%06X", 0xFFFFFF and selectedColor)
                        )

                        Realtime.agencyData().getReference("${agencyID}/positions/${pushKey}").setValue(position).addOnCompleteListener {
                            dialog.dismiss()
                        }
                    }
                }
                negativeButton(text = "CANCEL") { d -> d.dismiss() }
                cornerRadius(16f)
            }

            val dialogLayout = dialog.getCustomView()
            val positionTypeButton = dialogLayout.findViewById<Button>(R.id.positionTypeButton)
            val positionColorButton = dialogLayout.findViewById<Button>(R.id.positionColorButton)

            positionTypeButton.onClick { btn ->
                MaterialDialog(requireActivity()).show {
                    listItemsSingleChoice(R.array.position_types, initialSelection = selectedTypeI) { materialDialog: MaterialDialog, i: Int, charSequence: CharSequence ->
                        selectedTypeI = i
                        selectedType = charSequence.toString()
                        positionTypeButton.text = selectedType
                    }
                }
            }

            positionColorButton.onClick { btn ->
                MaterialDialog(requireActivity()).show {
                    title(text = "Pick Color")
                    colorChooser(ColorPalette.Primary, initialSelection = selectedColor) { dialog, color ->
                        selectedColor = color
                        positionColorButton.backgroundTintList = ColorStateList.valueOf(color)
                    }
                }
            }

        }
    }

    private fun loadPositions() {
        if (!::agencyID.isInitialized) {
            toast("Agency ID null.")
            return
        }

        Realtime.agencyData().getReference("${agencyID}/positions").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                mData.clear()

                if (!p0.exists()) {
                    toast("No positions found.")
                    return
                }

                for (child in p0.children) {
                    val position = child.getValue(Position::class.java)!!
                    mData.add(position)
                }

                mData.sortBy { it.name }
                mAdapter.updateData(mData)
            }
        })
    }
}
