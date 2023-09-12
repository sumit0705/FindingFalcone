package com.example.findingfalcone.initializers

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.findingfalcone.R

/**
 * A helper class responsible for initializing and managing view components
 * within the FindFalconFragment.
 */
class FindFalconFragmentViewInitializer(view: View) {
    /**
     * List of AutoCompleteTextViews used for selecting planets.
     */
    val planetAutoCompleteTextViews: List<AutoCompleteTextView> = listOf(
    view.findViewById(R.id.autoCompleteTextView),
    view.findViewById(R.id.autoCompleteTextView2),
    view.findViewById(R.id.autoCompleteTextView3),
    view.findViewById(R.id.autoCompleteTextView4)
    )

    /**
     * List of RadioGroups used for selecting destination vehicles.
     */
    val vehicleRadioGroups: List<RadioGroup> = listOf(
    view.findViewById(R.id.radioGroup1),
    view.findViewById(R.id.radioGroup2),
    view.findViewById(R.id.radioGroup3),
    view.findViewById(R.id.radioGroup4)
    )

    /**
     * The ProgressBar used to indicate loading or progress and it should be visible when we are
     * fetching the data from API..
     */
    //
    val progressBar: ProgressBar = view.findViewById(R.id.progress_layout)

    /**
     * The ConstraintLayout containing the fragment's layout and it should be visible when data
     * from API is available..
     */
    //
    val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayout)

    /**
     * This Button will be used to show the result screen i.e. FindFalconResultFragment when user
     * has selected 4 planets and 4 vehicles.
     */
    val findFalconButton: AppCompatButton = view.findViewById(R.id.findFalcon)

    /**
     * The TextView used to display the time taken for the mission.
     */
    val timeTakenTV: TextView = view.findViewById(R.id.timeTaken)
}