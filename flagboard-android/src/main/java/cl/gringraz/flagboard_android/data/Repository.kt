package cl.gringraz.flagboard_android.data

import cl.gringraz.flagboard_android.data.models.FBDataError
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.data.models.Key
import cl.gringraz.flagboard_android.data.models.Param
import cl.gringraz.flagboard_android.util.Either
import cl.gringraz.flagboard_android.util.log
import org.json.JSONArray
import org.json.JSONObject

enum class ConflictStrategy {
    Replace,
    Keep,
}

internal class Repository(private val localDataSource: DataSource) {

    internal fun save(featureFlag: Map<String, Any>, conflictStrategy: ConflictStrategy) {
        when (conflictStrategy) {
            ConflictStrategy.Keep    -> {
                if (getAll().isEmpty()) localDataSource.save(featureFlag)
            }
            ConflictStrategy.Replace -> {
                localDataSource.save(featureFlag)
            }
        }
    }

    internal fun getAll(): List<FeatureFlag> =
        parseToFeatureFlags(localDataSource.getAll().getSuccessOrNull()) ?: emptyList()

    internal fun getRawFlags(): Either<FBDataError, Map<String, *>> = localDataSource.getAll()

    internal fun getInt(key: String): Either<FBDataError, Int> = localDataSource.getIntResult(key)

    internal fun getLong(key: String): Either<FBDataError, Long> =
        localDataSource.getLongResult(key)

    internal fun getString(key: String): Either<FBDataError, String> =
        localDataSource.getStringResult(key)

    internal fun getBoolean(key: String): Either<FBDataError, Boolean> =
        localDataSource.getBooleanResult(key)

    internal fun getDouble(key: String): Either<FBDataError, Double> =
        localDataSource.getDoubleResult(key)

    private fun parseToFeatureFlags(featureFlagsMap: Map<String, *>?): List<FeatureFlag>? =
        featureFlagsMap?.map { entry ->
            when (val value = entry.value) {
                is String  ->
                    getStringType(Param(key = Key(value = entry.key), value = value))
                is Boolean ->
                    FeatureFlag.BooleanFlag(Param(key = Key(value = entry.key), value = value))
                is Number  ->
                    FeatureFlag.NumberFlag(Param(key = Key(value = entry.key), value = value))
                else       ->
                    FeatureFlag.UnknownFlag(Param(key = Key(value = entry.key),
                        value = entry.value!!::class.java))
            }
        }

    private val error = "error"

    private fun getStringType(param: Param<String>): FeatureFlag = when {
        param.value.contains("{") -> FeatureFlag.JsonFlag(
            param = Param(
                key = param.key,
                value = try {
                    JSONObject(param.value)
                } catch (e: Exception) {
                    try {
                        JSONArray(param.value)
                    } catch (e: Exception) {
                        log("key: \"${param.key.value}\" exception: $e")
                        JSONObject("{\"$error\": \"${e.javaClass.canonicalName}\"}")
                    }
                }
            )
        )
        else                      -> FeatureFlag.StringFlag(param = param)
    }

    fun save(key: String, value: Any) = localDataSource.save(key, value)

    fun clear() = localDataSource.clear()
}
