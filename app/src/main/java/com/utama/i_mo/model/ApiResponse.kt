package com.utama.i_mo.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status") val status  : String,
    @SerializedName("message") val message : String? = null,
    @SerializedName("data")    val data    : T?
)

data class BarangApiModel(
    @SerializedName("id")          val id         : Int,
    @SerializedName("nama_barang") val namaBarang : String,
    @SerializedName("kategori")    val kategori   : String,
    @SerializedName("stok")        val stok       : Int,
    @SerializedName("harga")       val harga      : Int,
    @SerializedName("deskripsi")   val deskripsi  : String?
) {
    fun toBarang(): Barang = Barang(
        id         = id,
        namaBarang = namaBarang,
        kategori   = kategori,
        stok       = stok,
        harga      = harga,
        deskripsi  = deskripsi ?: ""
    )
}

data class BarangRequest(
    @SerializedName("nama_barang") val namaBarang : String,
    @SerializedName("kategori")    val kategori   : String,
    @SerializedName("stok")        val stok       : Int,
    @SerializedName("harga")       val harga      : Int,
    @SerializedName("deskripsi")   val deskripsi  : String = ""
)