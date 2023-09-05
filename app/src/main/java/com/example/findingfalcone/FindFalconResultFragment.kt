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

/** Result screen to show the result. **/
class FindFalconResultFragment: Fragment() {
    private lateinit var resultTV: TextView
    private lateinit var timeTakenTV: TextView
    private lateinit var planetTV: TextView
    private lateinit var startAgainButton: AppCompatButton
    private lateinit var progressBar: ProgressBar
    private var timeTaken: Int? = null
    private var planets: List<String>? = null
    private var vehicles: List<String>? = null
    private lateinit var viewModel: MainViewModel
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
        val view = inflater.inflate(R.layout.fragment_find_falcon_result_screen,container, false)

        resultTV = view.findViewById(R.id.resultTV)
        timeTakenTV = view.findViewById(R.id.timeTakenTV)
        planetTV = view.findViewById(R.id.planetTV)
        startAgainButton = view.findViewById(R.id.startAgain)
        progressBar = view.findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.getToken()

        viewModel.myToken.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                token = response.token
                fetchResult(token)
                generateToast("Token received!!")
            } else {
                generateToast("Error in fetching Token")
            }
        })

        viewModel.myFalconResult.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                handleResult(response)
            } else {
                generateToast("Error in fetching Result")
            }
        })

        startAgainButton.setOnClickListener {
            againFindFalcon()
        }

        return view
    }

    private fun handleResult(response: Result) {
        if(response.error != null) {
            hideProgressBar()
            startAgainButton.visibility = View.VISIBLE
            showResultTV("Token not initialized")
            return
        }

        if (response.status != null) {
            if(response.status == "false") {
                hideProgressBar()
                showResultTV("Falcon is not found, Try Again")
                startAgainButton.visibility = View.VISIBLE
            } else if(response.status == "success") {
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

    private fun showResultTV(text: String) {
        resultTV.text = text
        resultTV.visibility = View.VISIBLE

    }

    private fun fetchResult(token: String) {
        if (planets!= null && vehicles != null) {
            val requestBody = RequestBody(token, planets!!, vehicles!!)
            viewModel.getFalconResult(requestBody)
        } else {
            generateToast("planetlist or vehiclesList is empty")
        }
    }

    private fun generateToast(msg: String) {
        Toast.makeText(
            requireContext(),
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    /** Show [FindFalconFragment] screen to again find falcon. **/
    private fun againFindFalcon() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.fragmentContainer)

        fragment?.let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, FindFalconFragment()).addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    companion object {
        fun newInstance(timeTaken: Int, selectedPlanetNames: MutableList<String>, selectedVehicles: List<String>): FindFalconResultFragment {
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