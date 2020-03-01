package com.respondingio.main.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.functions.RespondingUtils
import com.respondingio.functions.models.firestore.Agency
import com.respondingio.functions.models.realtime.User
import com.respondingio.main.R
import com.respondingio.main.RespondDialog
import com.respondingio.main.utils.Auth
import com.respondingio.main.viewmodels.MainActivityViewModel
import com.respondingio.main.views.PositionChip
import com.respondingio.main.views.RespondingChip
import kotlinx.android.synthetic.main.fragment_feed.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.sendSMS
import org.jetbrains.anko.support.v4.toast

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

        mainViewModel.agencies.observe(viewLifecycleOwner, Observer {
            for (agency in it) {
                FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agency.agencyID).addValueEventListener(object :
                    ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        feedPG?.visibility = View.GONE
                        if (!p0.hasChildren()) {
                            if (agencies.isEmpty())
                            activity?.findViewById<TextView>(R.id.feedEmpty)?.visibility = View.VISIBLE
                            //No responding users
                            val index = agencies.indexOf(agency)
                            if (index != -1) {
                                agencies.remove(agency)
                                mAdapter?.notifyItemRemoved(index)
                            }
                            return
                        } else {
                            activity?.findViewById<TextView>(R.id.feedEmpty)?.visibility = View.GONE
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
            activity?.findViewById<TextView>(R.id.feedEmpty)?.visibility = View.GONE

            val agencyRV = injector.findViewById<RecyclerView>(R.id.agencyRV)
            if (agencyRV.layoutManager == null) agencyRV.layoutManager = LinearLayoutManager(requireContext())
            val chronometer = injector.findViewById<Chronometer>(R.id.alertText3)

            injector.text(R.id.alertText, agency.stationNumber)
            injector.text(R.id.alertText2, agency.agencyName?.toUpperCase())

            val marquee = injector.findViewById<TextView>(R.id.respondingUnavailable)
            marquee.isSelected = true
            marquee.requestFocus()

            val respondingUsers: MutableList<User> = ArrayList()
            val unavailableUsers: MutableList<User> = ArrayList()

            injector.clicked(R.id.alertLayout) {
                val myItems = listOf("Send Quick Alert", "Clear List")

                MaterialDialog(requireActivity()).show {
                    listItems(items = myItems) { dialog, index, text ->
                        when (index) {
                            1 -> {
                                RespondingUtils.clearAgencyRespondingUser(agencyID = agency.agencyID!!)
                            }
                        }
                    }
                }
            }

            //TODO Make user updates seamless and not cause layout to flicker.
            val agencyAdapter = SlimAdapter.create().updateData(respondingUsers).attachTo(agencyRV).register(R.layout.item_responding_user) { user: User, injector ->
                if (respondingUsers.first() == user) {
                    injector.gone(R.id.div)
                } else {
                    injector.visible(R.id.div)
                }

                val positionList: LinearLayout = injector.findViewById(R.id.test)

                positionList.removeAllViews()

                if (user.positions != null && user.positions?.get(agency.agencyID) != null) {
                    for (positionID in user.positions?.get(agency.agencyID)?.keys!!) {
                        FirebaseDatabase.getInstance("https://responding-io-agency.firebaseio.com/")
                            .getReference(agency.agencyID!!).child("positions").child(positionID)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    positionList.addView(
                                        PositionChip(activity!!).setType(p0.child("type").value?.toString()).setText(
                                            p0.child("name").value.toString()
                                        ).setColor(p0.child("color").value?.toString())
                                    )
                                }
                            })
                    }
                }

                injector.text(R.id.respondingName, user.name.toString())

                val respondingChip = injector.findViewById<RespondingChip>(R.id.respchip)
                if (user.responding?.onScene == true) {
                    respondingChip.onScene()
                        .setText("ON SCENE")
                        .setTime(user.responding?.onSceneTime ?: 0)
                } else {
                    respondingChip.setRespondingStatus(user.responding?.respondingToType)
                        .setText(user.responding?.respondingTo?.toUpperCase() ?: "UNKNOWN")
                        .setTime(user.responding?.timestamp ?: 0)
                }

                injector.clicked(R.id.top) {
                    if (user.userID == Auth.getUser()?.uid) {
                        val respondDialog = RespondDialog()
                        respondDialog.show(childFragmentManager.beginTransaction(), "RESPOND_DIALOG")
                    } else {
                        //TODO List options for selected user, call, text, etc.
                        val call = "Call"
                        val text = "Text"

                        val items = if (user.contactPhoneNumber == null) {
                            listOf("Send Orders")
                        } else {
                            listOf(call, text, "Send Orders")
                        }

                        MaterialDialog(requireActivity()).show {
                            title(text = user.name.toString())
                            listItems(items = items) { dialog, index, text ->
                                when (text) {
                                    call -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.contactPhoneNumber!!)))
                                    text -> sendSMS(user.contactPhoneNumber!!)
                                    "Send Orders" -> {

                                    }
                                }
                            }
                        }
                    }
                }
            }

            FirebaseDatabase.getInstance().getReference("users").orderByChild("responding/agency").equalTo(agency.agencyID).addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    //respondingUsers.clear()
                    unavailableUsers.clear()

                    p0.children.sortedBy { it.child("name").value?.toString() ?: "" }

                    for (item in p0.children) {
                        val user = item.getValue(User::class.java)!!
                        user.userID = item.key

                        val existing = respondingUsers.find { it.userID == item.key }
                        if (existing != null && user.responding?.respondingToType?.toUpperCase() != "UNAVAILABLE" && existing != user) {
                            val index = respondingUsers.indexOf(existing)
                            respondingUsers[index] = user
                            agencyAdapter.notifyItemChanged(index)
                        } else if (existing != null && existing == user) {

                        } else {
                            if (user.responding?.respondingToType?.toUpperCase() != "UNAVAILABLE") {
                                respondingUsers.add(user)
                                agencyAdapter.notifyItemInserted(respondingUsers.indexOf(user))
                            } else {
                                unavailableUsers.add(user)
                            }
                        }
                    }

                    try {
                        respondingUsers.forEach { u ->
                            if (p0.children.find { it.key == u.userID } == null) {
                                val index = respondingUsers.indexOf(u)
                                respondingUsers.remove(u)
                                agencyAdapter.notifyItemRemoved(index)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    //respondingUsers.sortBy { it.name.toString() }
                    unavailableUsers.sortBy { it.name.toString() }

                    //agencyAdapter.updateData(respondingUsers)

                    if (unavailableUsers.isNotEmpty()) {
                        marquee.text = "Unavailable: ${unavailableUsers.joinToString { it.name.toString() }}"
                        marquee.visibility = View.VISIBLE
                    } else {
                        marquee.visibility = View.GONE
                    }

                    val oldestRespondingUser: User? = respondingUsers.filterNot { it.responding?.timestamp == null }.minBy { it.responding?.timestamp ?: 0 }
                    if (oldestRespondingUser != null) {
                        chronometer.base =
                            SystemClock.elapsedRealtime() - (System.currentTimeMillis() - oldestRespondingUser.responding?.timestamp!!)
                        chronometer.format = "+ %s"
                        chronometer.start()
                    }
                }
            })
        }
    }
}