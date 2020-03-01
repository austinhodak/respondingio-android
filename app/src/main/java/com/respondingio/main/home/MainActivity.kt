package com.respondingio.main.home

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.respondingio.functions.agencies.AgencyUtils
import com.respondingio.functions.models.firestore.Incident
import com.respondingio.main.R
import com.respondingio.main.managing.IncidentCodesManager
import com.respondingio.main.settings.SettingsActivity
import com.respondingio.main.utils.Auth
import com.respondingio.main.utils.Firestore
import com.respondingio.main.viewmodels.MainActivityViewModel
import com.respondingio.main.views.IncidentChip
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.configuration
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(MainActivityViewModel::class.java)
    }

    private lateinit var mDrawer: Drawer

    private val mIncidentRV: RecyclerView by lazy { find<RecyclerView>(R.id.mainHorzIncidentList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

       mDrawer = DrawerBuilder()
           .withActivity(this)
           .withToolbar(findViewById(R.id.toolbar_settings))
           .withHeader(R.layout.drawer_home_header)
           .withHeaderDivider(false)
           .addDrawerItems(
               PrimaryDrawerItem().withIdentifier(100).withName("Dashboard").withIcon(R.drawable.icons8_speedometer_96)
               /*ExpandableDrawerItem().withName("Apparatus").withSelectable(false).withIcon(R.drawable.icons8_fire_truck).withSubItems(
                   ExpandableDrawerItem().withName("Garland 65").withSelectable(false).withSubItems(
                       SecondaryDrawerItem().withName("Engine 651"),
                       SecondaryDrawerItem().withName("Tanker 654"),
                       SecondaryDrawerItem().withName("Utility 658")
                   )
               )*/
           )
           .addStickyDrawerItems(
               SecondarySwitchDrawerItem().withIcon(R.drawable.dark_mode_icon).withSelectable(false).withIdentifier(200).withOnCheckedChangeListener(object : OnCheckedChangeListener {
                   override fun onCheckedChanged(drawerItem: IDrawerItem<*>, buttonView: CompoundButton, isChecked: Boolean) {
                       if (isChecked) {
                           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                       } else {
                           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                       }
                   }
               }).withName("Dark Mode").withChecked(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES),
               SecondaryDrawerItem().withName("Settings").withIdentifier(201).withSelectable(false).withEnabled(true).withIcon(R.drawable.icons8_settings)
           )
           .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
               override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                   when (drawerItem.identifier.toInt()) {
                       100 -> {
                           supportFragmentManager.beginTransaction().replace(R.id.main_frame, DashboardFragment()).commit()
                       }
                       201 -> {
                           startActivity<SettingsActivity>()
                       }
                   }
                   return false
               }
           })
           .build()

        supportFragmentManager.beginTransaction().replace(R.id.main_frame, DashboardFragment()).commit()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val agencyUser = Firestore.getUser(Auth.getUser()!!)
            mDrawer.header?.findViewById<TextView>(R.id.header_name)?.text = agencyUser.name.toString()
            mDrawer.header?.findViewById<TextView>(R.id.header_email)?.text = agencyUser.email
        }
    }
}
