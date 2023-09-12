package com.example.findingfalcone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findingfalcone.models.Result
import com.example.findingfalcone.models.Planets
import com.example.findingfalcone.models.RequestBody
import com.example.findingfalcone.models.Token
import com.example.findingfalcone.models.Vehicles
import com.example.findingfalcone.repo.Repository
import kotlinx.coroutines.launch

/** Creates a new ViewModel instance. */
class MainViewModel(private val repo: Repository) : ViewModel() {

    val planetsLiveData: MutableLiveData<List<Planets>> = MutableLiveData()
    val vehiclesLiveData: MutableLiveData<List<Vehicles>> = MutableLiveData()
    val tokenLiveData: MutableLiveData<Token> = MutableLiveData()
    val resultLiveData: MutableLiveData<Result> = MutableLiveData()

    init {
        getPlanets()
        getVehicles()
    }

    /** This method will fetch the planets data. */
    fun getPlanets() {
        viewModelScope.launch {
            val response: List<Planets> = repo.getPlanets()
            planetsLiveData.value = response
        }
    }

    /** This method will fetch the vehicles data. */
    fun getVehicles() {
        viewModelScope.launch {
            val response: List<Vehicles> = repo.getVehicles()
            vehiclesLiveData.value = response
        }
    }

    /** This method will fetch the token data. */
    fun getToken() {
        viewModelScope.launch {
            val response: Token = repo.getToken()
            tokenLiveData.value = response
        }
    }


    /** This method will fetch the falcon result data. */
    fun getFalconResult(
        requestBody: RequestBody
    ) {
        viewModelScope.launch {
            val response: Result = repo.getFalconResult(requestBody)
            resultLiveData.value = response
        }
    }
}
