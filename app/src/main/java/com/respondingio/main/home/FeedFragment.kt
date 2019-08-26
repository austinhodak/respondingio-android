package com.respondingio.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.respondingio.functions.agencies.AgencyUtils
import com.respondingio.functions.models.firestore.Agency
import com.respondingio.functions.models.realtime.User
import com.respondingio.main.R
import com.respondingio.main.RespondDialog
import com.respondingio.main.utils.Auth
import com.respondingio.main.viewmodels.MainActivityViewModel
import com.respondingio.main.viewmodels.RespondingListModel
import com.respondingio.main.views.PositionChip
import com.respondingio.main.views.RespondingChip
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.find
import androidx.recyclerview.widget.DividerItemDecoration


class FeedFragment : Fragment() {

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
    }

    var mAdapter: SlimAdapter? = null

    val agencies: MutableList<Agency> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter(view)

        mainViewModel.agencies.observe(this, Observer {
            for (agency in it) {
                FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agency.agencyID).addValueEventListener(object :
                    ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.hasChildren()) {
                            //No responding users
                            val index = agencies.indexOf(agency)
                            if (index != -1) {
                                agencies.remove(agency)
                                mAdapter?.notifyItemRemoved(index)
                            }
                            return
                        } else {
                            if (!agencies.contains(agency)) {
                                agencies.add(agency)
                                mAdapter?.notifyItemInserted(agencies.indexOf(agency))
                            }
                        }
                    }
                })
            }
        })
    }

    private fun setupAdapter(view: View) {
        val rv = view.find<RecyclerView>(R.id.feedRV)
        rv.layoutManager = LinearLayoutManager(requireContext())
        //rv.itemAnimator = FadeInAnimator()
        mAdapter = SlimAdapter.create().attachTo(rv).updateData(agencies).register(R.layout.responding_feed_agency_card) { agency: Agency, injector ->
            val agencyRV = injector.findViewById<RecyclerView>(R.id.agencyRV)
            if (agencyRV.layoutManager == null) agencyRV.layoutManager = LinearLayoutManager(requireContext())

            val marquee = injector.findViewById<TextView>(R.id.respondingUnavailable)
            marquee.isSelected = true
            marquee.requestFocus()

            val respondingUsers: MutableList<User> = ArrayList()
            val unavailableUsers: MutableList<User> = ArrayList()

            val agencyAdapter = SlimAdapter.create().attachTo(agencyRV).register(R.layout.item_responding_user) { user: User, injector ->
                if (respondingUsers.first() == user) {
                    injector.gone(R.id.div)
                } else {
                    injector.visible(R.id.div)
                }

                val positionList: LinearLayout = injector.findViewById(R.id.test)

                positionList.removeAllViews()

                //TODO Finish adding position stuff.
                val positionChip = PositionChip(activity!!)
                positionChip.setText("TESTING").setImageBackground(R.color.md_pink_700).setColor(R.color.md_pink_500)

                positionList.addView(positionChip)

                injector.text(R.id.respondingName, user.name.toString())

                val respondingChip = injector.findViewById<RespondingChip>(R.id.respchip)
                respondingChip.setRespondingStatus(user.responding?.respondingToType).setText(user.responding?.respondingTo?.toUpperCase() ?: "UNKNOWN").setTime(user.responding?.timestamp?: 0)
            }

            FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agency.agencyID).addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    respondingUsers.clear()
                    unavailableUsers.clear()

                    for (item in p0.children) {
                        val user = item.getValue(User::class.java)!!
                        user.userID = item.key

                        if (user.responding?.respondingToType?.toUpperCase() != "UNAVAILABLE") {
                            respondingUsers.add(user)
                        } else {
                            unavailableUsers.add(user)
                        }
                    }

                    respondingUsers.sortBy { it.name.toString() }
                    unavailableUsers.sortBy { it.name.toString() }

                    agencyAdapter.updateData(respondingUsers)

                    if (unavailableUsers.isNotEmpty()) {
                        marquee.text = "Unavailable: ${unavailableUsers.joinToString { it.name.toString() }}"
                        marquee.visibility = View.VISIBLE
                    } else {
                        marquee.visibility = View.GONE
                    }
                }
            })
        }


    }
}