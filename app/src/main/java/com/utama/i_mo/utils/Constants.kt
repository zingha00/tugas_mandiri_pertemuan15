package com.utama.i_mo.utils

/**
 * Constants.kt
 * Menyimpan semua konstanta global aplikasi I-Mo
 * Ganti BASE_URL dengan IP WiFi komputer XAMPP kamu
 */
object Constants {

    // ══════════════════════════════════════════════════════
    // BASE URL — Sesuaikan dengan IP WiFi komputer XAMPP
    // Cara cek IP: buka CMD → ketik "ipconfig"
    //              lihat bagian "IPv4 Address" di WiFi adapter
    //
    // Contoh:
    //   Emulator  → "http://10.0.2.2/14-imo/imo/"
    //   HP Fisik  → "http://192.168.x.x/14-imo/imo/"  ← Ganti ke IP Laptop kamu!
    // ══════════════════════════════════════════════════════
    const val BASE_URL = "http://10.108.244.116/14-imo/imo/"

    // ══════════════════════════════════════════════════════
    // INTENT KEYS — untuk passing data antar Activity
    // ══════════════════════════════════════════════════════
    const val KEY_BARANG_ID = "barang_id"

    // ══════════════════════════════════════════════════════
    // REQUEST CODE — untuk Activity Result
    // ══════════════════════════════════════════════════════
    const val REQUEST_ADD    = 100
    const val REQUEST_DETAIL = 101

    // ══════════════════════════════════════════════════════
    // RESULT CODE
    // ══════════════════════════════════════════════════════
    const val RESULT_ADDED   = 201
    const val RESULT_UPDATED = 202
    const val RESULT_DELETED = 203

    // ══════════════════════════════════════════════════════
    // KATEGORI — pilihan dropdown di form
    // ══════════════════════════════════════════════════════
    val KATEGORI_LIST = listOf(
        "Elektronik",
        "Aksesoris",
        "Furnitur",
        "Pakaian",
        "Makanan",
        "Lainnya"
    )
}