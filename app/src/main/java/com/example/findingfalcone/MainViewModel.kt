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

class MainViewModel(private val repo: Repository) : ViewModel() {

    val myPlanets: MutableLiveData<List<Planets>> = MutableLiveData()
    val myVehicles: MutableLiveData<List<Vehicles>> = MutableLiveData()
    val myToken: MutableLiveData<Token> = MutableLiveData()
    val myFalconResult: MutableLiveData<Result> = MutableLiveData()

    init {
        getPlanets()
        getVehicles()
    }

    fun getPlanets() {
        viewModelScope.launch {
            val response: List<Planets> = repo.getPlanets()
            myPlanets.value = response
        }
    }

    fun getVehicles() {
        viewModelScope.launch {
            val response: List<Vehicles> = repo.getVehicles()
            myVehicles.value = response
        }
    }

    fun getToken() {
        viewModelScope.launch {
            val response: Token = repo.getToken()
            myToken.value = response
        }
    }


    fun getFalconResult(
        requestBody: RequestBody
    ) {
        viewModelScope.launch {
            val response: Result = repo.getFalconResult(requestBody)
            myFalconResult.value = response
        }
    }
}
