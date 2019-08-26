package com.respondingio.apparatus.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.respondingio.apparatus.R
import com.respondingio.apparatus.models.Incident
import com.respondingio.apparatus.models.User
import com.respondingio.apparatus.models.UserDB
import com.respondingio.apparatus.viewmodels.MainActivityViewModel
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.find

class DashboardFragment : Fragment() {

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
    }

    private var space: SpaceNavigationView? = null
    val respondingUsers: MutableList<UserDB> = ArrayList()
    val rightRVData: MutableList<Any> = ArrayList()

    var mAdapter: SlimAdapter? = null
    var mRightAdapter: SlimAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setupAdapter(view)
        setupRightAdapter(view)
        return view
    }

    private fun setupRightAdapter(view: View) {
        val rv = view.find<RecyclerView>(R.id.dashboardRightRV)
        rv.layoutManager = LinearLayoutManager(requireContext())
        mRightAdapter = SlimAdapter.create().attachTo(rv).updateData(rightRVData).register(R.layout.dashboard_incident_item) { incident: Incident, injector ->
            if (incident.alert == null) {
                injector.gone(R.id.alertLayout)
            } else {
                injector.visible(R.id.alertLayout)
                val alertLayout = injector.findViewById<ConstraintLayout>(R.id.alertLayout)
                alertLayout.backgroundColorResource = when (incident.alert?.severity) {
                    "HIGH" -> {
                        R.color.md_red_A700
                    }
                    "MED" -> {
                        R.color.md_orange_A700
                    }
                    "LOW" -> {
                        R.color.md_blue_A400
                    }
                    else -> {
                        R.color.md_red_A400
                    }
                }

                injector.text(R.id.alertText, incident.alert?.text?.replace("\\n", "\n"))
            }

            injector.text(R.id.incidentTitle, incident.type?.name?.replace("\\n", "\n") ?: incident.type?.CADCode?.replace("\\n", "\n"))
            injector.findViewById<View>(R.id.incidentColor).setBackgroundColor(Color.parseColor(incident.type?.color ?: "#424242"))
            injector.text(R.id.incidentRemarks, incident.remarks?.joinToString(separator = "\n"))
            injector.text(R.id.incidentAddress, incident.address?.fromText)
            injector.text(R.id.incidentPriority, incident.type?.priority)


            if (incident.units != null) {
                injector.findViewById<FlexboxLayout>(R.id.unitsBox).removeAllViews()
                for (unit in incident.units!!) {
                    val textView = layoutInflater.inflate(
                        R.layout.unit_layout,
                        injector.findViewById<FlexboxLayout>(R.id.unitsBox),
                        false
                    ) as TextView
                    textView.text = unit.unitName
                    textView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(unit.unitColor ?: "#424242"))
                    injector.findViewById<FlexboxLayout>(R.id.unitsBox).addView(textView)
                }
            }
        }

        mainViewModel.activeIncidentData.observeForever {
            rightRVData.clear()
            rightRVData.addAll(it.activeIncidentsList)
            mRightAdapter?.notifyDataSetChanged()
        }
    }

    private fun setupAdapter(view: View) {
        val rv = view.find<RecyclerView>(R.id.dashboardRespondingRV)
        rv.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = SlimAdapter.create().attachTo(rv).updateData(respondingUsers).register(R.layout.item_responding_user) { user: UserDB, injector ->
            injector.text(R.id.respondingName, user.name.toString())
        }

        mainViewModel.respondingData.observeForever {
            mAdapter?.updateData(it.respondingList)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/respondingAgency").equalTo("NDV5qtDMjIHaSQszyKxU").addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                respondingUsers.clear()
//                for (child in p0.children) {
//                    val user = child.getValue(UserDB::class.java)
//                    respondingUsers.add(user!!)
//                }
//
//                respondingUsers.sortBy { it.name?.toString() }
//
//                mAdapter?.notifyDataSetChanged()
//            }
//        })

    }
}