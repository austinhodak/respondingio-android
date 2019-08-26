package com.respondingio.main.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.Drawer
import com.respondingio.functions.agencies.AgencyUtils
import com.respondingio.functions.models.firestore.Incident
import com.respondingio.main.R
import com.respondingio.main.managing.IncidentCodesManager
import com.respondingio.main.utils.Auth
import com.respondingio.main.utils.Firestore
import com.respondingio.main.viewmodels.MainActivityViewModel
import com.respondingio.main.views.IncidentChip
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(MainActivityViewModel::class.java)
    }

    private lateinit var mDrawer: Drawer

    private val mIncidentRV: RecyclerView by lazy { find<RecyclerView>(R.id.mainHorzIncidentList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AgencyUtils.loadUserAgencies(FirebaseAuth.getInstance().uid!!)

        setupDrawer()

        mIncidentRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val mAdapter = SlimAdapter.create().attachTo(mIncidentRV).register(R.layout.incident_chip_item) { incident: Incident, injector ->
            val item = injector.findViewById<IncidentChip>(R.id.top)
            item.setText(incident.address!!.fromText!!)
            item.setColor(incident.type!!.color!!)
            item.EMS()
            //item.EMS()
        }
        mainViewModel.activeIncidentData.observe(this, Observer {
            if (it.activeIncidentsList.isNotEmpty()) {
                mIncidentRV.visibility = View.GONE
            } else {
                mIncidentRV.visibility = View.GONE
            }

            mAdapter.updateData(it.activeIncidentsList)
        })
        mainViewModel.getActiveIncidents()
        mainViewModel.loadUserAgencies(Auth.getUser()!!.uid)

        GlobalScope.launch {
            //mainViewModel.loadResponding(Firestore.getUser(Auth.getUser()!!).getPrimaryAgencyID()!!)
        }
    }

    private fun setupDrawer() {
        mDrawer = drawer {
            toolbar = findViewById(R.id.toolbar)
            headerViewRes = R.layout.drawer_home_header
            headerDivider = false
            primaryItem(name = "Dashboard") {
                icon = R.drawable.icons8_speedometer_96
                onClick { _ ->
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame, DashboardFragment()).commit()
                    return@onClick false
                }
            }
            primaryItem(name = "Manage Codes") {
                onClick { _ ->
                    startActivity<IncidentCodesManager>()
                    return@onClick true
                }
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.main_frame, DashboardFragment()).commit()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val agencyUser = Firestore.getUser(Auth.getUser()!!)
            mDrawer.header.findViewById<TextView>(R.id.header_name).text = agencyUser.name.toString()
            mDrawer.header.findViewById<TextView>(R.id.header_email).text = agencyUser.email
        }
    }
}
