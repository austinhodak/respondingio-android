package com.respondingio.admin.agency

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.respondingio.admin.R
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.Step
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import kotlinx.android.synthetic.main.activity_new_agency.*
import org.jetbrains.anko.find

class NewAgencyActivity : AppCompatActivity() {

    private lateinit var mStepperLayout: StepperLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_agency)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        mStepperLayout = find(R.id.newAgencyStepper)
        mStepperLayout.adapter = NewAgencyStepperAdapater(supportFragmentManager, this)

        setupFirstBox()
    }

    private var selectedAgencyTypes: IntArray = intArrayOf()

    private fun setupFirstBox() {
        /*newAgencyTypeButton.setOnClickListener {
            MaterialDialog(this).show {
                listItemsMultiChoice(R.array.agency_types, initialSelection = selectedAgencyTypes) { _, indices, text ->
                    // Invoked when the user selects item(s)
                    selectedAgencyTypes = indices
                    (it as Button).text = text.joinToString(separator = ", ")
                }
                positiveButton(text = "DONE")
            }
        }*/
    }

    class NewAgencyStepperAdapater(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm, context) {
        override fun getCount(): Int {
            return 3
        }

        override fun createStep(position: Int): Step {
             return when (position) {
                1 -> {
                    NewAgencyFirstStep()
                }
                else -> {
                    NewAgencyFirstStep()
                }
            }
        }
    }

    class NewAgencyFirstStep : Fragment(), BlockingStep {

        @UiThread
        override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback?) {

        }

        @UiThread
        override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {

        }

        @UiThread
        override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback?) {
            val dialog = ProgressDialog(context)
            dialog.setCancelable(false)
            dialog.setMessage("Saving")
            dialog.show()
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.item_agency, container, false)
        }

        override fun onSelected() {

        }

        override fun verifyStep(): VerificationError? {
            //return VerificationError("Not Finished.")
            return null
        }

        override fun onError(error: VerificationError) {

        }

    }
}