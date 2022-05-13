package cl.gringraz.flagboard_android.presentation

import android.content.Context
import cl.gringraz.flagboard_android.data.ConflictStrategy
import cl.gringraz.flagboard_android.data.Repository
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.di.FlagboardContainer
import cl.gringraz.flagboard_android.ui.FlagboardActivity
import cl.gringraz.flagboard_android.util.*
import cl.gringraz.flagboard_android.util.log

object FlagboardInternal {

    internal sealed class FBState {
        internal object Unknown : FBState()
        internal data class Initialized(val ffLoaded: FBDataState) : FBState()
    }

    internal enum class FBDataState {
        FF_LOADED,
        FF_NOT_LOADED,
    }

    internal val state: FBState
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
        val flags = repository.fetchAll()
        log("$flagsCountMessage ${flags.size}.")
        return flags
    }

    internal fun save(key: String, value: Any) = repository.save(key, value)

    internal fun getRawFlags() = repository.getRawFlags()

    internal fun getBoolean(key: String): Boolean = repository.getBoolean(key)

    internal fun getString(key: String): String = repository.getString(key)

    internal fun getLong(key: String): Long = repository.getLong(key)

    internal fun getInt(key: String): Int = repository.getInt(key)
}
