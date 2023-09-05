package com.example.findingfalcone.models

data class RequestBody(
    val token: String,
    val planet_names: List<String>,
    val vehicle_names: List<String>
)