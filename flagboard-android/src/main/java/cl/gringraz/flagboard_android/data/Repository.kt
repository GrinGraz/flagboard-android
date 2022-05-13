package cl.gringraz.flagboard_android.data

import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.data.models.Key
import cl.gringraz.flagboard_android.data.models.Param
import org.json.JSONObject

enum class ConflictStrategy {
    Replace,
    Keep,
}

internal class Repository(private val localDataSource: DataSource) {

    internal fun save(featureFlag: Map<String, Any>, conflictStrategy: ConflictStrategy) {
        when (conflictStrategy) {
            ConflictStrategy.Keep    -> {
                if (fetchAll().isNullOrEmpty()) localDataSource.save(featureFlag)
            }
            ConflictStrategy.Replace -> {
                localDataSource.save(featureFlag)
            }
        }
    }

    internal fun fetchAll(): List<FeatureFlag> = parseToFeatureFlags(localDataSource.fetchAll())

    internal fun getRawFlags() = localDataSource.fetchAll()

    internal fun getBoolean(key: String): Boolean = localDataSource.getBoolean(key)

    internal fun getString(key: String): String = localDataSource.getString(key)

    internal fun getLong(key: String): Long = localDataSource.getLong(key)

    internal fun getInt(key: String): Int = localDataSource.getInt(key)

    private fun parseToFeatureFlags(featureFlagsMap: Map<String, *>): List<FeatureFlag> =
        featureFlagsMap.map { entry ->
            when (val value = entry.value) {
                is Int     ->
                    FeatureFlag.IntFlag(Param(key = Key(value = entry.key), value = value))
                is String  ->
                    getStringType(Param(key = Key(value = entry.key), value = value))
                is Boolean ->
                    FeatureFlag.BooleanFlag(Param(key = Key(value = entry.key), value = value))
                else       ->
                    FeatureFlag.UnknownFlag(Param(key = Key(value = entry.key),
                        value = entry.value!!::class.java))
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

    fun save(key: String, value: Any) = localDataSource.save(key, value)
}
