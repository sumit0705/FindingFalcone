package com.example.findingfalcone

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    @Before
    fun setUp() {
        // Launch the MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testFragmentContainerIsDisplayed() {
        // Check if the fragment container is displayed
        Espresso.onView(ViewMatchers.withId(R.id.fragmentContainer))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDefaultFragmentIsAdded() {
        // Check if the default fragment (FindFalconFragment) is added to the container
        val fragment = getCurrentFragment()
        assert(fragment is FindFalconFragment)
    }

    @Test
    fun testMainActivityTags() {
        Assert.assertEquals("FindingFalcone_TAG", MainActivity.TAG)
        Assert.assertEquals("timeTaken", MainActivity.TIME_TAKEN)
        Assert.assertEquals("selected_planets", MainActivity.PLANETS)
        Assert.assertEquals("selected_vehicles", MainActivity.VEHICLES)
    }

    private fun getCurrentFragment(): Fragment? {
        val activity = getActivity()
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        return fragmentManager.findFragmentById(R.id.fragmentContainer)
    }

    private fun getActivity(): MainActivity {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        var activity: MainActivity? = null
        scenario.onActivity {
            activity = it
        }
        return activity!!
    }
}
