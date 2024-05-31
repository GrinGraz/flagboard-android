package cl.gringraz.flagboard_android.data

import android.content.SharedPreferences
import cl.gringraz.flagboard_android.data.models.FBDataError
import cl.gringraz.flagboard_android.util.Either
import cl.gringraz.flagboard_android.util.log
import cl.gringraz.flagboard_android.util.tryToSaveUnsupportedTypeMsg
import java.lang.NullPointerException

internal interface DataSource {
    fun save(ffs: Map<String, Any>)
    fun save(key: String, value: Any)
    fun getAll(): Either<FBDataError, MutableMap<String, *>>
    fun getIntResult(key: String): Either<FBDataError, Int>
    fun getLongResult(key: String): Either<FBDataError, Long>
    fun getStringResult(key: String): Either<FBDataError, String>
    fun getBooleanResult(key: String): Either<FBDataError, Boolean>
    fun getDoubleResult(key: String): Either<FBDataError, Double>
    fun clear()
}

internal class LocalDataSource(private val sharedPreferences: SharedPreferences) : DataSource {
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val defaultInt by lazy { -1 }
    private val defaultString by lazy { "" }

    override fun save(ffs: Map<String, Any>) {
        ffs.entries.forEach { entry ->
            when (entry.value) {
                is Int     -> editor.putInt(entry.key, entry.value as Int)
                is Long    -> editor.putLong(entry.key, entry.value as Long)
                is Double  -> editor.putFloat(entry.key, entry.value as Float)
                is String  -> editor.putString(entry.key, entry.value as String)
                is Boolean -> editor.putBoolean(entry.key, entry.value as Boolean)
                else       -> log("$tryToSaveUnsupportedTypeMsg ${entry.value.javaClass} for key: ${entry.key}")
            }
        }
        editor.apply()
    }

    override fun save(key: String, value: Any) = when (value) {
        is Int     -> editor.putInt(key, value).apply()
        is Long    -> editor.putLong(key, value).apply()
        is Double  -> editor.putFloat(key, value.toFloat()).apply()
        is String  -> editor.putString(key, value).apply()
        is Boolean -> editor.putBoolean(key, value).apply()
        else       -> log("$tryToSaveUnsupportedTypeMsg ${value.javaClass} for key: $key")
    }

    override fun getAll(): Either<FBDataError, MutableMap<String, *>> = try {
        Either.Success(sharedPreferences.all)
    } catch (_: NullPointerException) {
        Either.Error(FBDataError.NoDataError)
    }

    override fun getIntResult(key: String): Either<FBDataError, Int> =
        safeGetValue(key) { sharedPreferences.getInt(key, defaultInt) }

    override fun getLongResult(key: String): Either<FBDataError, Long> =
        safeGetValue(key) { sharedPreferences.getLong(key, defaultInt.toLong()) }

    override fun getStringResult(key: String): Either<FBDataError, String> =
        safeGetValue(key) { sharedPreferences.getString(key, defaultString) ?: defaultString }

    override fun getBooleanResult(key: String): Either<FBDataError, Boolean> =
        safeGetValue(key) { sharedPreferences.getBoolean(key, false) }

    override fun getDoubleResult(key: String): Either<FBDataError, Double> =
        safeGetValue(key) { sharedPreferences.getFloat(key, 0f).toDouble() }

    override fun clear() = editor.clear().apply()

    private inline fun <T> safeGetValue(key: String, block: () -> T): Either<FBDataError, T> {
        return if (sharedPreferences.contains(key)) {
            tryGetValue(block)
        } else {
            Either.Error(FBDataError.KeyNotExistError)
        }
    }

    private inline fun <T> tryGetValue(block: () -> T): Either<FBDataError, T> {
        return try {
            Either.Success(block())
        } catch (_: ClassCastException) {
            Either.Error(FBDataError.WrongTypeError)
        }
    }
}
