package com.respondingio.admin.agency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.respondingio.admin.R
import com.respondingio.admin.home.AgencyListFragment
import com.respondingio.admin.home.DashboardFragment
import com.respondingio.admin.models.Agency
import com.respondingio.admin.utils.Firestore
import kotlinx.android.synthetic.main.activity_agency_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AgencyDetailActivity : AppCompatActivity() {

    lateinit var agencyID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agency_detail)

        setSupportActionBar(agencyDetailToolbar)

        processIntent(intent)

        setupDrawer()
    }

    private fun setupDrawer() {
        DrawerBuilder()
            .withActivity(this)
            .withToolbar(findViewById(R.id.agencyDetailToolbar))
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100).withName("Agency Dashboard").withIcon(R.drawable.icons8_speedometer_96),
                PrimaryDrawerItem().withIdentifier(101).withName("Positions"),
                PrimaryDrawerItem().withIdentifier(102).withName("Members")
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    when (drawerItem.identifier.toInt()) {
                        //100 -> supportFragmentManager.beginTransaction().replace(R.id.main_frame, DashboardFragment()).commit()
                        101 -> supportFragmentManager.beginTransaction().replace(R.id.agencyDetailFrame, PositionListFragment()).commit()
                        102 -> supportFragmentManager.beginTransaction().replace(R.id.agencyDetailFrame, MembersListFragment()).commit()
                    }
                    return false
                }
            })
            .build()
    }

    private fun processIntent(intent: Intent?) {
        val agencyID = intent?.getStringExtra("agencyID") ?: return
        this.agencyID = agencyID

        GlobalScope.launch {
            val agency = Firestore.getAgency(agencyID)
            Log.d("Agency", agency.toString())
            runOnUiThread {
                agencyLoaded(agency)
            }
        }
    }

    private fun agencyLoaded(agency: Agency) {
        title = agency.agencyName
    }
}
