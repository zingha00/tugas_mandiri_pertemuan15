package com.utama.i_mo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.utama.i_mo.repository.BarangRepository

class BarangViewModelFactory(private val repository: BarangRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BarangViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BarangViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}