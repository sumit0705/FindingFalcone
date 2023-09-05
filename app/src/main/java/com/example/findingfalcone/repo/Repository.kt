package com.example.findingfalcone.repo

import com.example.findingfalcone.api.ApiClient
import com.example.findingfalcone.models.Result
import com.example.findingfalcone.models.Planets
import com.example.findingfalcone.models.RequestBody
import com.example.findingfalcone.models.Token
import com.example.findingfalcone.models.Vehicles

class Repository {

    suspend fun getPlanets(): List<Planets> {
        return ApiClient.api.getPlanets()
    }

    suspend fun getVehicles(): List<Vehicles> {
        return ApiClient.api.getVehicles()
    }

    suspend fun getToken(): Token {
        return ApiClient.api.getToken()
    }

    suspend fun getFalconResult(requestBody: RequestBody): Result {
        return ApiClient.api.findFalcon(requestBody)
    }
}