package com.example.data.remote

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

@JsonClass(generateAdapter = true)
data class StkPushRequest(
    val phone: String,
    val amount: Int,
    val packageName: String,
    val link: String
)

@JsonClass(generateAdapter = true)
data class StkPushResponse(
    val ResponseCode: String? = null,
    val ResponseDescription: String? = null,
    val MerchantRequestID: String? = null,
    val CheckoutRequestID: String? = null,
    val CustomerMessage: String? = null,
    val errorMessage: String? = null,
    val message: String? = null
)

interface StkPushApi {
    @POST
    suspend fun sendStkPush(
        @Url fullEndpointUrl: String,
        @Body request: StkPushRequest
    ): Response<StkPushResponse>
}
