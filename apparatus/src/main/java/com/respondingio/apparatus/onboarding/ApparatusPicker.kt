package com.respondingio.apparatus.onboarding

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.respondingio.apparatus.R
import com.respondingio.apparatus.models.Apparatus
import com.respondingio.apparatus.snacky.Snacky
import kotlinx.android.synthetic.main.onboarding_picker.*
import net.idik.lib.slimadapter.SlimAdapter
import org.jetbrains.anko.toast

class ApparatusPicker : AppCompatActivity() {

    private var mAdapter: SlimAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateUI()
        setContentView(R.layout.onboarding_picker)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        pickerRV?.layoutManager = GridLayoutManager(this, 3)

        var mData: MutableList<Apparatus> = ArrayList()
        mAdapter = SlimAdapter.create().attachTo(pickerRV).register(R.layout.apparatus_picker_card) { data: Apparatus, injector ->
            injector.text(R.id.apparatusName, data.apparatusName)
            injector.text(R.id.apparatusUnit, data.apparatusCode)

            val card = injector.findViewById<CardView>(R.id.pickerCard)
            if (data.selected == true) {
                card.setCardBackgroundColor(resources.getColor(R.color.timelineGreen))
            } else {
                card.setCardBackgroundColor(resources.getColor(R.color.md_grey_850))
            }

            injector.clicked(R.id.pickerCard) {
                val index = mData.indexOf(data)
                data.selected = true

                mData.removeAt(index)

                for (item in mData) {
                    if (item.selected == true) {
                        item.selected = false
                    }
                }

                mData.add(index, data)
                mAdapter?.updateData(mData)
            }
        }.updateData(mData)

        MaterialDialog(this).show {
            title(text = "Enter Agency Code")
            cancelOnTouchOutside(false)
            input { dialpog, text ->
                progressBar?.visibility = View.VISIBLE
                // Text submitted with the action button
                FirebaseFirestore.getInstance().collection("agencies").whereEqualTo("agencyCode", text.toString()).get().addOnSuccessListener {
                    if (it.documents.isEmpty()) return@addOnSuccessListener
                    FirebaseFirestore.getInstance().collection("agencies").document(it.documents[0].id).collection("apparatus").orderBy("apparatusName").get().addOnSuccessListener {
                        for (item in it.documents) {
                            val apparatus = item.toObject(Apparatus::class.java)!!
                            apparatus.agencyID = intent.getStringExtra("agencyID")
                            apparatus.apparatusID = item.id

                            mData.add(apparatus)
                        }

                        mAdapter?.updateData(mData)
                        //progressBar?.visibility = View.GONE
                    }
                }
            }
            positiveButton(text = "CONTINUE")
        }

        pickerSave?.setOnClickListener {
            for (item in mData) {
                if (item.selected == true) {
                    progressBar?.visibility = View.VISIBLE
                    signInApparatus(item.agencyID!!, item.apparatusID!!).addOnSuccessListener { token ->
                        token.let {
                            FirebaseAuth.getInstance().signInWithCustomToken(it).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    task.result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(item.apparatusName).build())
                                    Snacky.builder().setActivity(this).success().setText("Signed In!").show()
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                } else {
                                    Snacky.builder().setActivity(this).error().setText(task.exception?.message.toString()).show()
                                    progressBar?.visibility = View.GONE
                                }
                            }
                        }
                    }
                    break
                }
            }
        }
    }

    private fun signInApparatus(agencyID: String, apparatusID: String): Task<String> {
        val data = hashMapOf(
            "agencyID" to agencyID,
            "apparatusID" to apparatusID
        )

        return FirebaseFunctions.getInstance().getHttpsCallable("authSignInApparatus")
            .call(data)
            .continueWith {
                val result = it.result?.data as String
                result
            }
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
    }
}