package com.utama.i_mo.model

import java.text.NumberFormat
import java.util.Locale

/**
 * Barang.kt
 * Data class representasi 1 baris tabel `barang` di MySQL
 * Digunakan sebagai model lokal di seluruh aplikasi
 */
data class Barang(
    val id          : Int    = 0,
    val namaBarang  : String = "",
    val kategori    : String = "",
    val stok        : Int    = 0,
    val harga       : Int    = 0,
    val deskripsi   : String = ""
) {
    fun hargaFormatted(): String {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp ${format.format(harga)}"
    }

    fun statusStok(): StokStatus {
        return when {
            stok == 0  -> StokStatus.HABIS
            stok <= 5  -> StokStatus.MENIPIS
            else       -> StokStatus.TERSEDIA
        }
    }
}

enum class StokStatus {
    TERSEDIA,
    MENIPIS,
    HABIS
}