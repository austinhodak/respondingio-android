package com.respondingio.admin.home

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
import com.mikepenz.materialdrawer.Drawer
import com.respondingio.admin.R
import com.respondingio.admin.managing.IncidentCodesManager
import com.respondingio.admin.models.Incident
import com.respondingio.admin.utils.Auth
import com.respondingio.admin.utils.Firestore
import com.respondingio.admin.viewmodels.MainActivityViewModel
import com.respondingio.admin.views.IncidentChip
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

        setupDrawer()
    }

    private fun setupDrawer() {
        mDrawer = drawer {
            toolbar = findViewById(R.id.toolbar)
            headerViewRes = R.layout.drawer_home_header
            headerDivider = false
            primaryItem(name = "Dashboard") {
                icon = R.drawable.icons8_speedometer_96
                onClick { _ ->
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame,
                        DashboardFragment()
                    ).commit()
                    return@onClick false
                }
            }
            primaryItem(name = "Agencies") {
                icon = R.drawable.ic_fire_station
                onClick { _ ->
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame,
                        AgencyListFragment()
                    ).commit()
                    return@onClick false
                }
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.main_frame,
            DashboardFragment()
        ).commit()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val agencyUser = Firestore.getUser(Auth.getUser()!!)
            mDrawer.header.findViewById<TextView>(R.id.header_name).text = agencyUser.name.toString()
            mDrawer.header.findViewById<TextView>(R.id.header_email).text = agencyUser.email
        }
    }
}
