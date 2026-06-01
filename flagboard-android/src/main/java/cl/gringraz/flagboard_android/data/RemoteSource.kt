package cl.gringraz.flagboard_android.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

internal class RemoteSource(
    private val remoteConfig: FirebaseRemoteConfig,
    private val cacheExpirationSeconds: Long,
) {
    suspend fun fetchAndActivate(): Map<String, Any> =
        suspendCancellableCoroutine { continuation ->
            remoteConfig.fetch(cacheExpirationSeconds)
                .addOnSuccessListener {
                    remoteConfig.activate().addOnCompleteListener {
                        val result = remoteConfig.all.mapValues { (_, v) ->
                            coerceRcValue(v.asString())
                        }
                        continuation.resume(result)
                    }
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }

    private fun coerceRcValue(raw: String): Any {
        if (raw.equals("true", ignoreCase = true)) return true
        if (raw.equals("false", ignoreCase = true)) return false
        raw.toLongOrNull()?.let { return it }
        raw.toDoubleOrNull()?.let { return it }
        return raw
    }
}
