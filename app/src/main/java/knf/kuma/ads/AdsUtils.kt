package knf.kuma.ads

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.appodeal.ads.Appodeal
import com.google.android.gms.ads.AdSize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import knf.kuma.App
import knf.kuma.BuildConfig
import knf.kuma.commons.PrefsUtil
import knf.kuma.commons.diceOf
import knf.kuma.commons.noCrash
import knf.kuma.commons.noCrashLet
import knf.kuma.news.NewsObject
import knf.kuma.pojos.Achievement
import knf.kuma.pojos.FavoriteObject
import knf.kuma.pojos.RecentObject

enum class AdsType {
    RECENT_BANNER,
    RECENT_BANNER2,
    FAVORITE_BANNER,
    FAVORITE_BANNER2,
    DIRECTORY_BANNER,
    HOME_BANNER,
    HOME_BANNER2,
    EMISSION_BANNER,
    SEEING_BANNER,
    RECOMMEND_BANNER,
    QUEUE_BANNER,
    RECORD_BANNER,
    NEWS_BANNER,
    RANDOM_BANNER,
    INFO_BANNER,
    ACHIEVEMENT_BANNER,
    EXPLORER_BANNER,
    CAST_BANNER,
    REWARDED,
    INTERSTITIAL
}

object AdsUtils {
    val remoteConfigs = FirebaseRemoteConfig.getInstance().apply {
        if (BuildConfig.DEBUG)
            setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().apply { minimumFetchIntervalInSeconds = 0 }.build())
        setDefaultsAsync(mapOf(
                "admob_enabled" to true,
                "appbrains_enabled" to false,
                "startapp_enabled" to false,
                "appodeal_enabled" to false,
                "ads_forced" to false,
                "admob_percent" to 100.0,
                "appodeal_percent" to 0.0,
                "appbrains_percent" to 0.0,
                "startapp_percent" to 0.0,
                "appodeal_fullscreen_percent" to 100.0,
                "admob_fullscreen_percent" to 100.0,
                "appbrains_fullscreen_percent" to 100.0,
                "startappp_fullscreen_percent" to 100.0,
                "appodeal_fullscreen_percent" to 100.0,
                "rewarded_percent" to 90.0,
                "interstitial_percent" to 10.0,
                "disqus_version" to "9e3da5ae8d7caf8389087c4c35a6ca1b",
                "samsung_disable_foreground" to false)
        )
        fetch().addOnCompleteListener {
            it.exception?.printStackTrace()
            if (it.isSuccessful) {
                Log.e("Remote config", "Updated: ${it.isSuccessful}")
                FirebaseRemoteConfig.getInstance().activate().addOnCompleteListener {
                    Log.e("Remote config", "Activated: ${it.isSuccessful}")
                }
            }
        }
    }
}

fun Activity.preload(list: List<*>) {
    if (PrefsUtil.isAdsEnabled && list.isNotEmpty()) {
        if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
            Appodeal.cache(this, Appodeal.NATIVE, list.size / 5)
    }
}

fun MutableList<RecentObject>.implAdsRecent() {
    if (PrefsUtil.isAdsEnabled)
        noCrash {
            diceOf({ implAdsRecentBrains() }) {
                if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                    put({ implAdsRecentMob() }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
                if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                    put({ implAdsRecentBrains() }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
                if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                    put({ implAdsRecentAppOdeal() }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
            }()
        }
}

fun MutableList<FavoriteObject>.implAdsFavorite() {
    noCrash {
        diceOf({ implAdsFavoriteBrains() }) {
            if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                put({ implAdsFavoriteMob() }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                put({ implAdsFavoriteBrains() }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                put({ implAdsFavoriteAppOdeal() }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
        }()
    }
}

fun MutableList<NewsObject>.implAdsNews() {
    noCrash {
        diceOf({ implAdsNewsBrain() }) {
            if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                put({ implAdsNewsMob() }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                put({ implAdsNewsBrain() }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                put({ implAdsNewsAppOdeal() }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
        }()
    }
}

fun MutableList<Achievement>.implAdsAchievement() {
    noCrash {
        diceOf({ implAdsAchievementBrain() }) {
            if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                put({ implAdsAchievementMob() }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                put({ implAdsAchievementBrain() }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                put({ implAdsAchievementAppOdeal() }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
        }()
    }
}

fun ViewGroup.implBannerCast() {
    noCrash {
        diceOf({ implBannerCastBrains() }) {
            if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                put({ implBannerCastMob() }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                put({ implBannerCastBrains() }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                put({ implBannerCastAppOdeal(context) }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
        }()
    }
}

fun ViewGroup.implBanner(unitID: String, isSmart: Boolean = false) {
    noCrash {
        diceOf({ implBannerBrains(unitID, isSmart) }) {
            if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                put({ implBannerMob(unitID, isSmart) }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                put({ implBannerBrains(unitID, isSmart) }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                put({ implBannerAppOdeal(context, unitID, isSmart) }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
        }()
    }
}

fun ViewGroup.implBanner(unitID: AdsType, isSmart: Boolean = false) {
    noCrash {
        diceOf({ implBannerBrains(unitID, isSmart) }) {
            if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                put({ implBannerMob(unitID, isSmart) }, AdsUtils.remoteConfigs.getDouble("admob_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                put({ implBannerBrains(unitID, isSmart) }, AdsUtils.remoteConfigs.getDouble("appbrains_percent"))
            if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                put({ implBannerAppOdeal(context, unitID, isSmart) }, AdsUtils.remoteConfigs.getDouble("appodeal_percent"))
        }()
    }
}

fun getFAdLoaderRewarded(context: Activity, onUpdate: () -> Unit = {}): FullscreenAdLoader =
        noCrashLet(getFAdLoaderBrains(context, onUpdate)) {
            diceOf({ getFAdLoaderBrains(context, onUpdate) }) {
                if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                    put({ getFAdLoaderRewardedMob(context, onUpdate) }, AdsUtils.remoteConfigs.getDouble("admob_fullscreen_percent"))
                if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                    put({ getFAdLoaderRewardedAppOdeal(context, onUpdate) }, AdsUtils.remoteConfigs.getDouble("appodeal_fullscreen_percent"))
            }()
        }

fun getFAdLoaderInterstitial(context: Activity, onUpdate: () -> Unit = {}): FullscreenAdLoader =
        noCrashLet(getFAdLoaderBrains(context, onUpdate)) {
            diceOf({ getFAdLoaderBrains(context, onUpdate) }) {
                if (AdsUtils.remoteConfigs.getBoolean("admob_enabled"))
                    put({ getFAdLoaderInterstitialMob(context, onUpdate) }, AdsUtils.remoteConfigs.getDouble("admob_fullscreen_percent"))
                if (AdsUtils.remoteConfigs.getBoolean("appbrains_enabled"))
                    put({ getFAdLoaderBrains(context, onUpdate) }, AdsUtils.remoteConfigs.getDouble("appbrains_fullscreen_percent"))
                if (AdsUtils.remoteConfigs.getBoolean("appodeal_enabled"))
                    put({ getFAdLoaderInterstitialAppOdeal(context, onUpdate) }, AdsUtils.remoteConfigs.getDouble("appodeal_fullscreen_percent"))
            }()
        }

fun getAdSize(width: Float): AdSize {
    val metrics = App.context.resources.displayMetrics
    val density = metrics.density
    var adWidthPixels = width
    if (adWidthPixels == 0f) {
        adWidthPixels = metrics.widthPixels.toFloat()
    }

    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationBannerAdSizeWithWidth(App.context, adWidth)

}

interface FullscreenAdLoader {
    fun load()
    fun show()
}