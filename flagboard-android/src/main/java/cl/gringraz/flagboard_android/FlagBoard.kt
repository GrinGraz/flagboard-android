package cl.gringraz.flagboard_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.data.models.Key
import cl.gringraz.flagboard_android.data.models.Param
import org.json.JSONObject
import java.lang.ref.WeakReference

object FlagBoard {

    internal enum class FlagBoardState {
        INITIALIZED,
        NOT_INITIALIZED,
        UNKNOWN,
    }

    internal var flagBoardState: FlagBoardState = FlagBoardState.NOT_INITIALIZED
    /*internal var env: Env = Env.LOCAL*/

    internal var featureFlags: List<FeatureFlag> = emptyList()
    internal var thisFeatureFlagsMap: MutableMap<String, Any>? = null
    private lateinit var ctxReference: WeakReference<Context>

    fun init(context: Context) {
        this.ctxReference = WeakReference(context)
    }

    fun loadFlags(/*env: Env = Env.LOCAL, */@NonNull featureFlagsMap: Map<String, Any>): FlagBoard {
        println("FlagBoard is initializing")
        if (flagBoardState == FlagBoardState.INITIALIZED) {
            println("FlagBoard was already initialized. Previous parameters will be " +
                    "overwritten")
        }
        flagBoardState = FlagBoardState.INITIALIZED
//        this.env = env
        thisFeatureFlagsMap = featureFlagsMap.toMutableMap()
        featureFlags = parseToFeatureFlags(featureFlagsMap)
        ctxReference.get()?.let { loadData(context = it) } ?: println("FlagBoard not load flags")
        println("FlagBoard is initialized")
        return this
    }

//    fun changeEnv(env: Env): FlagBoard {
//        this.env = env
//        when (this.env) {
//            Env.LOCAL  -> {
//
//            }
//            Env.REMOTE -> {
//
//            }
//        }
//        return this
//    }

    fun getFeatureFlag(key: String) = thisFeatureFlagsMap?.get(key)

    fun open(@NonNull context: Context) {
        //loadData(context)
        when (flagBoardState) {
            FlagBoardState.INITIALIZED     -> {
                FlagBoardActivity.openFlagBoard(context)
            }
            FlagBoardState.NOT_INITIALIZED -> println("FlagBoard is not initialized. Before opening" +
                    " call the FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function")
            FlagBoardState.UNKNOWN         -> println("FlagBoard is a inconsistent state. Recall the " +
                    "FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function")
        }
    }

    internal fun parseToFeatureFlags(featureFlagsMap: Map<String, Any>): List<FeatureFlag> =
        featureFlagsMap.map { entry ->
            when (val value = entry.value) {
                is Int     -> FeatureFlag.IntFlag(Param(key = Key(value = entry.key),
                    value = value))
                is String  -> getStringType(Param(key = Key(value = entry.key), value = value))
                is Boolean -> FeatureFlag.BooleanFlag(Param(key = Key(value = entry.key), value =
                value))
                else       -> FeatureFlag.UnknownFlag(Param(key = Key(value = entry.key),
                    value = entry
                        .value::class.java))
            }
        }

    private fun getStringType(param: Param<String>): FeatureFlag = when {
        param.value.contains("{") -> FeatureFlag.JsonFlag(
            param = Param(
                key = param.key,
                value = try {
                    JSONObject(param.value)
                } catch (e: Exception) {
                    JSONObject("")
                }
            )
        )
        else                      -> FeatureFlag.StringFlag(param = param)
    }

    private fun loadData(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("flagboard",
            Context.MODE_PRIVATE)

        if (sharedPreferences.all.isEmpty()) {
            thisFeatureFlagsMap?.entries?.forEach { entry ->
                if (entry.value is Boolean) {
                    sharedPreferences.edit().putBoolean(entry.key, entry.value as Boolean).apply()
                }
            }
        } else {
            thisFeatureFlagsMap = sharedPreferences.all as MutableMap<String, Any>?
        }
    }
}
