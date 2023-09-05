package com.example.findingfalcone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/** Starting point of the App. **/
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragment: Fragment? = fragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment == null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragmentContainer, FindFalconFragment())
            fragmentTransaction.commit()
        }
    }

    companion object {
        const val TAG = "FindingFalcone_TAG"
        const val TIME_TAKEN = "timeTaken"
        const val PLANETS = "selected_planets"
        const val VEHICLES = "selected_vehicles"
    }
}