package com.example.findingfalcone

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.findingfalcone.models.Planets
import com.example.findingfalcone.models.Vehicles
import com.example.findingfalcone.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var planetsObserver: Observer<List<Planets>>

    @Mock
    lateinit var vehiclesObserver: Observer<List<Vehicles>>

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun testFetchPlanetsSuccess() = runTest {
        // Arrange
        val mockPlanetsList = listOf(Planets("Planet1", 100), Planets("Planet2", 200))
        Mockito.`when`(repository.getPlanets()).thenReturn(mockPlanetsList)

        // Act
        viewModel.planetsLiveData.observeForever(planetsObserver)
        viewModel.getPlanets()

        // Assert
        Mockito.verify(planetsObserver).onChanged(mockPlanetsList)
        viewModel.planetsLiveData.removeObserver(planetsObserver)
    }

    @Test
    fun testFetchPlanetsEmptyList() = runTest {
        // Arrange
        Mockito.`when`(repository.getPlanets()).thenReturn(emptyList())

        // Act
        viewModel.planetsLiveData.observeForever(planetsObserver)
        viewModel.getPlanets()

        // Assert
        Mockito.verify(planetsObserver).onChanged(emptyList())
        viewModel.planetsLiveData.removeObserver(planetsObserver)
    }

    @Test
    fun testFetchVehiclesSuccess() = runTest {
        // Arrange
        val mockVehiclesList = listOf(Vehicles("Vehicle1", 100, 50, 2), Vehicles("Vehicle2", 200, 60, 5))
        Mockito.`when`(repository.getVehicles()).thenReturn(mockVehiclesList)

        // Act
        viewModel.vehiclesLiveData.observeForever(vehiclesObserver)
        viewModel.getVehicles()

        // Assert
        Mockito.verify(vehiclesObserver).onChanged(mockVehiclesList)
        viewModel.vehiclesLiveData.removeObserver(vehiclesObserver)
    }

    @Test
    fun testFetchVehiclesEmptyList() = runTest {
        // Arrange
        Mockito.`when`(repository.getVehicles()).thenReturn(emptyList())

        // Act
        viewModel.vehiclesLiveData.observeForever(vehiclesObserver)
        viewModel.getVehicles()

        // Assert
        Mockito.verify(vehiclesObserver).onChanged(emptyList())
        viewModel.vehiclesLiveData.removeObserver(vehiclesObserver)

    }

    class MainCoroutineRule : TestRule {
        override fun apply(base: Statement, description: Description?): Statement {
            return object : Statement() {
                override fun evaluate() {
                    Dispatchers.setMain(Dispatchers.Unconfined)
                    try {
                        base.evaluate()
                    } finally {
                        Dispatchers.resetMain()
                    }
                }
            }
        }
    }
}

