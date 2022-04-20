package cl.gringraz.flagboard_android

import android.content.Context
import androidx.annotation.NonNull
import org.json.JSONObject

@JvmInline
internal value class Key(val value: String)
internal data class Param<T>(val key: Key, val value: T)

internal sealed interface FeatureFlag {
    @JvmInline
    value class IntFlag(val param: Param<Int>) : FeatureFlag
    @JvmInline
    value class JsonFlag(val param: Param<JSONObject>) : FeatureFlag
    @JvmInline
    value class StringFlag(val param: Param<String>) : FeatureFlag
    @JvmInline
    value class BooleanFlag(val param: Param<Boolean>) : FeatureFlag
    @JvmInline
    value class UnknownFlag(val param: Param<Class<*>>) : FeatureFlag
}


object FlagBoard {
    internal var flagBoardState: FlagBoardState = FlagBoardState.NOT_INITIALIZED

    internal var featureFlags: List<FeatureFlag> = emptyList()
    internal var thisFeatureFlagsMap: Map<String, Any> = emptyMap()

    fun init(@NonNull featureFlagsMap: Map<String, Any>): FlagBoard? {
        if (flagBoardState == FlagBoardState.INITIALIZED) {
            println("FlagBoard was already initialized. Previous parameters will be " +
                    "overwritten")
        }
        flagBoardState = FlagBoardState.INITIALIZED
        thisFeatureFlagsMap = featureFlagsMap
        featureFlags = parseToFeatureFlags(featureFlagsMap)
        println("FlagBoard is initialized")
        return this
    }

    fun open(@NonNull context: Context) {
        when(flagBoardState) {
            FlagBoardState.INITIALIZED -> {
                FlagBoardActivity.openFlagBoard(context)
            }
            FlagBoardState.NOT_INITIALIZED -> println("FlagBoard is not initialized. Before opening" +
                    " call the FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function")
            FlagBoardState.UNKNOWN -> println("FlagBoard is a inconsistent state. Recall the " +
                    "FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function")
        }
    }

    private fun parseToFeatureFlags(featureFlagsMap: Map<String, Any>): List<FeatureFlag> =
        featureFlagsMap.map { entry ->
            when (val value = entry.value) {
                is Int -> FeatureFlag.IntFlag(Param(key = Key(value = entry.key), value = value))
                is String -> getStringType(Param(key = Key(value = entry.key), value = value))
                is Boolean -> FeatureFlag.BooleanFlag(Param(key = Key(value = entry.key), value =
                value))
                else -> FeatureFlag.UnknownFlag(Param(key = Key(value = entry.key), value = entry
                    .value::class.java))
            }
        }

    private fun getStringType(param: Param<String>): FeatureFlag = when {
        param.value.contains("{") -> FeatureFlag.JsonFlag(
            param = Param(
                key = param.key,
                value = try { JSONObject(param.value) } catch (e: Exception) { JSONObject("") }
            )
        )
        else -> FeatureFlag.StringFlag(param = param)
    }

    internal enum class FlagBoardState {
        INITIALIZED,
        NOT_INITIALIZED,
        UNKNOWN,
    }
}
