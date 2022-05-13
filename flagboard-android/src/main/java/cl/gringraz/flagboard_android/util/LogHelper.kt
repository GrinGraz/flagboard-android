package cl.gringraz.flagboard_android.util

import android.util.Log

internal const val LOG_TAG = "FlagBoard"

internal fun log(message: String) {
    Log.d(LOG_TAG, message)
}

internal val unknownStateMessage by lazy { "is in a inconsistent state. Recall the FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function." }
internal val initializedStateMessage by lazy { "is initialized." }
internal val initializingStateMessage by lazy { "is initializing." }
internal val initializedWithoutDataStateMessage by lazy { "has not feature flags loaded. First call the FlagBoard.loadFlags(@NonNull featureFlagsMap: Map<String, Any>) function." }
internal val failedInitializationMessage by lazy { "initialization failed." }
internal val flagsLoadedAndStrategyMessage by lazy { "feature flag loading strategy is: " }
internal val flagsCountMessage by lazy { "number of loaded feature flags: " }