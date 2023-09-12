package com.example.findingfalcone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.findingfalcone.initializers.FindFalconFragmentViewInitializer
import com.example.findingfalcone.models.Planets
import com.example.findingfalcone.models.Vehicles
import com.example.findingfalcone.repo.Repository
import com.example.findingfalcone.utils.NetworkUtils
import kotlin.system.exitProcess

/** First screen in which user will select its choices. */
class FindFalconFragment : Fragment() {

    /** This list will contain all the planets data from API. */
    private lateinit var planetsList: List<Planets>

    /**
     * This list will contain currently available planets names to show in
     * [FindFalconFragmentViewInitializer.planetAutoCompleteTextViews].
     */
    private lateinit var planetNamesList: MutableList<String>

    /**
     * This list will contain currently selected planet names by the user in
     * [FindFalconFragmentViewInitializer.planetAutoCompleteTextViews].
     */
    private var selectedPlanetNames: MutableList<String> = mutableListOf()

    /**
     * This list is used for manipulating the data and contains the previously selected planet name
     * by the user in [FindFalconFragmentViewInitializer.planetAutoCompleteTextViews].
     */
    private var prevSelectedPlanetNames: MutableList<String> = mutableListOf("", "", "", "")

    /** This list will contain all the vehicles data from API. */
    private lateinit var vehiclesList: List<Vehicles>

    /** This list will contain all the vehicle names from the vehiclesList. */
    private lateinit var vehicleNames: List<String>

    /**
     * This list will contain all the vehicle maximum distances that they can travel from
     * the [vehiclesList].
     */
    private lateinit var vehicleDistances: List<Int>

    /** This list will contain all the vehicle speeds from the [vehiclesList]. */
    private lateinit var vehicleSpeeds: List<Int>

    /** This list will contain all the total number of vehicles from the [vehiclesList]. */
    private lateinit var noOfVehicles: MutableList<Int>

    /** ViewModel to fetch the API result. */
    private lateinit var viewModel: MainViewModel

    /**
     * The [FindFalconFragmentViewInitializer] instance used to initialize and manage view components
     * within the [FindFalconFragment].
     */
    private lateinit var viewInitializer: FindFalconFragmentViewInitializer

    /**
     * This list will contain Radio Ids of the currently selected vehicles (i.e. RadioButton(s)) on
     * all the [FindFalconFragmentViewInitializer.vehicleRadioGroups].
     */
    private var selectedVehicleRadioIds: MutableList<Int> = mutableListOf(0, 0, 0, 0)

    /**
     * This hashmap will store values in [k,V] Pair when K is planet name and V is the distance to
     * that planet.
     */
    private var planetHashMap: LinkedHashMap<String, Int> = LinkedHashMap()

    /**
     * Boolean variable to be mainly used to not call [updateRadioGroupOnSelection] method when
     * RadioButton.isChecked property is manually updated.
     */
    private var shouldRespondToChange: Boolean = true

    /** Variable to store the current total time taken to go to planet to find Falcon. */
    private var totalTimeTaken: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_falcon_screen, container, false)

        if (!NetworkUtils.isInternetConnected(requireContext())) {
            generateToast("Check your internet connection")
            exitProcess(0)
        }
        viewInitializer = FindFalconFragmentViewInitializer(view)
        setupViewModel()
        setClickListeners()

        return view
    }

    /** This method will set up the [viewModel] and adds an observer for planet and vehicle data. */
    private fun setupViewModel() {
        val repo = Repository()
        val viewModelFactory = MainViewModelFactory(repo)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]

        viewModel.planetsLiveData.observe(viewLifecycleOwner, Observer { planets ->
            if (planets != null) {
                planetsList = planets
                setDestination(planetsList)
                generateToast("Success Response in fetching Planets")
            } else {
                viewInitializer.progressBar.visibility = View.GONE
                generateToast("Error in fetching Planets")
            }
        })

        viewModel.vehiclesLiveData.observe(viewLifecycleOwner, Observer { vehicles ->
            if (vehicles != null) {
                vehiclesList = vehicles
                setVehicles(vehiclesList)
                showLayoutAndHideProgressBar()
                generateToast("Success Response in fetching Vehicles")
            } else {
                viewInitializer.progressBar.visibility = View.GONE
                generateToast("Error in fetching Vehicles")
            }
        })
    }

    /** This method will set click listeners. */
    private fun setClickListeners() {
        viewInitializer.findFalconButton.setOnClickListener {
            if (validateSelections()) {
                val selectedVehicles = getSelectedVehicles()
                showResultScreen(selectedVehicles)
            }
        }

        for (i in viewInitializer.planetAutoCompleteTextViews.indices) {
            viewInitializer.planetAutoCompleteTextViews[i].setOnItemClickListener { parent, _, position, _ ->
                // Handle planet selection here.
                updateAutoCompleteTextViews(i)
                val selectedItem = parent.getItemAtPosition(position) as String
                updatePlanetNamesList(selectedItem, i)
                updateRadioGroup(i)
            }
        }

        for (i in viewInitializer.vehicleRadioGroups.indices) {
            viewInitializer.vehicleRadioGroups[i].setOnCheckedChangeListener { _, checkedId ->
                updateRadioGroupOnSelection(i, checkedId)
            }
        }
    }

    /** This method will set [planetNamesList] and [planetHashMap] and update the values in
     *  [FindFalconFragmentViewInitializer.planetAutoCompleteTextViews] via [updateDestination] method.
     */
    private fun setDestination(pList: List<Planets>) {
        planetNamesList = pList.map { it.name }.toMutableList()
        val planetDistanceList = planetsList.map { it.distance }
        for (i in planetNamesList.indices) {
            planetHashMap[planetNamesList[i]] = planetDistanceList[i]
        }
        updateDestination(planetNamesList)
    }

    /** This method will set vehicles data. */
    private fun setVehicles(vList: List<Vehicles>) {
        vehicleNames = vList.map { it.name }
        vehicleDistances = vList.map { it.max_distance }
        vehicleSpeeds = vList.map { it.speed }
        noOfVehicles = vList.map { it.total_no }.toMutableList()
    }

    /** This method will update the destinations on
     * [FindFalconFragmentViewInitializer.planetAutoCompleteTextViews] after user has selected
     *  a destination from drop-down menu or to set its value initially.
     */
    private fun updateDestination(pNamesList: List<String>) {
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, pNamesList)
        // set adapter to the autocomplete tv to the arrayAdapter
        for (i in viewInitializer.planetAutoCompleteTextViews.indices) {
            viewInitializer.planetAutoCompleteTextViews[i].setAdapter(arrayAdapter)
        }
    }

    /** This method will validate the selections whether user has selected 4 planets and 4 vehicles
     *  to go to those planets.
     */
    private fun validateSelections(): Boolean {
        // Return false if any planet choice is not selected.
        for (i in prevSelectedPlanetNames.indices) {
            if (prevSelectedPlanetNames[i].isEmpty()) {
                generateToast("Please select planet ${i + 1}")
                return false
            }
        }

        // Return false if vehicle is not selected for any selected planets.
        for (i in selectedVehicleRadioIds.indices) {
            if (selectedVehicleRadioIds[i] == 0) {
                generateToast("Planet select vehicle to planet ${i + 1}")
                return false
            }
        }

        return true
    }

    /** This method will return the selected vehicles by the user to go the planets. */
    private fun getSelectedVehicles(): List<String> {
        val selectedVehicles = mutableListOf<String>()
        for (i in selectedVehicleRadioIds.indices) {
            val index = viewInitializer.vehicleRadioGroups[i].indexOfChild(
                viewInitializer.vehicleRadioGroups[i].findViewById(selectedVehicleRadioIds[i])
            )
            selectedVehicles.add(vehicleNames[index])
        }
        return selectedVehicles
    }

    /** Show [FindFalconResultFragment] screen in which result will be displayed. */
    private fun showResultScreen(selectedVehicles: List<String>) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.fragmentContainer)

        fragment?.let {
            val findFalconResultFragment = FindFalconResultFragment.newInstance(
                totalTimeTaken,
                prevSelectedPlanetNames,
                selectedVehicles
            )
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, findFalconResultFragment)
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    /**
     * This method will be called whenever user selects any vehicle (or radiobutton) from any
     * [FindFalconFragmentViewInitializer.vehicleRadioGroups] and it will update the [noOfVehicles]
     * (currently available vehicles), [FindFalconFragmentViewInitializer.timeTakenTV] (total time taken)
     * and it will also call [updateRadioGroup] method to update the radioButton(s) text.
     */
    private fun updateRadioGroupOnSelection(i: Int, checkedId: Int) {
        if (shouldRespondToChange) {
            val radioButton: RadioButton = viewInitializer.vehicleRadioGroups[i].findViewById(checkedId)

            // To again show selection as it hides the selection first time
            shouldRespondToChange = false
            radioButton.isChecked = true
            shouldRespondToChange = true

            val index = viewInitializer.vehicleRadioGroups[i].indexOfChild(radioButton)
            if (noOfVehicles[index] - 1 >= 0) {
                noOfVehicles[index]--
            }
            if (selectedVehicleRadioIds[i] != 0) {
                val idx = viewInitializer.vehicleRadioGroups[i].indexOfChild(
                    viewInitializer.vehicleRadioGroups[i].findViewById(selectedVehicleRadioIds[i])
                )
                noOfVehicles[idx]++
                updateTimer(i, idx)
            }
            selectedVehicleRadioIds[i] = checkedId
            updateRadioGroup(i)
            updateTimer(i, index, false)
        }
    }

    /** This method will update [noOfVehicles] if user has previously selected vehicle and call
     *  the [updateTimer] method to update and show the time.
     */
    private fun updateAutoCompleteTextViews(i: Int) {
        if (selectedVehicleRadioIds[i] != 0) {
            val oldRadioButton: RadioButton =
                viewInitializer.vehicleRadioGroups[i].findViewById(selectedVehicleRadioIds[i])
            val idx = viewInitializer.vehicleRadioGroups[i].indexOfChild(oldRadioButton)
            noOfVehicles[idx]++
            unSelectRadioGroup(i)
            selectedVehicleRadioIds[i] = 0
            updateTimer(i, idx)
        }
    }

    /** This method will update and show the time. */
    private fun updateTimer(planetIndex: Int, vehicleIndex: Int, decreaseTime: Boolean = true) {
        val planetDistance: Int? = planetHashMap[prevSelectedPlanetNames[planetIndex]]
        planetDistance?.let {
            val time = it / vehicleSpeeds[vehicleIndex]
            if (decreaseTime) {
                totalTimeTaken -= time
            } else {
                totalTimeTaken += time
            }
            updateTime()
        }
    }

    /**
     * This method will update the text of [FindFalconFragmentViewInitializer.timeTakenTV] and
     * show the new time to user.
     */
    private fun updateTime() {
        viewInitializer.timeTakenTV.text = "Time taken: " + totalTimeTaken
    }

    /**
     * This method will show the layout for user interaction on Success result of vehicles and planets.
     */
    private fun showLayoutAndHideProgressBar() {
        viewInitializer.progressBar.visibility = View.GONE
        viewInitializer.constraintLayout.visibility = View.VISIBLE
    }

    /**
     * This method will update [planetNamesList] and [selectedPlanetNames] if user has previously
     *  selected planet and update the values in [FindFalconFragmentViewInitializer.planetAutoCompleteTextViews]
     *  via [updateDestination] method.
     */
    private fun updatePlanetNamesList(selectedItem: String, pos: Int) {
        if (prevSelectedPlanetNames[pos].isNotEmpty()) {
            selectedPlanetNames.remove(prevSelectedPlanetNames[pos])
            selectedPlanetNames.add(selectedItem)
            planetNamesList.remove(selectedItem)
            planetNamesList.add(prevSelectedPlanetNames[pos])
        } else {
            planetNamesList.remove(selectedItem)
            selectedPlanetNames.add(selectedItem)
        }
        prevSelectedPlanetNames[pos] = selectedItem
        updateDestination(planetNamesList)
    }

    /**
     * This method will set the RadioButton(s) text for
     * [FindFalconFragmentViewInitializer.vehicleRadioGroups] and show appropriate Enable/Disable
     * status of RadioButton(s) and make RadioGroup visible via the [index] variable.
     */
    private fun updateRadioGroup(index: Int) {
        // To set all the RadioButton(s) text in destinationVehicleRadioGroups.
        for (i in viewInitializer.vehicleRadioGroups.indices) {
            viewInitializer.vehicleRadioGroups[i].children
                .filterIsInstance<RadioButton>()
                .forEachIndexed { idx, radioButton ->
                    radioButton.isEnabled = true
                    radioButton.text = vehicleNames[idx] + " (" + noOfVehicles[idx] + ")"
                }

            // To update the visibility of destinationVehicleRadioGroups[index] as visible.
            viewInitializer.vehicleRadioGroups[index].visibility = View.VISIBLE

            // To update the Enable/Disable status of RadioButton(s) as per the given criteria.
            viewInitializer.vehicleRadioGroups[i].children
                .filterIsInstance<RadioButton>()
                .forEachIndexed { idx, radioButton ->
                    if (noOfVehicles[idx] == 0) {
                        radioButton.isEnabled = false
                    }
                    planetHashMap[prevSelectedPlanetNames[i]]?.let { x ->
                        if (vehicleDistances[idx] < x) {
                            radioButton.isEnabled = false
                        }
                    }
                }
        }
    }

    /**
     * This method will remove selection from RadioButtons(s) in
     * [FindFalconFragmentViewInitializer.vehicleRadioGroups] at [index].
     */
    private fun unSelectRadioGroup(index: Int) {
        shouldRespondToChange = false
        viewInitializer.vehicleRadioGroups[index].children
            .filterIsInstance<RadioButton>()
            .forEach { radioButton ->
                radioButton.isChecked = false
            }
        shouldRespondToChange = true
    }

    /** This method will show the toast. */
    private fun generateToast(msg: String) {
        Toast.makeText(
            requireContext(),
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}