package com.example.findingfalcone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.findingfalcone.models.RequestBody
import com.example.findingfalcone.models.Result
import com.example.findingfalcone.repo.Repository

/** Result screen to show the result. **/
class FindFalconResultFragment : Fragment() {

    /** It will show the result of API whether falcon is found or not. */
    private lateinit var resultTV: TextView

    /** Variable to store the planet name where Falcon has been found. */
    private lateinit var planetTV: TextView

    /** This Button will be used to navigate to [FindFalconFragment] screen to again find falcon. */
    private lateinit var startAgainButton: AppCompatButton

    /** This layout should be visible when we are fetching the data from API. */
    private lateinit var progressBar: ProgressBar

    /** Variable to store the total time taken to go to planet to find Falcon. */
    private var timeTaken: Int? = null

    /** TextView to show the time taken to find falcon. */
    private lateinit var timeTakenTV: TextView

    /** This list will contain selected planet names. */
    private var planets: List<String>? = null

    /** This list will contain selected vehicle names.*/
    private var vehicles: List<String>? = null

    /** ViewModel to fetch the API result. */
    private lateinit var viewModel: MainViewModel

    /** This will store the taken value from API request. */
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeTaken = arguments?.getInt(MainActivity.TIME_TAKEN)
        planets = arguments?.getStringArrayList(MainActivity.PLANETS)
        vehicles = arguments?.getStringArrayList(MainActivity.VEHICLES)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_falcon_result_screen, container, false)

        resultTV = view.findViewById(R.id.resultTV)
        timeTakenTV = view.findViewById(R.id.timeTakenTV)
        planetTV = view.findViewById(R.id.planetTV)
        startAgainButton = view.findViewById(R.id.startAgain)
        progressBar = view.findViewById(R.id.progressBar)

        setupViewModel()

        startAgainButton.setOnClickListener {
            againFindFalcon()
        }

        return view
    }

    /** This method will set up the [viewModel] and adds an observer for token and result data. */
    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.getToken()

        viewModel.tokenLiveData.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                token = response.token
                fetchResult(token)
                generateToast("Token received!!")
            } else {
                generateToast("Error in fetching Token")
            }
        })

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                handleResult(response)
            } else {
                generateToast("Error in fetching Result")
            }
        })
    }

    /** This method will update the UI based on result from API request (/find). */
    private fun handleResult(response: Result) {
        if (response.error != null) {
            hideProgressBar()
            startAgainButton.visibility = View.VISIBLE
            showResultTV("Token not initialized")
            return
        }

        if (response.status != null) {
            if (response.status == "false") {
                hideProgressBar()
                showResultTV("Falcon is not found, Try Again")
                startAgainButton.visibility = View.VISIBLE
            } else if (response.status == "success") {
                hideProgressBar()
                showResultTV("Success! Congratulations on finding falcon.King Shan is mightly pleased.")
                timeTakenTV.text = "Time taken: " + timeTaken.toString()
                timeTakenTV.visibility = View.VISIBLE
                if (response.planet_name != null) {
                    planetTV.text = "Planet found: " + response.planet_name
                    planetTV.visibility = View.VISIBLE
                }
                startAgainButton.visibility = View.VISIBLE
            }
        }
    }

    /** This method will update the [resultTV] text and visibility. */
    private fun showResultTV(text: String) {
        resultTV.text = text
        resultTV.visibility = View.VISIBLE

    }

    /**
     * This method will be used to fetch the result to get the falcon status after we get the
     * token.
     */
    private fun fetchResult(token: String) {
        if (planets != null && vehicles != null) {
            val requestBody = RequestBody(token, planets!!, vehicles!!)
            viewModel.getFalconResult(requestBody)
        } else {
            generateToast("planetlist or vehiclesList is empty")
        }
    }

    /** This method will show the toast. */
    private fun generateToast(msg: String) {
        Toast.makeText(
            requireContext(),
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    /** This method will hide the [progressBar]. */
    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    /** Show [FindFalconFragment] screen to again select planets and vehicles to find falcon. **/
    private fun againFindFalcon() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.fragmentContainer)

        fragment?.let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, FindFalconFragment())
                .addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    companion object {
        fun newInstance(
            timeTaken: Int,
            selectedPlanetNames: MutableList<String>,
            selectedVehicles: List<String>
        ): FindFalconResultFragment {
            val fragment = FindFalconResultFragment()
            val args = Bundle()
            args.putInt(MainActivity.TIME_TAKEN, timeTaken)
            args.putStringArrayList(MainActivity.PLANETS, ArrayList(selectedPlanetNames))
            args.putStringArrayList(MainActivity.VEHICLES, ArrayList(selectedVehicles))
            fragment.arguments = args
            return fragment
        }
    }
}