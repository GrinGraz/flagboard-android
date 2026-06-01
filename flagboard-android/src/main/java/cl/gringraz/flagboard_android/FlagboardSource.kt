package cl.gringraz.flagboard_android

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

sealed class FlagboardSource {
    object Local : FlagboardSource()
    data class Firebase(
        val remoteConfig: FirebaseRemoteConfig,
        val cacheExpirationSeconds: Long = 3600L,
    ) : FlagboardSource()
}
