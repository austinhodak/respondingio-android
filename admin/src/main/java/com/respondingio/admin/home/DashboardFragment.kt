package com.respondingio.admin.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.respondingio.admin.R

class DashboardFragment : Fragment() {

    private var space: SpaceNavigationView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

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
    }
}