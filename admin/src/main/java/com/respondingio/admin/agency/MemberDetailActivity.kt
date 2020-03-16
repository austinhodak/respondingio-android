package com.respondingio.admin.agency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.respondingio.admin.R
import com.respondingio.admin.models.User
import com.respondingio.admin.utils.Firestore
import com.respondingio.functions.models.realtime.Position
import com.respondingio.functions.utils.Realtime
import kotlinx.android.synthetic.main.activity_member_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.sdk27.coroutines.onClick

class MemberDetailActivity : AppCompatActivity() {

    lateinit var UID: String
    lateinit var agencyID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_detail)

        setSupportActionBar(agencyDetailToolbar)

        processIntent(intent)

        setupDrawer()
    }

    private fun setupDrawer() {

    }

    private fun processIntent(intent: Intent?) {
        val UID = intent?.getStringExtra("userID") ?: return
        val agencyID = intent.getStringExtra("agencyID") ?: return
        this.UID = UID
        this.agencyID = agencyID

        GlobalScope.launch {
            val user = Firestore.getUser(UID)
            runOnUiThread {
                userLoaded(user)
            }
        }
    }

    private fun userLoaded(user: User) {
        title = user.name.toString()
        button?.onClick {
            GlobalScope.launch {
                val mList = Realtime.getPositions(agencyID)
                runOnUiThread {
                    MaterialDialog(this@MemberDetailActivity).show {
                        listItemsMultiChoice(items = mList.map { it.name!! }, initialSelection = getPositionSelectedIndices(mList, user)) { _, indices, items ->
                            // Invoked when the user selects item(s)
                            user.updateUserPositions(mList.slice(indices.toList()), agencyID)
                        }
                        positiveButton(text = "SAVE")
                    }
                }
            }
        }
    }

    private fun getPositionSelectedIndices(
        mList: ArrayList<Position>,
        user: User
    ): IntArray {
        val intArray = ArrayList<Int>()
        val userPositions = user.getPositionsList(agencyID)
        for (i in mList) {
            if (userPositions != null) {
                for (p in userPositions) {
                    if (i.ID == p) {
                        intArray.add(mList.indexOf(i))
                    }
                }
            }
        }
        return intArray.toIntArray()
    }
}
