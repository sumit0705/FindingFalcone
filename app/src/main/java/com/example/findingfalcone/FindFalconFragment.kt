    package com.example.findingfalcone
    
    import android.content.Context
    import android.net.ConnectivityManager
    import android.net.NetworkInfo
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ArrayAdapter
    import android.widget.AutoCompleteTextView
    import android.widget.ProgressBar
    import android.widget.RadioButton
    import android.widget.RadioGroup
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.widget.AppCompatButton
    import androidx.constraintlayout.widget.ConstraintLayout
    import androidx.core.view.children
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.FragmentManager
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import com.example.findingfalcone.models.Planets
    import com.example.findingfalcone.models.Vehicles
    import com.example.findingfalcone.repo.Repository
    import kotlin.system.exitProcess
    
    /** First screen in which user will select its choices. **/
    class FindFalconFragment : Fragment() {
        private lateinit var autoCompleteTV: AutoCompleteTextView
        private lateinit var autoCompleteTV2: AutoCompleteTextView
        private lateinit var autoCompleteTV3: AutoCompleteTextView
        private lateinit var autoCompleteTV4: AutoCompleteTextView
    
        private lateinit var constraintLayout: ConstraintLayout
        private lateinit var timeTakenTV: TextView
        private lateinit var findFalconButton: AppCompatButton
        private lateinit var progressBarLayout: ProgressBar
        private lateinit var viewModel: MainViewModel
        private lateinit var planetsList: List<Planets>
        private lateinit var planetNamesList: MutableList<String>
        private var selectedPlanetNames: MutableList<String> = mutableListOf()
        private var prevSelectedPlanetName: MutableList<String> = mutableListOf("", "", "", "")
        private var prevSelectedRadioButtonId: MutableList<Int> = mutableListOf(0, 0, 0, 0)
        private lateinit var vehiclesList: List<Vehicles>
        private lateinit var vehicleNames: List<String>
        private lateinit var vehicleDist: List<Int>
        private lateinit var vehicleSpeed: List<Int>
        private lateinit var noOfVehicles: MutableList<Int>
        private var shouldRespondToChange: Boolean = true
        private var planetHashMap: LinkedHashMap<String, Int> = LinkedHashMap()
        private var totalTimeTaken :Int = 0
    
        private lateinit var radioGroup1: RadioGroup
        private lateinit var radioGroup2: RadioGroup
        private lateinit var radioGroup3: RadioGroup
        private lateinit var radioGroup4: RadioGroup
        private lateinit var radioButton1: RadioButton
        private lateinit var radioButton2: RadioButton
        private lateinit var radioButton3: RadioButton
        private lateinit var radioButton4: RadioButton
        private lateinit var radioButton5: RadioButton
        private lateinit var radioButton6: RadioButton
        private lateinit var radioButton7: RadioButton
        private lateinit var radioButton8: RadioButton
        private lateinit var radioButton9: RadioButton
        private lateinit var radioButton10: RadioButton
        private lateinit var radioButton11: RadioButton
        private lateinit var radioButton12: RadioButton
        private lateinit var radioButton13: RadioButton
        private lateinit var radioButton14: RadioButton
        private lateinit var radioButton15: RadioButton
        private lateinit var radioButton16: RadioButton
    
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_find_falcon_screen, container, false)
    
            if (!isInternetConnected(requireContext())) {
                generateToast("Check your internet connection")
                exitProcess(0)
            }
            initializeViews(view)
    
            val repo = Repository()
            val viewModelFactory = MainViewModelFactory(repo)
            viewModel =
                ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
    
            addObservers()
            setClickListeners()
    
            return view
        }
    
        private fun setClickListeners() {
            findFalconButton.setOnClickListener {
                generateToast("Fetching Result")
    
                // Return if any planet choice is not selected.
                when {
                    prevSelectedPlanetName[0].isEmpty() -> {
                        generateToast("Select first planet")
                        return@setOnClickListener
                    }
    
                    prevSelectedPlanetName[1].isEmpty() -> {
                        generateToast("Select second planet")
                        return@setOnClickListener
                    }
    
                    prevSelectedPlanetName[2].isEmpty() -> {
                        generateToast("Select third planet")
                        return@setOnClickListener
                    }
    
                    prevSelectedPlanetName[3].isEmpty() -> {
                        generateToast("Select fourth planet")
                        return@setOnClickListener
                    }
                }
    
                // Return if vehicle is not selected for any selected planets.
                when {
                    prevSelectedRadioButtonId[0] == 0 -> {
                        generateToast("Select vehicle to first planet")
                        return@setOnClickListener
                    }
    
                    prevSelectedRadioButtonId[1] == 0 -> {
                        generateToast("Select vehicle to second planet")
                        return@setOnClickListener
                    }
    
                    prevSelectedRadioButtonId[2] == 0 -> {
                        generateToast("Select vehicle to third planet")
                        return@setOnClickListener
                    }
    
                    prevSelectedRadioButtonId[3] == 0 -> {
                        generateToast("Select vehicle to fourth planet")
                        return@setOnClickListener
                    }
                }
    
                val idx1 = radioGroup1.indexOfChild(radioGroup1.findViewById(prevSelectedRadioButtonId[0]))
                val idx2 = radioGroup2.indexOfChild(radioGroup2.findViewById(prevSelectedRadioButtonId[1]))
                val idx3 = radioGroup3.indexOfChild(radioGroup3.findViewById(prevSelectedRadioButtonId[2]))
                val idx4 = radioGroup4.indexOfChild(radioGroup4.findViewById(prevSelectedRadioButtonId[3]))
                val selectedVehicles: List<String> = listOf(vehicleNames[idx1], vehicleNames[idx2], vehicleNames[idx3], vehicleNames[idx4])
    
                showResultScreen(selectedVehicles)
            }
    
            autoCompleteTV.setOnItemClickListener { parent, view, position, id ->
                // Handle item click here
                if (prevSelectedRadioButtonId[0] != 0) {
                    val oldRadioButton: RadioButton =
                        radioGroup1.findViewById(prevSelectedRadioButtonId[0])
                    val idx = radioGroup1.indexOfChild(oldRadioButton)
                    noOfVehicles[idx]++
                    unSelectRadioGroup1()
                    prevSelectedRadioButtonId[0] = 0
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[0]]
                    planetDistance?.let {
                        val i = radioGroup1.indexOfChild(oldRadioButton)
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken -=time
                        updateTime()
                    }
                }
                val selectedItem = parent.getItemAtPosition(position) as String
                updatePlanetNamesList(selectedItem, 0)
                updateRadioGroup(radioGroup1, true, 0)
                updateRadioGroup(radioGroup2, planetIndex = 1)
                updateRadioGroup(radioGroup3, planetIndex = 2)
                updateRadioGroup(radioGroup4, planetIndex = 3)
            }
    
            autoCompleteTV2.setOnItemClickListener { parent, view, position, id ->
                // Handle item click here
                if (prevSelectedRadioButtonId[1] != 0) {
                    val oldRadioButton: RadioButton =
                        requireView().findViewById(prevSelectedRadioButtonId[1])
                    val idx = radioGroup2.indexOfChild(oldRadioButton)
                    noOfVehicles[idx]++
                    unSelectRadioGroup2()
                    prevSelectedRadioButtonId[1] = 0
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[1]]
                    planetDistance?.let {
                        val i = radioGroup2.indexOfChild(oldRadioButton)
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken -=time
                        updateTime()
                    }
                }
                val selectedItem = parent.getItemAtPosition(position) as String
                updatePlanetNamesList(selectedItem, 1)
                updateRadioGroup(radioGroup1, planetIndex = 0)
                updateRadioGroup(radioGroup2, true, 1)
                updateRadioGroup(radioGroup3, planetIndex = 2)
                updateRadioGroup(radioGroup4, planetIndex = 3)
            }
    
            autoCompleteTV3.setOnItemClickListener { parent, view, position, id ->
                // Handle item click here
                if (prevSelectedRadioButtonId[2] != 0) {
                    val oldRadioButton: RadioButton =
                        requireView().findViewById(prevSelectedRadioButtonId[2])
                    val idx = radioGroup3.indexOfChild(oldRadioButton)
                    noOfVehicles[idx]++
                    unSelectRadioGroup3()
                    prevSelectedRadioButtonId[2] = 0
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[2]]
                    planetDistance?.let {
                        val i = radioGroup3.indexOfChild(oldRadioButton)
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken -=time
                        updateTime()
                    }
                }
                val selectedItem = parent.getItemAtPosition(position) as String
                updatePlanetNamesList(selectedItem, 2)
                updateRadioGroup(radioGroup1, planetIndex = 0)
                updateRadioGroup(radioGroup2, planetIndex = 1)
                updateRadioGroup(radioGroup3, true, 2)
                updateRadioGroup(radioGroup4, planetIndex = 3)
            }
    
            autoCompleteTV4.setOnItemClickListener { parent, view, position, id ->
                // Handle item click here
                if (prevSelectedRadioButtonId[3] != 0) {
                    val oldRadioButton: RadioButton =
                        requireView().findViewById(prevSelectedRadioButtonId[3])
                    val idx = radioGroup4.indexOfChild(oldRadioButton)
                    noOfVehicles[idx]++
                    unSelectRadioGroup4()
                    prevSelectedRadioButtonId[3] = 0
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[3]]
                    planetDistance?.let {
                        val i = radioGroup4.indexOfChild(oldRadioButton)
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken -=time
                        updateTime()
                    }
                }
                val selectedItem = parent.getItemAtPosition(position) as String
                updatePlanetNamesList(selectedItem, 3)
                updateRadioGroup(radioGroup1, planetIndex = 0)
                updateRadioGroup(radioGroup2, planetIndex = 1)
                updateRadioGroup(radioGroup3, planetIndex = 2)
                updateRadioGroup(radioGroup4, true, 3)
            }
    
            radioGroup1.setOnCheckedChangeListener { group, checkedId ->
                if (shouldRespondToChange) {
                    // update properties if user has selected vehicles previously.
                    val radioButton: RadioButton = group.findViewById(checkedId)
    
                    // To again show selection as it hides the selection first time
                    shouldRespondToChange = false
                    radioButton.isChecked = true
                    shouldRespondToChange = true
    
                    val i = group.indexOfChild(radioButton)
                    if (noOfVehicles[i] - 1 >= 0) {
                        noOfVehicles[i]--
                    }
    
                    if (prevSelectedRadioButtonId[0] != 0) {
                        val idx = group.indexOfChild(group.findViewById(prevSelectedRadioButtonId[0]))
                        noOfVehicles[idx]++
                        val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[0]]
                        planetDistance?.let {
                            val time = it/vehicleSpeed[idx]
                            totalTimeTaken -=time
                            updateTime()
                        }
                    }
                    prevSelectedRadioButtonId[0] = checkedId
                    updateRadioGroup(radioGroup1, true, 0)
                    updateRadioGroup(radioGroup2, planetIndex = 1)
                    updateRadioGroup(radioGroup3, planetIndex = 2)
                    updateRadioGroup(radioGroup4, planetIndex = 3)
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[0]]
                    planetDistance?.let {
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken +=time
                        updateTime()
                    }
                }
            }
    
            radioGroup2.setOnCheckedChangeListener { group, checkedId ->
                if (shouldRespondToChange) {
                    val radioButton: RadioButton = group.findViewById(checkedId)
    
                    // To again show selection as it hides the selection first time
                    shouldRespondToChange = false
                    radioButton.isChecked = true
                    shouldRespondToChange = true
    
                    val i = group.indexOfChild(radioButton)
                    if (noOfVehicles[i] - 1 >= 0) {
                        noOfVehicles[i]--
                    }
                    if (prevSelectedRadioButtonId[1] != 0) {
                        val idx = group.indexOfChild(group.findViewById(prevSelectedRadioButtonId[1]))
                        noOfVehicles[idx]++
                        val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[1]]
                        planetDistance?.let {
                            val time = it/vehicleSpeed[idx]
                            totalTimeTaken -=time
                            updateTime()
                        }
                    }
                    prevSelectedRadioButtonId[1] = checkedId
                    updateRadioGroup(radioGroup1, planetIndex = 0)
                    updateRadioGroup(radioGroup2, true, 1)
                    updateRadioGroup(radioGroup3, planetIndex = 2)
                    updateRadioGroup(radioGroup4, planetIndex = 3)
    
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[1]]
                    planetDistance?.let {
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken +=time
                        updateTime()
                    }
                }
            }
    
            radioGroup3.setOnCheckedChangeListener { group, checkedId ->
                if (shouldRespondToChange) {
                    val radioButton: RadioButton = group.findViewById(checkedId)
    
                    // To again show selection as it hides the selection first time
                    shouldRespondToChange = false
                    radioButton.isChecked = true
                    shouldRespondToChange = true
    
                    val i = group.indexOfChild(radioButton)
                    if (noOfVehicles[i] - 1 >= 0) {
                        noOfVehicles[i]--
                    }
    
                    if (prevSelectedRadioButtonId[2] != 0) {
                        val idx = group.indexOfChild(group.findViewById(prevSelectedRadioButtonId[2]))
                        noOfVehicles[idx]++
                        val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[2]]
                        planetDistance?.let {
                            val time = it/vehicleSpeed[idx]
                            totalTimeTaken -=time
                            updateTime()
                        }
                    }
                    prevSelectedRadioButtonId[2] = checkedId
                    updateRadioGroup(radioGroup1, planetIndex = 0)
                    updateRadioGroup(radioGroup2, planetIndex = 1)
                    updateRadioGroup(radioGroup3, true, 2)
                    updateRadioGroup(radioGroup4, planetIndex = 3)
    
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[2]]
                    planetDistance?.let {
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken +=time
                        updateTime()
                    }
                }
            }
    
            radioGroup4.setOnCheckedChangeListener { group, checkedId ->
                if (shouldRespondToChange) {
                    val radioButton: RadioButton = group.findViewById(checkedId)
    
                    // To again show selection as it hides the selection first time
                    shouldRespondToChange = false
                    radioButton.isChecked = true
                    shouldRespondToChange = true
    
                    val i = group.indexOfChild(radioButton)
                    if (noOfVehicles[i] - 1 >= 0) {
                        noOfVehicles[i]--
                    }
                    if (prevSelectedRadioButtonId[3] != 0) {
                        val idx = group.indexOfChild(group.findViewById(prevSelectedRadioButtonId[3]))
                        noOfVehicles[idx]++
                        val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[3]]
                        planetDistance?.let {
                            val time = it/vehicleSpeed[idx]
                            totalTimeTaken -=time
                            updateTime()
                        }
                    }
                    prevSelectedRadioButtonId[3] = checkedId
                    updateRadioGroup(radioGroup1, planetIndex = 0)
                    updateRadioGroup(radioGroup2, planetIndex = 1)
                    updateRadioGroup(radioGroup3, planetIndex = 2)
                    updateRadioGroup(radioGroup4, true, 3)
                    val planetDistance: Int? = planetHashMap[prevSelectedPlanetName[3]]
                    planetDistance?.let {
                        val time = it/vehicleSpeed[i]
                        totalTimeTaken +=time
                        updateTime()
                    }
                }
            }
        }
    
        /** Show [FindFalconResultFragment] screen in which result will be displayed. **/
        private fun showResultScreen(selectedVehicles: List<String>) {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragment: Fragment? = fragmentManager.findFragmentById(R.id.fragmentContainer)
    
            fragment?.let {
                val newFragment = FindFalconResultFragment.newInstance(totalTimeTaken, prevSelectedPlanetName, selectedVehicles)
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragmentContainer, newFragment).addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    
        private fun updateTime() {
            timeTakenTV.text = "Time taken: " + totalTimeTaken
        }
    
        private fun initializeViews(view: View) {
            constraintLayout = view.findViewById(R.id.constraintLayout)
            progressBarLayout = view.findViewById(R.id.progress_layout)
            findFalconButton = view.findViewById(R.id.findFalcon)
            autoCompleteTV = view.findViewById(R.id.autoCompleteTextView)
            autoCompleteTV2 = view.findViewById(R.id.autoCompleteTextView2)
            autoCompleteTV3 = view.findViewById(R.id.autoCompleteTextView3)
            autoCompleteTV4 = view.findViewById(R.id.autoCompleteTextView4)
            timeTakenTV = view.findViewById(R.id.timeTaken)
            radioGroup1 = view.findViewById(R.id.radioGroup1)
            radioGroup2 = view.findViewById(R.id.radioGroup2)
            radioGroup3 = view.findViewById(R.id.radioGroup3)
            radioGroup4 = view.findViewById(R.id.radioGroup4)
            radioButton1 = view.findViewById(R.id.radioButton1)
            radioButton2 = view.findViewById(R.id.radioButton2)
            radioButton3 = view.findViewById(R.id.radioButton3)
            radioButton4 = view.findViewById(R.id.radioButton4)
            radioButton5 = view.findViewById(R.id.radioButton5)
            radioButton6 = view.findViewById(R.id.radioButton6)
            radioButton7 = view.findViewById(R.id.radioButton7)
            radioButton8 = view.findViewById(R.id.radioButton8)
            radioButton9 = view.findViewById(R.id.radioButton9)
            radioButton10 = view.findViewById(R.id.radioButton10)
            radioButton11 = view.findViewById(R.id.radioButton11)
            radioButton12 = view.findViewById(R.id.radioButton12)
            radioButton13 = view.findViewById(R.id.radioButton13)
            radioButton14 = view.findViewById(R.id.radioButton14)
            radioButton15 = view.findViewById(R.id.radioButton15)
            radioButton16 = view.findViewById(R.id.radioButton16)
        }
    
        // It will fetch the planets and vehicles from API.
        private fun addObservers() {
            viewModel.myPlanets.observe(viewLifecycleOwner, Observer { response ->
                if (response != null) {
                    planetsList = response
                    setDestination(planetsList)
                    generateToast("Success Response in fetching Planets")
                } else {
                    progressBarLayout.visibility = View.GONE
                    generateToast("Error in fetching Planets")
                }
            })
    
            viewModel.myVehicles.observe(viewLifecycleOwner, Observer { response ->
                if (response != null) {
                    vehiclesList = response
                    setVehicles(vehiclesList)
                    showLayoutAndHideProgressBar()
                    generateToast("Success Response in fetching Vehicles")
                } else {
                    progressBarLayout.visibility = View.GONE
                    generateToast("Error in fetching Vehicles")
                }
            })
        }
    
        // Update Planet names if user selects or changes planet.
        private fun updatePlanetNamesList(selectedItem: String, pos: Int) {
            if (prevSelectedPlanetName[pos].isNotEmpty()) {
                selectedPlanetNames.remove(prevSelectedPlanetName[pos])
                selectedPlanetNames.add(selectedItem)
                planetNamesList.remove(selectedItem)
                planetNamesList.add(prevSelectedPlanetName[pos])
            } else {
                planetNamesList.remove(selectedItem)
                selectedPlanetNames.add(selectedItem)
            }
            prevSelectedPlanetName[pos] = selectedItem
            updateDestination(planetNamesList)
        }
    
        // Set the RadioButton(s) text for RadioGroup1.
        private fun updateRadioGroup(radioGroup: RadioGroup, makeVisible: Boolean = false, planetIndex: Int) {
            radioGroup.children
                .filterIsInstance<RadioButton>()
                .forEachIndexed { index, radioButton ->
                    radioButton.isEnabled = true
                    radioButton.text = vehicleNames[index] + " (" + noOfVehicles[index] + ")"
                }
    
            if (makeVisible) {
                radioGroup.visibility = View.VISIBLE
            }
    
            radioGroup.children
                .filterIsInstance<RadioButton>()
                .forEachIndexed { index, radioButton ->
                    if (noOfVehicles[index] == 0) {
                        radioButton.isEnabled = false
                    }
                    planetHashMap[prevSelectedPlanetName[planetIndex]]?.let { x ->
                        if (vehicleDist[index] < x) {
                            radioButton.isEnabled = false
                        }
                    }
                }
        }
    
        // Remove selection from RadioButtons(s) in RadioGroup1.
        private fun unSelectRadioGroup1() {
            shouldRespondToChange = false
            radioGroup1.children
                .filterIsInstance<RadioButton>()
                .forEach { radioButton ->
                    radioButton.isChecked = false
                }
            shouldRespondToChange = true
        }
    
        // Remove selection from RadioButtons(s) in RadioGroup2.
        private fun unSelectRadioGroup2() {
            shouldRespondToChange = false
            radioGroup2.children
                .filterIsInstance<RadioButton>()
                .forEach { radioButton ->
                    radioButton.isChecked = false
                }
            shouldRespondToChange = true
        }
    
        // Remove selection from RadioButtons(s) in RadioGroup3.
        private fun unSelectRadioGroup3() {
            shouldRespondToChange = false
            radioGroup3.children
                .filterIsInstance<RadioButton>()
                .forEach { radioButton ->
                    radioButton.isChecked = false
                }
            shouldRespondToChange = true
        }
    
        // Remove selection from RadioButtons(s) in RadioGroup1.
        private fun unSelectRadioGroup4() {
            shouldRespondToChange = false
            radioGroup4.children
                .filterIsInstance<RadioButton>()
                .forEach { radioButton ->
                    radioButton.isChecked = false
                }
            shouldRespondToChange = true
        }
    
        // storing separate values for vehicles.
        private fun setVehicles(vList: List<Vehicles>) {
            vehicleNames = vList.map { it.name }
            vehicleDist = vList.map { it.max_distance }
            vehicleSpeed = vList.map { it.speed }
            noOfVehicles = vList.map { it.total_no }.toMutableList()
        }
    
        private fun setDestination(pList: List<Planets>) {
            planetNamesList = pList.map { it.name }.toMutableList()
            val planetDistanceList = planetsList.map { it.distance }
            for(i in planetNamesList.indices) {
                planetHashMap[planetNamesList[i]] = planetDistanceList[i]
            }
            updateDestination(planetNamesList)
        }
    
        /** It will update the destinations on all AutoCompleteTextViews after user has selected a destination from drop-down menu or to set the AutoCompleteTextViews initially. **/
        private fun updateDestination(pNamesList: List<String>) {
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, pNamesList)
            // set adapter to the autocomplete tv to the arrayAdapter
            autoCompleteTV.setAdapter(arrayAdapter)
            autoCompleteTV2.setAdapter(arrayAdapter)
            autoCompleteTV3.setAdapter(arrayAdapter)
            autoCompleteTV4.setAdapter(arrayAdapter)
        }
    
        // To show the toast.
        private fun generateToast(msg: String) {
            Toast.makeText(
                requireContext(),
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    
        // Show the layout for user interaction on Success result of vehicles and planets.
        private fun showLayoutAndHideProgressBar() {
            progressBarLayout.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
        }
    
        // To check if the internet connection is active or not.
        private fun isInternetConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
    }