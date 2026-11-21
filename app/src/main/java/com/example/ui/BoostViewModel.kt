package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BoostRepository
import com.example.data.BoostValidator
import com.example.data.StkSubmissionOutcome
import com.example.data.local.BoostDatabase
import com.example.data.local.BoostOrderEntity
import com.example.data.local.SettingsDataStore
import com.example.model.BoostCatalog
import com.example.model.BoostPackage
import com.example.model.SocialPlatform
import com.example.model.StkPushState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BoostUiState(
    val selectedPlatform: SocialPlatform = SocialPlatform.ALL,
    val packages: List<BoostPackage> = BoostCatalog.packages,
    val selectedPackage: BoostPackage = BoostCatalog.packages.first(),
    val targetLink: String = "",
    val targetLinkError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val stkPushState: StkPushState = StkPushState.Idle,
    val serverBaseUrl: String = SettingsDataStore.DEFAULT_EMULATOR_BASE_URL,
    val orders: List<BoostOrderEntity> = emptyList(),
    val selectedOrderDetail: BoostOrderEntity? = null,
    val showServerSettings: Boolean = false
)

class BoostViewModel(
    application: Application,
    private val repository: BoostRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        repository = BoostRepository(
            orderDao = BoostDatabase.getInstance(application).boostOrderDao(),
            settingsDataStore = SettingsDataStore(application)
        )
    )

    private val formState = MutableStateFlow(FormDraft())

    val uiState: StateFlow<BoostUiState> = combine(
        formState,
        repository.serverBaseUrl,
        repository.orders
    ) { draft, baseUrl, orders ->
        val filteredPackages = if (draft.selectedPlatform == SocialPlatform.ALL) {
            BoostCatalog.packages
        } else {
            BoostCatalog.packages.filter { it.platform == draft.selectedPlatform }
        }
        BoostUiState(
            selectedPlatform = draft.selectedPlatform,
            packages = filteredPackages,
            selectedPackage = draft.selectedPackage,
            targetLink = draft.targetLink,
            targetLinkError = draft.targetLinkError,
            phone = draft.phone,
            phoneError = draft.phoneError,
            stkPushState = draft.stkPushState,
            serverBaseUrl = baseUrl,
            orders = orders,
            selectedOrderDetail = draft.selectedOrderDetail,
            showServerSettings = draft.showServerSettings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BoostUiState()
    )

    init {
        viewModelScope.launch {
            val saved = repository.savedPhone.first()
            if (saved.isNotBlank()) {
                formState.update { current ->
                    if (current.phone.isBlank()) current.copy(phone = saved) else current
                }
            }
        }
    }

    fun selectPlatform(platform: SocialPlatform) {
        formState.update { current ->
            val matching = if (platform == SocialPlatform.ALL) {
                BoostCatalog.packages
            } else {
                BoostCatalog.packages.filter { it.platform == platform }
            }
            val nextSelected = if (matching.any { it.id == current.selectedPackage.id }) {
                current.selectedPackage
            } else {
                matching.firstOrNull() ?: current.selectedPackage
            }
            current.copy(
                selectedPlatform = platform,
                selectedPackage = nextSelected
            )
        }
    }

    fun selectPackage(pkg: BoostPackage) {
        formState.update { it.copy(selectedPackage = pkg) }
    }

    fun updateTargetLink(value: String) {
        formState.update {
            it.copy(
                targetLink = value,
                targetLinkError = null
            )
        }
    }

    fun updatePhone(value: String) {
        formState.update {
            it.copy(
                phone = value,
                phoneError = null
            )
        }
    }

    fun dismissStatus() {
        formState.update { it.copy(stkPushState = StkPushState.Idle) }
    }

    fun openServerSettings() {
        formState.update { it.copy(showServerSettings = true) }
    }

    fun closeServerSettings() {
        formState.update { it.copy(showServerSettings = false) }
    }

    fun saveServerUrl(url: String): Boolean {
        val trimmed = url.trim()
        if (!BoostValidator.isValidServerBaseUrl(trimmed)) {
            return false
        }
        viewModelScope.launch {
            repository.saveServerBaseUrl(trimmed)
            formState.update { it.copy(showServerSettings = false) }
        }
        return true
    }

    fun submitStkPush() {
        val current = formState.value
        if (current.stkPushState is StkPushState.Waiting) return

        val linkValid = BoostValidator.isValidTargetUrl(current.targetLink)
        val phoneValid = BoostValidator.isValidKenyanMpesaPhone(current.phone)

        if (!linkValid || !phoneValid) {
            formState.update {
                it.copy(
                    targetLinkError = if (!linkValid) "Weka link sahihi inayoanza na https:// au http://" else null,
                    phoneError = if (!phoneValid) "Weka namba sahihi ya M-Pesa (mfano 0712345678)" else null
                )
            }
            return
        }

        val cleanedPhone = BoostValidator.sanitizePhoneInput(current.phone)
        val pkg = current.selectedPackage
        val link = current.targetLink.trim()

        formState.update {
            it.copy(
                targetLinkError = null,
                phoneError = null,
                stkPushState = StkPushState.Waiting(cleanedPhone)
            )
        }

        viewModelScope.launch {
            when (val result = repository.submitStkPush(pkg, link, cleanedPhone)) {
                is StkSubmissionOutcome.Success -> {
                    formState.update {
                        it.copy(
                            stkPushState = StkPushState.Success(
                                message = result.message,
                                checkoutRequestId = result.checkoutRequestId,
                                merchantRequestId = result.merchantRequestId
                            )
                        )
                    }
                }
                is StkSubmissionOutcome.Failure -> {
                    formState.update {
                        it.copy(
                            stkPushState = StkPushState.Error(
                                message = result.message,
                                isNetworkError = result.isNetworkError
                            )
                        )
                    }
                }
            }
        }
    }

    fun reuseOrder(order: BoostOrderEntity) {
        val matchedPkg = BoostCatalog.packages.find { it.id == order.packageId }
            ?: BoostCatalog.packages.first()
        formState.update {
            it.copy(
                selectedPlatform = SocialPlatform.ALL,
                selectedPackage = matchedPkg,
                targetLink = order.targetLink,
                phone = order.phone,
                targetLinkError = null,
                phoneError = null,
                stkPushState = StkPushState.Idle,
                selectedOrderDetail = null
            )
        }
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            formState.update {
                if (it.selectedOrderDetail?.id == orderId) {
                    it.copy(selectedOrderDetail = null)
                } else {
                    it
                }
            }
        }
    }

    fun clearAllOrders() {
        viewModelScope.launch {
            repository.clearAllOrders()
            formState.update { it.copy(selectedOrderDetail = null) }
        }
    }

    fun selectOrderDetail(order: BoostOrderEntity?) {
        formState.update { it.copy(selectedOrderDetail = order) }
    }

    private data class FormDraft(
        val selectedPlatform: SocialPlatform = SocialPlatform.ALL,
        val selectedPackage: BoostPackage = BoostCatalog.packages.first(),
        val targetLink: String = "",
        val targetLinkError: String? = null,
        val phone: String = "",
        val phoneError: String? = null,
        val stkPushState: StkPushState = StkPushState.Idle,
        val selectedOrderDetail: BoostOrderEntity? = null,
        val showServerSettings: Boolean = false
    )
}
