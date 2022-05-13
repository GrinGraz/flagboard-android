package cl.gringraz.flagboard_android

import android.content.Context
import androidx.annotation.NonNull
import cl.gringraz.flagboard_android.data.ConflictStrategy
import cl.gringraz.flagboard_android.presentation.FlagboardInternal

/**
 * Public Object that interact with the Flagboard internal API through [FlagboardInternal].
 */
object Flagboard {

    /**
     * Static function in charge of load the feature flags in Flagboard to persist them.
     *
     * @param featureFlagsMap: map containing the feature flags to be loaded.
     * @param conflictStrategy: strategy for the feature flags loading, replace or keep the previous data.
     * @return Flagboard: Returns [this] to chain functions.
     *
     * @see [ConflictStrategy]
     */
    @JvmStatic
    @JvmOverloads
    fun loadFlags(
        @NonNull featureFlagsMap: Map<String, Any>,
        conflictStrategy: ConflictStrategy = ConflictStrategy.Keep,
    ): Flagboard {
        FlagboardInternal.loadFlags(featureFlagsMap, conflictStrategy)
        return this
    }

    /**
     * Static function in charge of open the Flagboard UI.
     *
     * @param context: context to launch the Flagboard Activity.
     */
    @JvmStatic
    fun open(@NonNull context: Context) = FlagboardInternal.open(context)

    /**
     * Static function in charge of get a Int feature flag by its key.
     *
     * @param key: string key of the feature flag.
     * @return Int: value of the feature flag.
     */
    @JvmStatic
    fun getInt(key: String): Int = FlagboardInternal.getInt(key)

    /**
     * Static function in charge of get a Long feature flag by its key.
     *
     * @param key: string key of the feature flag.
     * @return Long: value of the feature flag.
     */
    @JvmStatic
    fun getLong(key: String): Long = FlagboardInternal.getLong(key)

    /**
     * Static function in charge of get a String feature flag by its key.
     *
     * @param key: string key of the feature flag.
     * @return String: value of the feature flag.
     */
    @JvmStatic
    fun getString(key: String): String = FlagboardInternal.getString(key)

    /**
     * Static function in charge of get a Boolean feature flag by its key.
     *
     * @param key: string key of the feature flag.
     * @return Boolean: value of the feature flag.
     */
    @JvmStatic
    fun getBoolean(key: String): Boolean = FlagboardInternal.getBoolean(key)

    /**
     * Static function in charge of get all the feature flags.
     *
     * @return Map<String, Any>: all the feature flags.
     */
    @JvmStatic
    fun getAll(): Map<String, *> = FlagboardInternal.getRawFlags()
}
