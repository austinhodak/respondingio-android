package com.respondingio.apparatus.drawer

import android.os.Handler
import androidx.annotation.ColorRes
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.holder.ColorHolder
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

class CustomPrimaryDrawerItem : AbstractBadgeableDrawerItem<CustomPrimaryDrawerItem>() {

    var background: ColorHolder? = null
    var handler = Handler()

    fun withBackgroundColor(backgroundColor: Int): CustomPrimaryDrawerItem {
        this.background = ColorHolder.fromColor(backgroundColor)
        return this
    }

    fun withBackgroundRes(backgroundRes: Int): CustomPrimaryDrawerItem {
        this.background = ColorHolder.fromColorRes(backgroundRes)
        return this
    }

    override fun bindView(holder: AbstractBadgeableDrawerItem.ViewHolder, payloads: List<*>?) {
        super.bindView(holder, payloads)



        if (background != null) {
            background!!.applyToBackground(holder.itemView)

        }


    }

    private var runFlasher: Boolean = false

    fun flashItem(mDrawer: Drawer?, @ColorRes firstColor: Int, @ColorRes secondColor: Int, waitDuration: Long) {
        if (waitDuration < 375) {
            throw IllegalArgumentException("Values lower than 375 will cause flickering.")
        }

        runFlasher = true

        val runnableCode = object: Runnable {
            override fun run() {
                if (background!!.colorRes == firstColor) {
                    withBackgroundRes(secondColor)
                } else {
                    withBackgroundRes(firstColor)
                }

                mDrawer?.updateItem(this@CustomPrimaryDrawerItem)

                if (runFlasher) {
                    handler.postDelayed(this, waitDuration)
                } else {
                    withBackgroundRes(firstColor)
                }
            }
        }

        runnableCode.run()
    }

    fun stopFlashing() {
        runFlasher = false
    }
}