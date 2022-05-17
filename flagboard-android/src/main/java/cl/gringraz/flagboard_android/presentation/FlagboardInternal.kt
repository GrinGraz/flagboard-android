package cl.gringraz.flagboard_android.presentation

import android.content.Context
import cl.gringraz.flagboard_android.FBDataState
import cl.gringraz.flagboard_android.FBState
import cl.gringraz.flagboard_android.data.ConflictStrategy
import cl.gringraz.flagboard_android.data.models.FBDataError
import cl.gringraz.flagboard_android.data.Repository
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.di.FlagboardContainer
import cl.gringraz.flagboard_android.ui.FlagboardActivity
import cl.gringraz.flagboard_android.util.*
import cl.gringraz.flagboard_android.util.log

internal object FlagboardInternal {

    val state: FBState
        get() = _state
    private var _state: FBState = FBState.Unknown
    private lateinit var repository: Repository
    private lateinit var container: FlagboardContainer

    internal fun init(context: Context) {
        log(initializingStateMessage)
        container = FlagboardContainer(context)
        repository = container.repository
        _state = FBState.Initialized(FBDataState.FF_NOT_LOADED)
        log(initializedStateMessage)
    }

    internal fun open(context: Context) = when (val safeState = _state) {
        is FBState.Initialized -> {
            if (safeState.ffLoaded == FBDataState.FF_LOADED) {
                FlagboardActivity.openFlagBoard(context)
            } else {
                log(initializedWithoutDataStateMessage)
            }
        }
        is FBState.Unknown     -> log(unknownStateMessage)
    }


    internal fun loadFlags(featureFlagsMap: Map<String, Any>, conflictStrategy: ConflictStrategy) {
        repository.save(featureFlagsMap, conflictStrategy)
        _state = FBState.Initialized(FBDataState.FF_LOADED)
        log("$flagsLoadedAndStrategyMessage ${conflictStrategy.name}.")
    }

    internal fun getFlags(): List<FeatureFlag> {
        val flags = repository.getAll()
        log("$flagsCountMessage ${flags.size}.")
        return flags
    }

    internal fun getRawFlags(): Map<String, *> = repository.getRawFlags().fold(
        { error ->
            logError(error = error, defaultValue = emptyMap<String, Any>())
        },
        { value ->
            value
        }
    )

    internal fun getInt(key: String): Int = repository.getInt(key).fold(
        { error ->
            logError(key, error, defaultValue = -1)
        }, { value ->
            value
        }
    )

    internal fun getLong(key: String): Long = repository.getLong(key).fold(
        { error ->
            logError(key, error, -1)
        }, { value ->
            value
        }
    )

    internal fun getString(key: String): String = repository.getString(key).fold(
        { error ->
            logError(key, error, "")
        }, { value ->
            value
        }
    )

    internal fun getBoolean(key: String): Boolean = repository.getBoolean(key).fold(
        { error ->
            logError(key, error, false)
        }, { value ->
            value
        }
    )

    internal fun save(key: String, value: Any) = repository.save(key, value)

    private inline fun <reified T> logError(
        key: String? = null,
        error: FBDataError,
        defaultValue: Any,
    ): T {
        if (key != null) {
            log("key $key throws $error. Default value was returned")
        } else {
            log("throws $error. Default value was returned")
        }
        return defaultValue as T
    }
}
