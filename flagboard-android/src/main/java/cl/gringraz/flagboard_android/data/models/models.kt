package cl.gringraz.flagboard_android.data.models

import org.json.JSONObject

@JvmInline
internal value class Key(val value: String)

internal data class Param<T>(val key: Key, val value: T)

internal sealed interface FeatureFlag {
    @JvmInline
    value class IntFlag(val param: Param<Int>) : FeatureFlag

    @JvmInline
    value class JsonFlag(val param: Param<Any>) : FeatureFlag

    @JvmInline
    value class StringFlag(val param: Param<String>) : FeatureFlag

    @JvmInline
    value class BooleanFlag(val param: Param<Boolean>) : FeatureFlag

    @JvmInline
    value class UnknownFlag(val param: Param<Class<*>>) : FeatureFlag
}