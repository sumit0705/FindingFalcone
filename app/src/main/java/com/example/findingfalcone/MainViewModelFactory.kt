package com.example.findingfalcone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.findingfalcone.repo.Repository

/** Factory which is responsible to instantiate [MainViewModel]. */
class MainViewModelFactory(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}