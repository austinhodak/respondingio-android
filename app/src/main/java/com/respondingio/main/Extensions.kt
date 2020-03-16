package com.respondingio.main

import android.content.res.Configuration
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import org.jetbrains.anko.configuration

fun DrawerBuilder.darkModeToggle(config: Configuration): DrawerBuilder = addStickyDrawerItems(
    SecondarySwitchDrawerItem().withIcon(R.drawable.dark_mode_icon).withSelectable(false).withIdentifier(200).withOnCheckedChangeListener(object :
        OnCheckedChangeListener {
        override fun onCheckedChanged(drawerItem: IDrawerItem<*>, buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }).withName("Dark Mode").withChecked(config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
)