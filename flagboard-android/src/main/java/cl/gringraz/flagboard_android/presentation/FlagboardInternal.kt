package cl.gringraz.flagboard_android.presentation

import android.content.Context
import cl.gringraz.flagboard_android.FBDataState
import cl.gringraz.flagboard_android.FBState
import cl.gringraz.flagboard_android.FlagboardSource
import cl.gringraz.flagboard_android.data.ConflictStrategy
import cl.gringraz.flagboard_android.data.RemoteSource
import cl.gringraz.flagboard_android.data.Repository
import cl.gringraz.flagboard_android.data.models.FBDataError
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.di.FlagboardContainer
import cl.gringraz.flagboard_android.ui.FlagboardActivity
import cl.gringraz.flagboard_android.util.*
import cl.gringraz.flagboard_android.util.log

internal object FlagboardInternal {

    private var state: FBState = FBState.Unknown
    private lateinit var repository: Repository
    private lateinit var container: FlagboardContainer
    private var remoteSource: RemoteSource? = null
    internal var configuredSource: FlagboardSource = FlagboardSource.Local
        private set
    private var lastLocalFlags: Map<String, Any> = emptyMap()
    private val defaultInt by lazy { -1 }
    private val defaultString by lazy { "" }

    internal val isRemoteConfigured: Boolean get() = remoteSource != null

    internal var currentSource: FlagboardSource = FlagboardSource.Local
        private set

    internal fun getState() = state

    internal fun init(context: Context, source: FlagboardSource = FlagboardSource.Local) {
        log(initializingStateMessage)
        container = FlagboardContainer(context, source)
        repository = container.repository
        remoteSource = container.remoteSource
        configuredSource = source
        currentSource = FlagboardSource.Local
        state = FBState.Initialized(FBDataState.FF_NOT_LOADED)
        log(initializedStateMessage)
    }

    internal fun open(context: Context) = when (val safeState = state) {
        is FBState.Initialized -> {
            if (safeState.ffLoaded == FBDataState.FF_NOT_LOADED) {
                log(initializedWithoutDataStateMessage)
            } else {
                FlagboardActivity.openFlagBoard(context)
            }
        }
        is FBState.Unknown -> log(unknownStateMessage)
    }

    internal fun loadFlags(featureFlagsMap: Map<String, Any>, conflictStrategy: ConflictStrategy) {
        when (state) {
            is FBState.Initialized -> {
                lastLocalFlags = featureFlagsMap
                repository.save(featureFlagsMap, conflictStrategy)
                state = FBState.Initialized(FBDataState.FF_LOADED)
                log("$flagsLoadedAndStrategyMessage ${conflictStrategy.name}.")
            }
            is FBState.Unknown -> log(unknownStateMessage)
        }
    }

    internal suspend fun activateRemoteSource() {
        val remote = remoteSource ?: run { log(remoteNotConfiguredMessage); return }
        state = FBState.Initialized(FBDataState.FF_LOADING)
        log(remoteLoadStartedMessage)
        val remoteFlags = remote.fetchAndActivate()
        repository.replaceAll(remoteFlags)
        currentSource = configuredSource
        state = FBState.Initialized(FBDataState.FF_LOADED)
        log(remoteLoadSuccessMessage)
    }

    internal fun activateLocalSource() {
        if (lastLocalFlags.isEmpty()) { log("No local flags to restore."); return }
        repository.replaceAll(lastLocalFlags)
        currentSource = FlagboardSource.Local
        state = FBState.Initialized(FBDataState.FF_LOADED)
        log("Local source activated.")
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
            logError(key, error, defaultInt)
        }, { value ->
            value
        }
    )

    internal fun getLong(key: String): Long = repository.getLong(key).fold(
        { error ->
            logError(key, error, defaultInt.toLong())
        }, { value ->
            value
        }
    )

    internal fun getString(key: String): String = repository.getString(key).fold(
        { error ->
            logError(key, error, defaultString)
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

    internal fun getDouble(key: String): Double = repository.getDouble(key).fold(
        { error ->
            logError(key, error, -1.0)
        }, { value ->
            value
        }
    )

    internal fun save(key: String, value: Any) = repository.save(key, value)

    internal fun reset() {
        repository.clear()
        lastLocalFlags = emptyMap()
        currentSource = FlagboardSource.Local
        state = FBState.Initialized(FBDataState.FF_NOT_LOADED)
    }

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
