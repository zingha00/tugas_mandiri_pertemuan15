package com.utama.i_mo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utama.i_mo.model.Barang
import com.utama.i_mo.repository.BarangRepository
import kotlinx.coroutines.launch

class BarangViewModel(private val repository: BarangRepository) : ViewModel() {

    private val _barangList = MutableLiveData<List<Barang>>()
    val barangList: LiveData<List<Barang>> = _barangList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    private val _isDone = MutableLiveData<Boolean>()
    val isDone: LiveData<Boolean> = _isDone

    init { loadAllBarang() }

    fun loadAllBarang() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _barangList.value = repository.getAllBarang()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createBarang(barang: Barang) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val berhasil = repository.createBarang(barang)
                if (berhasil) {
                    _successMessage.value = "Barang berhasil ditambahkan!"
                    _isDone.value = true
                    loadAllBarang()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambahkan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBarang(barang: Barang) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val berhasil = repository.updateBarang(barang)
                if (berhasil) {
                    _successMessage.value = "Barang berhasil diperbarui!"
                    _isDone.value = true
                    loadAllBarang()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memperbarui: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBarang(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val berhasil = repository.deleteBarang(id)
                if (berhasil) {
                    _successMessage.value = "Barang berhasil dihapus!"
                    _isDone.value = true
                    loadAllBarang()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
    fun clearSuccess() { _successMessage.value = null }
    fun clearDone() { _isDone.value = false }

    // Detail Barang
    private val _selectedBarang = MutableLiveData<Barang?>()
    val selectedBarang: LiveData<Barang?> = _selectedBarang

    fun loadBarangById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedBarang.value = repository.getBarangById(id)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat detail: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}