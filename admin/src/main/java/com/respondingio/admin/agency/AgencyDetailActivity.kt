package com.respondingio.admin.agency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.respondingio.admin.R
import com.respondingio.admin.models.Agency
import com.respondingio.admin.utils.Firestore
import kotlinx.android.synthetic.main.activity_agency_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AgencyDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agency_detail)

        setSupportActionBar(agencyDetailToolbar)

        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
        val agencyID = intent?.getStringExtra("agencyID") ?: return

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
