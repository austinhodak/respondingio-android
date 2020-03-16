package com.respondingio.admin.agency


import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
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
import com.respondingio.admin.R
import com.respondingio.admin.models.Position
import com.respondingio.admin.models.User
import com.respondingio.admin.utils.Firestore
import com.respondingio.functions.utils.Realtime
import kotlinx.android.synthetic.main.fragment_position_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class MembersListFragment : Fragment() {

    lateinit var mAdapter: SlimAdapter
    lateinit var agencyID: String
    var mData: MutableList<User> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_position_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        agencyID = (requireActivity() as AgencyDetailActivity).agencyID

        positionList?.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = SlimAdapter.create().attachTo(requireActivity().findViewById(R.id.positionList)).register(R.layout.item_position) { item: User, injector ->
            injector.text(R.id.positionName, item.name.toString())
            //injector.text(R.id.positionSubtitle, item.type)
            //val bar = injector.findViewById<View>(R.id.view3)
            //bar.setBackgroundColor(Color.parseColor(item.color))
            injector.clicked(R.id.codeTop) {
                startActivity<MemberDetailActivity>("userID" to item.UID, "agencyID" to agencyID)
            }

        }.updateData(mData)

        loadMembers()

        positionListAddFAB.onClick {

        }
    }

    private fun loadMembers() {
        if (!::agencyID.isInitialized) {
            toast("Agency ID null.")
            return
        }

        Firestore.i().collection("users").whereIn("agencies.${agencyID}", listOf(true, false)).get().addOnSuccessListener {
            mData.addAll(it.toObjects(User::class.java))

            mData.sortBy { it.name.toString() }
            mAdapter.updateData(mData)

        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}
