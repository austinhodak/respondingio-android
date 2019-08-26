package com.respondingio.apparatus.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.respondingio.apparatus.R
import com.respondingio.apparatus.StatusChangeDialog
import com.respondingio.apparatus.drawer.CustomPrimaryDrawerItem
import com.respondingio.apparatus.drawer.OverflowMenuDrawerItem
import com.respondingio.apparatus.models.Apparatus
import com.respondingio.apparatus.models.ApparatusStatus
import com.respondingio.apparatus.onboarding.ApparatusPicker
import com.respondingio.apparatus.viewmodels.MainActivityViewModel
import com.respondingio.functions.apparatus.ApparatusUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(MainActivityViewModel::class.java)
    }

    private lateinit var mDrawer: Drawer

    private val mIncidentRV: RecyclerView by lazy { find<RecyclerView>(R.id.mainHorzIncidentList) }

    private val unitMarkOnSceneItem = OverflowMenuDrawerItem().withIdentifier(101).withSelectable(false).withName("UNIT ON SCENE").withMenu(R.menu.on_scene_options).withBackgroundRes(R.color.timelineRed)
    private val unitMarkReturning = OverflowMenuDrawerItem().withIdentifier(102).withSelectable(false).withName("UNIT RETURNING").withMenu(R.menu.on_scene_options).withBackgroundRes(R.color.md_pink_500)
    private val unitMarkInStation = OverflowMenuDrawerItem().withIdentifier(103).withSelectable(false).withName("UNIT IN STATION").withMenu(R.menu.on_scene_options).withBackgroundRes(R.color.timelineGreen)

    private var apparatus: Apparatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateUI()

        setContentView(R.layout.activity_main)

        setupDrawer()
        setupRightDrawer()

        homeStatusChip?.setOnClickListener {
            /*val dialog = StatusChangeDialog()
            dialog.show(supportFragmentManager.beginTransaction(), "STATUS")*/
            val myItems = listOf("In Service • In Station", "In Service • Out of Station", "Out of Service • In Station", "Out of Service • Out of Station", "Responding", "On Scene", "Returning")

            val dialog = MaterialDialog(this).listItemsSingleChoice(items = myItems, initialSelection = myItems.indexOf(apparatus?.status?.statusString())) { dialog, index, text ->
                FirebaseFirestore.getInstance().collection("apparatus").document(FirebaseAuth.getInstance().currentUser!!.uid).update("status", getStatusObject(text))
            }

            updateUI(dialog)
        }

        mainViewModel.apparatusData.observeForever {
            apparatus = it

            if (apparatus!!.agencyID != null) {
                mainViewModel.loadResponding(apparatus!!.agencyID!!)
                mainViewModel.getActiveIncidents(apparatus!!.agencyID!!)
            }

            homeStatusText?.text = it.status?.statusString()?.toUpperCase()
            homeStatusChip?.backgroundTintList = ColorStateList.valueOf(it.status!!.getColor(this))
            Log.d("APPARATUS", it.status.toString())

            if (it.status?.responding == true) {
                mDrawer2?.addStickyFooterItem(unitMarkOnSceneItem)
            } else {
                if (mDrawer2!!.getStickyFooterPosition(101) != -1)
                mDrawer2?.removeStickyFooterItemAtPosition(mDrawer2!!.getStickyFooterPosition(101))
            }

            if (it.status?.onscene == true) {
                mDrawer2?.addStickyFooterItem(unitMarkReturning)
            } else {
                if (mDrawer2!!.getStickyFooterPosition(102) != -1)
                    mDrawer2?.removeStickyFooterItemAtPosition(mDrawer2!!.getStickyFooterPosition(102))
            }

            if (it.status?.returning == true) {
                mDrawer2?.addStickyFooterItem(unitMarkInStation)
            } else {
                if (mDrawer2!!.getStickyFooterPosition(103) != -1)
                    mDrawer2?.removeStickyFooterItemAtPosition(mDrawer2!!.getStickyFooterPosition(103))
            }
        }
        mainViewModel.loadApparatus(FirebaseAuth.getInstance().currentUser!!.uid)

        GlobalScope.launch {
            val apparatusList = ApparatusUtils.getAllAgencyApparatus("NDV5qtDMjIHaSQszyKxU")
            Log.d("LIST", apparatusList.toString())
        }

    }

    private fun getStatusObject(text: String): Any? {
        when (text) {
            "Returning" -> {
                return ApparatusStatus(returning = true)
            }
            "On Scene" -> {
                return ApparatusStatus(onscene = true)
            }
            "Responding" -> {
                return ApparatusStatus(responding = true)
            }
            "Returning" -> {
                return ApparatusStatus(returning = true)
            }
            "Out of Service • Out of Station" -> {
                return ApparatusStatus(inService = false, inStation = false)
            }
            "Out of Service • In Station" -> {
                return ApparatusStatus(inService = false, inStation = true)
            }
            "In Service • Out of Station" -> {
                return ApparatusStatus(inService = true, inStation = false)
            }
            "In Service • In Station" -> {
                return ApparatusStatus(inService = true, inStation = true)
            }
        }

        return ApparatusStatus()
    }

    private var mDrawer2: Drawer? = null

    private fun setupRightDrawer() {
        mDrawer2 = DrawerBuilder()
            .withActivity(this)
            .withHeaderPadding(false)
            .withHeaderDivider(false)
            .withSelectedItem(-1)
            .withTranslucentStatusBar(false)
            .addDrawerItems(
                ExpandableDrawerItem().withIcon(R.drawable.fire_truck_color).withIdentifier(1001).withSelectable(false).withName("Apparatus").withSubItems(
                    SecondaryDrawerItem().withSelectable(false).withName("RESCUE 652").withBadge("ON SCENE").withBadgeStyle(BadgeStyle().withPaddingLeftRightDp(8).withBadgeBackground(resources.getDrawable(R.drawable.chip_red)).withTextColorRes(R.color.md_white_1000)),
                    SecondaryDrawerItem().withSelectable(false).withName("TANKER 654").withBadge("RESPONDING").withBadgeStyle(BadgeStyle().withPaddingLeftRightDp(8).withBadgeBackground(resources.getDrawable(R.drawable.chip_red)).withTextColorRes(R.color.md_white_1000)),
                    SecondaryDrawerItem().withSelectable(false).withName("UTILITY 658")
                ),
                DividerDrawerItem(),
                ExpandableBadgeDrawerItem().withSelectable(false).withName("Pending Incidents").withBadge("2").withSubItems(
                    SecondaryDrawerItem().withSelectable(false).withName("UTILITY 658")
                )
                //CustomPrimaryDrawerItem().withIdentifier(11).withSelectable(false).withName("PENDING INCIDENTS").withBadge("2")
            ).addStickyDrawerItems(
                //unitOnSceneItem
            ).buildView()

        mDrawer2!!.recyclerView.setPadding(mDrawer2!!.recyclerView.leftPadding, 8, mDrawer2!!.recyclerView.rightPadding, mDrawer2!!.recyclerView.bottomPadding)
        mDrawer2!!.expandableExtension.expand(0)

        find<FrameLayout>(R.id.drawerTest).addView(mDrawer2!!.slider)
        //(mDrawer2.getDrawerItem(11) as CustomPrimaryDrawerItem).flashItem(mDrawer2, android.R.color.transparent, R.color.md_red_500, 400)
    }

    private fun flashItem(mDrawer: Drawer?, drawerItem: IDrawerItem<*, *>?, @ColorRes firstColor: Int, @ColorRes secondColor: Int, waitDuration: Long) {
        if (waitDuration < 375) {
            throw IllegalArgumentException("Values lower than 375 will cause flickering.")
        }
        if (drawerItem == null) return
        val newItem = drawerItem as CustomPrimaryDrawerItem

        val runnableCode = object: Runnable {
            override fun run() {
                if (newItem.background!!.colorRes ==firstColor) {
                    newItem.withBackgroundRes(secondColor)
                } else {
                    newItem.withBackgroundRes(firstColor)
                }

                mDrawer?.updateItem(newItem)

                Handler().postDelayed(this, waitDuration)
            }
        }

        runnableCode.run()
    }

    private fun setupDrawer() {
        mDrawer = drawer {
            toolbar = findViewById(R.id.toolbar)
            //headerViewRes = R.layout.drawer_home_header
            //headerDivider = false
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
            primaryItem(name = "Apparatus Picker") {
                icon = R.drawable.ic_fire_station
                onClick { _ ->
                    startActivity<ApparatusPicker>("agencyID" to "LyDDUKhjYo2rpqpw0Vld")
                    return@onClick false
                }
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.main_frame,
            DashboardFragment()
        ).commit()

        /*GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val agencyUser = Firestore.getUser(Auth.getUser()!!)
            mDrawer.header.findViewById<TextView>(R.id.header_name).text = agencyUser.name.toString()
            mDrawer.header.findViewById<TextView>(R.id.header_email).text = agencyUser.email
        }*/

        mDrawer.drawerLayout.fitsSystemWindows = false

    }

    private fun updateUI() {
        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        updateUI()
        if (FirebaseAuth.getInstance().currentUser != null) {
            mainToolbarText?.text = FirebaseAuth.getInstance().currentUser!!.displayName?.toUpperCase()
        }
    }

    private fun updateUI(materialDialog: MaterialDialog) {
        materialDialog.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        materialDialog.show()

        materialDialog.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        materialDialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

    }
}
