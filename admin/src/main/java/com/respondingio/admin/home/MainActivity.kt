package com.respondingio.admin.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.respondingio.admin.R
import com.respondingio.admin.utils.Auth
import com.respondingio.admin.utils.Firestore
import com.respondingio.admin.viewmodels.MainActivityViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.find

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

        mDrawer = DrawerBuilder()
            .withActivity(this)
            .withToolbar(findViewById(R.id.toolbar))
            .withHeader(R.layout.drawer_home_header)
            .withHeaderDivider(false)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100).withName("Dashboard").withIcon(R.drawable.icons8_speedometer_96),
                PrimaryDrawerItem().withIdentifier(101).withName("Agencies").withIcon(R.drawable.ic_fire_station)
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    when (drawerItem.identifier.toInt()) {
                        100 -> {
                            supportFragmentManager.beginTransaction().replace(R.id.main_frame, DashboardFragment()).commit()
                        }
                        101 -> supportFragmentManager.beginTransaction().replace(R.id.main_frame, AgencyListFragment()).commit()
                    }
                    return false
                }
            })
            .build()

        supportFragmentManager.beginTransaction().replace(R.id.main_frame,
            DashboardFragment()
        ).commit()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val agencyUser = Firestore.getUser(Auth.getUser()!!)
            mDrawer.header?.findViewById<TextView>(R.id.header_name)?.text = agencyUser.name.toString()
            mDrawer.header?.findViewById<TextView>(R.id.header_email)?.text = agencyUser.email
        }
    }
}
