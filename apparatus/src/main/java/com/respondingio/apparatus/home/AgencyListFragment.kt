package com.respondingio.apparatus.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.respondingio.apparatus.R
import com.respondingio.apparatus.agency.NewAgencyActivity
import com.respondingio.apparatus.models.Agency
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.startActivity

class AgencyListFragment : Fragment() {

    val mList: RecyclerView by lazy { find<RecyclerView>(R.id.agency_list_rv) }
    val mFAB: FloatingActionButton by lazy { find<FloatingActionButton>(R.id.agency_list_add) }
    lateinit var mAdapter: SlimAdapter
    var mData: MutableList<Agency> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_agency_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mList.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = SlimAdapter.create().attachTo(mList).register(R.layout.item_agency) { agency: Agency, injector ->
            injector.text(R.id.agencyName, agency.agencyName)
            injector.text(R.id.agencySubtitle, agency.county?.countyName())
            injector.text(R.id.agencyType, agency.county?.toString())

            if (agency.agencyType != null) {
                var agencyType = ""
                for (item in agency.agencyType!!.keys) {
                    agencyType += item.toUpperCase()
                    if (agency.agencyType!!.keys.indexOf(item) != agency.agencyType!!.size - 1 && agency.agencyType!!.size != 1) {
                        agencyType += " • "
                    }
                }
                //injector.text(R.id.agencySubtitle, agencyType)
            }
        }

        loadAgencies()

        mFAB.setOnClickListener {
            startActivity<NewAgencyActivity>()
        }
    }

    private fun loadAgencies() {
        FirebaseFirestore.getInstance().collection("agencies").orderBy("agencyName").get().addOnSuccessListener {
            mData.clear()
            mData = it.toObjects(Agency::class.java)
            mAdapter.updateData(mData)
        }
    }
}