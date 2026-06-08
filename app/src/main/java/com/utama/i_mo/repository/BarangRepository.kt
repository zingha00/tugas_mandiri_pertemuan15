package com.utama.i_mo.repository

import com.utama.i_mo.api.ApiClient
import com.utama.i_mo.model.Barang
import com.utama.i_mo.model.BarangRequest

class BarangRepository {

    private val api = ApiClient.apiService

    suspend fun getAllBarang(): List<Barang> {
        val response = api.getAllBarang()
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.status == "success") {
                return body.data?.map { it.toBarang() } ?: emptyList()
            }
            throw Exception(body?.message ?: "Gagal mengambil data")
        }
        throw Exception("HTTP Error: ${response.code()} ${response.message()}")
    }

    // Ambil dari list, filter by id — tidak perlu endpoint terpisah
    suspend fun getBarangById(id: Int): Barang {
        val list = getAllBarang()
        return list.find { it.id == id }
            ?: throw Exception("Data tidak ditemukan")
    }

    suspend fun createBarang(barang: Barang): Boolean {
        val request = BarangRequest(
            namaBarang = barang.namaBarang,
            kategori   = barang.kategori,
            stok       = barang.stok,
            harga      = barang.harga,
            deskripsi  = barang.deskripsi
        )
        val response = api.createBarang(request)
        if (response.isSuccessful) {
            return response.body()?.status == "success"
        }
        throw Exception("HTTP Error: ${response.code()} ${response.message()}")
    }

    suspend fun updateBarang(barang: Barang): Boolean {
        val request = BarangRequest(
            namaBarang = barang.namaBarang,
            kategori   = barang.kategori,
            stok       = barang.stok,
            harga      = barang.harga,
            deskripsi  = barang.deskripsi
        )
        val response = api.updateBarang(barang.id, request)
        if (response.isSuccessful) {
            return response.body()?.status == "success"
        }
        throw Exception("HTTP Error: ${response.code()} ${response.message()}")
    }

    suspend fun deleteBarang(id: Int): Boolean {
        val response = api.deleteBarang(id)
        if (response.isSuccessful) {
            return response.body()?.status == "success"
        }
        throw Exception("HTTP Error: ${response.code()} ${response.message()}")
    }
}