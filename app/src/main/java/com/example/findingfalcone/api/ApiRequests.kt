package com.example.findingfalcone.api

import com.example.findingfalcone.models.Result
import com.example.findingfalcone.models.Planets
import com.example.findingfalcone.models.RequestBody
import com.example.findingfalcone.models.Token
import com.example.findingfalcone.models.Vehicles
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiRequests {

    @GET("planets")
    suspend fun getPlanets(): List<Planets>

    @GET("vehicles")
    suspend fun getVehicles(): List<Vehicles>

    @Headers("Accept: application/json")
    @POST("token")
    suspend fun getToken(): Token

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("find")
    suspend fun findFalcon(@Body requestBody: RequestBody): Result
}