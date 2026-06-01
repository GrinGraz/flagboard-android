package cl.gringraz.flagboard_android.di

import android.content.Context
import cl.gringraz.flagboard_android.FlagboardSource
import cl.gringraz.flagboard_android.data.LocalDataSource
import cl.gringraz.flagboard_android.data.RemoteSource
import cl.gringraz.flagboard_android.data.Repository

internal class FlagboardContainer(context: Context, source: FlagboardSource = FlagboardSource.Local) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences("flagboard", Context.MODE_PRIVATE)
    }
    private val localDataSource by lazy { LocalDataSource(sharedPreferences) }

    internal val remoteSource: RemoteSource? by lazy {
        when (source) {
            is FlagboardSource.Firebase -> RemoteSource(source.remoteConfig, source.cacheExpirationSeconds)
            is FlagboardSource.Local    -> null
        }
    }

    internal val repository by lazy { Repository(localDataSource) }
}
