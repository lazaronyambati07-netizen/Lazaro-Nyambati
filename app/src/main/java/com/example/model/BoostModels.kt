package com.example.model

enum class SocialPlatform(val label: String) {
    ALL("Zote"),
    TIKTOK("TikTok"),
    INSTAGRAM("Instagram"),
    YOUTUBE("YouTube")
}

data class BoostPackage(
    val id: String,
    val name: String,
    val shortTitle: String,
    val platform: SocialPlatform,
    val amountKes: Int,
    val deliveryHint: String,
    val badge: String? = null
)

object BoostCatalog {
    val packages: List<BoostPackage> = listOf(
        BoostPackage(
            id = "tiktok_views_1000",
            name = "1,000 TikTok Views",
            shortTitle = "1,000 TikTok Views",
            platform = SocialPlatform.TIKTOK,
            amountKes = 50,
            deliveryHint = "Inaanza ndani ya dak. 2",
            badge = "Maarufu"
        ),
        BoostPackage(
            id = "ig_likes_500",
            name = "500 Instagram Likes",
            shortTitle = "500 IG Likes",
            platform = SocialPlatform.INSTAGRAM,
            amountKes = 100,
            deliveryHint = "Inaanza ndani ya dak. 5"
        ),
        BoostPackage(
            id = "tiktok_followers_1000",
            name = "1,000 TikTok Followers",
            shortTitle = "1,000 Followers",
            platform = SocialPlatform.TIKTOK,
            amountKes = 250,
            deliveryHint = "Kasi thabiti ya ukuaji",
            badge = "Thamani Bora"
        ),
        BoostPackage(
            id = "yt_views_1000",
            name = "YouTube 1,000 Views",
            shortTitle = "1,000 YT Views",
            platform = SocialPlatform.YOUTUBE,
            amountKes = 300,
            deliveryHint = "High-retention views"
        )
    )
}

sealed interface StkPushState {
    data object Idle : StkPushState
    data class Waiting(val phone: String) : StkPushState
    data class Success(
        val message: String,
        val checkoutRequestId: String? = null,
        val merchantRequestId: String? = null
    ) : StkPushState
    data class Error(
        val message: String,
        val isNetworkError: Boolean = false
    ) : StkPushState
}
