package com.example.data

import java.net.URI

object BoostValidator {

    private val kenyaPhoneRegex = Regex("""^(?:\+?254|0)(7\d{8}|1\d{8})$""")

    fun sanitizePhoneInput(raw: String): String {
        return raw.trim().replace(" ", "").replace("-", "")
    }

    fun isValidKenyanMpesaPhone(raw: String): Boolean {
        val cleaned = sanitizePhoneInput(raw)
        return kenyaPhoneRegex.matches(cleaned)
    }

    fun isValidTargetUrl(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return false
        return runCatching {
            val uri = URI(trimmed)
            val scheme = uri.scheme?.lowercase()
            (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
        }.getOrDefault(false)
    }

    fun buildStkEndpointUrl(baseUrl: String): String {
        val cleaned = baseUrl.trim().trimEnd('/')
        if (cleaned.isEmpty()) return "http://10.0.2.2:3000/api/stkpush"
        return if (cleaned.endsWith("/api/stkpush", ignoreCase = true)) {
            cleaned
        } else {
            "$cleaned/api/stkpush"
        }
    }

    fun isValidServerBaseUrl(raw: String): Boolean {
        return isValidTargetUrl(raw.trim())
    }
}
