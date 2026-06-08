package com.utama.i_mo.api

import com.utama.i_mo.model.ApiResponse
import com.utama.i_mo.model.BarangApiModel
import com.utama.i_mo.model.BarangRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("read.php")
    suspend fun getAllBarang(): Response<ApiResponse<List<BarangApiModel>>>

    @GET("read.php")
    suspend fun getBarangById(
        @Query("id") id: Int
    ): Response<ApiResponse<BarangApiModel>>

    @POST("create.php")
    suspend fun createBarang(
        @Body request: BarangRequest
    ): Response<ApiResponse<Map<String, Int>>>

    @PUT("update.php")
    suspend fun updateBarang(
        @Query("id") id: Int,
        @Body request: BarangRequest
    ): Response<ApiResponse<Unit>>

    @DELETE("delete.php")
    suspend fun deleteBarang(
        @Query("id") id: Int
    ): Response<ApiResponse<Unit>>
}