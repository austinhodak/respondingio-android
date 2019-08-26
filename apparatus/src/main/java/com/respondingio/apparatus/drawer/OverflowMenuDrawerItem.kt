package com.respondingio.apparatus.drawer

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.annotation.LayoutRes
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.materialdrawer.holder.ColorHolder
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem
import com.mikepenz.materialdrawer.model.BaseViewHolder
import com.respondingio.apparatus.R


class OverflowMenuDrawerItem : BaseDescribeableDrawerItem<OverflowMenuDrawerItem, OverflowMenuDrawerItem.ViewHolder>() {
    var background: ColorHolder? = null
    private var height: Long? = null

    var menu: Int = 0
        private set

    var onMenuItemClickListener: PopupMenu.OnMenuItemClickListener? = null
        private set

    var onDismissListener: PopupMenu.OnDismissListener? = null
        private set

    fun withMenu(menu: Int): OverflowMenuDrawerItem {
        this.menu = menu
        return this
    }

    fun withBackgroundColor(backgroundColor: Int): OverflowMenuDrawerItem {
        this.background = ColorHolder.fromColor(backgroundColor)
        return this
    }

    fun withBackgroundRes(backgroundRes: Int): OverflowMenuDrawerItem {
        this.background = ColorHolder.fromColorRes(backgroundRes)
        return this
    }

    fun withOnMenuItemClickListener(onMenuItemClickListener: PopupMenu.OnMenuItemClickListener): OverflowMenuDrawerItem {
        this.onMenuItemClickListener = onMenuItemClickListener
        return this
    }

    fun withOnDismissListener(onDismissListener: PopupMenu.OnDismissListener): OverflowMenuDrawerItem {
        this.onDismissListener = onDismissListener
        return this
    }

    fun withHeight(height: Long): OverflowMenuDrawerItem {
        this.height = height
        return this
    }

    override fun getType(): Int {
        return R.id.material_drawer_item_overflow_menu
    }

    @LayoutRes
    override fun getLayoutRes(): Int {
        return R.layout.material_drawer_item_overflow_menu_primary
    }

    override fun bindView(viewHolder: ViewHolder, payloads: List<*>?) {
        super.bindView(viewHolder, payloads)

        val ctx = viewHolder.itemView.context

        //bind the basic view parts
        bindViewHelper(viewHolder)

        //handle menu click
        viewHolder.menu.setOnClickListener { view ->
//            val popup = PopupMenu(view.context, view)
//
//            val inflater = popup.menuInflater
//            inflater.inflate(menu, popup.menu)
//
//            popup.setOnMenuItemClickListener(onMenuItemClickListener)
//            popup.setOnDismissListener(onDismissListener)
//
//            popup.show()
            val myItems = listOf("Unit Returning", "Unit Returning (Cancelled)")

            val dialog = MaterialDialog(ctx).listItems(items = myItems)

            updateUI(dialog)
        }

        //handle image
        viewHolder.menu.setImageDrawable(
            IconicsDrawable(ctx, GoogleMaterial.Icon.gmd_more_vert).sizeDp(12).color(
                Color.WHITE
            )
        )

        if (background != null) {
            background!!.applyToBackground(viewHolder.itemView)
        }

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView)
    }



    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : BaseViewHolder(view) {
        //protected ImageButton ibOverflow;
        val menu: ImageButton

        init {
            this.menu = view.findViewById(R.id.material_drawer_menu_overflow)
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