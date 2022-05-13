package cl.gringraz.flagboard_android.di

import android.content.Context
import cl.gringraz.flagboard_android.data.LocalDataSource
import cl.gringraz.flagboard_android.data.Repository

internal class FlagboardContainer(context: Context) {
    private val sharedPreferences by lazy { context.getSharedPreferences("flagboard", Context.MODE_PRIVATE) }
    private val localDataSource by lazy { LocalDataSource(sharedPreferences) }
    internal val repository by lazy { Repository(localDataSource) }
}
