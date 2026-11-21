package com.example.data

import com.example.data.local.BoostOrderDao
import com.example.data.local.BoostOrderEntity
import com.example.data.local.SettingsDataStore
import com.example.data.remote.StkPushApi
import com.example.data.remote.StkPushNetworkClient
import com.example.data.remote.StkPushRequest
import com.example.model.BoostPackage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException

sealed interface StkSubmissionOutcome {
    data class Success(
        val message: String,
        val checkoutRequestId: String?,
        val merchantRequestId: String?
    ) : StkSubmissionOutcome

    data class Failure(
        val message: String,
        val isNetworkError: Boolean
    ) : StkSubmissionOutcome
}

class BoostRepository(
    private val orderDao: BoostOrderDao,
    private val settingsDataStore: SettingsDataStore,
    private val api: StkPushApi = StkPushNetworkClient.api
) {
    val orders: Flow<List<BoostOrderEntity>> = orderDao.observeAllOrders()
    val serverBaseUrl: Flow<String> = settingsDataStore.serverBaseUrl
    val savedPhone: Flow<String> = settingsDataStore.savedPhone

    suspend fun saveServerBaseUrl(url: String) {
        settingsDataStore.saveServerBaseUrl(url)
    }

    suspend fun deleteOrder(orderId: Long) {
        orderDao.deleteOrderById(orderId)
    }

    suspend fun clearAllOrders() {
        orderDao.clearAllOrders()
    }

    suspend fun submitStkPush(
        selectedPackage: BoostPackage,
        targetLink: String,
        phone: String
    ): StkSubmissionOutcome {
        val cleanedPhone = BoostValidator.sanitizePhoneInput(phone)
        val cleanedLink = targetLink.trim()
        settingsDataStore.savePhone(cleanedPhone)

        val baseUrl = settingsDataStore.serverBaseUrl.first()
        val endpointUrl = BoostValidator.buildStkEndpointUrl(baseUrl)

        return try {
            val response = api.sendStkPush(
                fullEndpointUrl = endpointUrl,
                request = StkPushRequest(
                    phone = cleanedPhone,
                    amount = selectedPackage.amountKes,
                    packageName = selectedPackage.name,
                    link = cleanedLink
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.ResponseCode == "0") {
                    val successMsg = "Prompt imetumwa! Weka M-Pesa PIN yako kukamilisha malipo na boost itaanza mara moja."
                    orderDao.insertOrder(
                        BoostOrderEntity(
                            packageId = selectedPackage.id,
                            packageName = selectedPackage.name,
                            platformName = selectedPackage.platform.label,
                            amountKes = selectedPackage.amountKes,
                            targetLink = cleanedLink,
                            phone = cleanedPhone,
                            status = "PROMPT_SENT",
                            checkoutRequestId = body.CheckoutRequestID,
                            merchantRequestId = body.MerchantRequestID,
                            responseMessage = body.CustomerMessage ?: body.ResponseDescription ?: successMsg
                        )
                    )
                    StkSubmissionOutcome.Success(
                        message = successMsg,
                        checkoutRequestId = body.CheckoutRequestID,
                        merchantRequestId = body.MerchantRequestID
                    )
                } else {
                    val errMsg = body?.errorMessage
                        ?: body?.message
                        ?: body?.ResponseDescription
                        ?: "Imeshindikana kutuma prompt."
                    logFailedOrder(selectedPackage, cleanedLink, cleanedPhone, errMsg)
                    StkSubmissionOutcome.Failure(message = errMsg, isNetworkError = false)
                }
            } else {
                val errorJson = response.errorBody()?.string()
                val parsed = StkPushNetworkClient.parseErrorResponse(errorJson)
                val errMsg = parsed?.errorMessage
                    ?: parsed?.message
                    ?: "Server error (${response.code()}): Imeshindikana kutuma prompt."
                logFailedOrder(selectedPackage, cleanedLink, cleanedPhone, errMsg)
                StkSubmissionOutcome.Failure(message = errMsg, isNetworkError = false)
            }
        } catch (e: IOException) {
            val errMsg = e.localizedMessage ?: "Imeshindikana kuwasiliana na server."
            logFailedOrder(selectedPackage, cleanedLink, cleanedPhone, errMsg)
            StkSubmissionOutcome.Failure(message = errMsg, isNetworkError = true)
        } catch (e: Exception) {
            val errMsg = e.localizedMessage ?: "Hitilafu isiyotarajiwa imetokea."
            logFailedOrder(selectedPackage, cleanedLink, cleanedPhone, errMsg)
            StkSubmissionOutcome.Failure(message = errMsg, isNetworkError = false)
        }
    }

    private suspend fun logFailedOrder(
        selectedPackage: BoostPackage,
        targetLink: String,
        phone: String,
        errorMessage: String
    ) {
        orderDao.insertOrder(
            BoostOrderEntity(
                packageId = selectedPackage.id,
                packageName = selectedPackage.name,
                platformName = selectedPackage.platform.label,
                amountKes = selectedPackage.amountKes,
                targetLink = targetLink,
                phone = phone,
                status = "FAILED",
                responseMessage = errorMessage
            )
        )
    }
}
