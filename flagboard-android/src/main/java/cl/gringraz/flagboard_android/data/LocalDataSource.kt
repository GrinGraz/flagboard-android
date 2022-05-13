package cl.gringraz.flagboard_android.data

import android.content.SharedPreferences
import cl.gringraz.flagboard_android.util.log

internal interface DataSource {
    fun save(ffs: Map<String, Any>)
    fun fetchAll(): MutableMap<String, *>
    fun getBoolean(key: String): Boolean
    fun getString(key: String): String
    fun getLong(key: String): Long
    fun getInt(key: String): Int
    fun save(key: String, value: Any)
}

internal class LocalDataSource(private val sharedPreferences: SharedPreferences) : DataSource {
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    override fun save(ffs: Map<String, Any>) {
        ffs.entries.forEach { entry ->
            when (entry.value) {
                is Boolean -> editor.putBoolean(entry.key, entry.value as Boolean)
                is Int     -> editor.putInt(entry.key, entry.value as Int)
                is Long    -> editor.putLong(entry.key, entry.value as Long)
                is String  -> editor.putString(entry.key, entry.value as String)
                else       -> log("Unsupported data type")
            }
        }
        editor.apply()
    }

    override fun save(key: String, value: Any) = when (value) {
        is Boolean -> { editor.putBoolean(key, value).apply() }
        is String  -> { editor.putString(key, value).apply() }
        is Int     -> { editor.putInt(key, value).apply() }
        is Long    -> { editor.putLong(key, value).apply() }
        else       -> { log("Unsupported type") }
    }

    override fun fetchAll(): MutableMap<String, *> = sharedPreferences.all

    override fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    override fun getString(key: String): String = sharedPreferences.getString(key, "")!!

    override fun getLong(key: String): Long = sharedPreferences.getLong(key, -1)

    override fun getInt(key: String): Int = sharedPreferences.getInt(key, -1)
}