package com.respondingio.main.home

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.luseen.spacenavigation.SpaceOnLongClickListener
import com.respondingio.functions.RespondingUtils
import com.respondingio.functions.models.realtime.User
import com.respondingio.main.R
import com.respondingio.main.RespondDialog
import com.respondingio.main.utils.Auth
import com.respondingio.main.viewmodels.MainActivityViewModel

class DashboardFragment : Fragment() {

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
    }

    private var space: SpaceNavigationView? = null
    private var currentUser: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_fast)
        transaction.replace(R.id.dashboard_frame, FeedFragment())
        transaction.commit()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        space = view.findViewById(R.id.dashboard_nav)

        space?.initWithSaveInstanceState(savedInstanceState)
        space?.addSpaceItem(SpaceItem("", R.drawable.feed_1))
        space?.addSpaceItem(SpaceItem("", R.drawable.icons8_fire_alarm_100))
        space?.addSpaceItem(SpaceItem("", R.drawable.fire_truck_1))
        space?.addSpaceItem(SpaceItem("", R.drawable.icons8_more_100))
        space?.setCentreButtonIconColorFilterEnabled(false)
        space?.shouldShowFullBadgeText(false)
        space?.showIconOnly()

        mainViewModel.userData.observe(this, Observer {
            currentUser = it
            Log.d("USER", it.toString())
            Handler().postDelayed({
                /*if (it.isUserResponding()) {
                    if (it.responding?.respondingToType == "UNAVAILABLE") {
                        space?.changeCenterButtonIcon(R.drawable.icons8_steering_wheel_100)
                    } else {
                        space?.changeCenterButtonIcon(R.drawable.icons8_place_marker_96)
                    }
                } else {
                    space?.changeCenterButtonIcon(R.drawable.icons8_steering_wheel_100)
                }*/

                space?.changeCenterButtonIcon(R.drawable.icons8_steering_wheel_100)
            }, 10)
        })
        mainViewModel.getRealtimeUserData(FirebaseAuth.getInstance().currentUser!!.uid)

        space?.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                val respondDialog = RespondDialog()
                respondDialog.show(childFragmentManager.beginTransaction(), "RESPOND_DIALOG")

                /*if (!currentUser!!.isUserResponding() || currentUser!!.responding?.respondingToType == "UNAVAILABLE") {
                    val respondDialog = RespondDialog()
                    respondDialog.show(childFragmentManager.beginTransaction(), "RESPOND_DIALOG")
                } else {
                    //User is on scene
                    RespondingUtils.markUserOnScene(Auth.getUser()?.uid.toString())
                }*/
            }

            override fun onItemReselected(itemIndex: Int, itemName: String?) {

            }

            override fun onItemClick(itemIndex: Int, itemName: String?) {
                val transaction = childFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_fast)
                transaction.replace(R.id.dashboard_frame, when (itemIndex) {
                    0 -> FeedFragment()
                    1 -> IncidentList()
                    else -> FeedFragment()
                })
                transaction.commit()
            }
        })

        space?.setSpaceOnLongClickListener(object : SpaceOnLongClickListener {
            override fun onCentreButtonLongClick() {
                if (currentUser!!.isUserResponding()) {
                    val respondDialog = RespondDialog()
                    respondDialog.show(childFragmentManager.beginTransaction(), "RESPOND_DIALOG")
                }
            }

            override fun onItemLongClick(itemIndex: Int, itemName: String?) {

            }
        })


    }
}