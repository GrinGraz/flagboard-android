package cl.gringraz.flagboard_android.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import cl.gringraz.flagboard_android.FlagboardSource
import cl.gringraz.flagboard_android.presentation.FlagboardInternal
import cl.gringraz.flagboard_android.ui.theme.FlagboardTheme
import cl.gringraz.flagboard_android.util.log
import kotlinx.coroutines.launch

internal class FlagboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            val activeSource = remember { mutableStateOf(FlagboardInternal.currentSource) }
            val isLoading = remember { mutableStateOf(false) }
            val flags = remember { mutableStateOf(FlagboardInternal.getFlags()) }
            val coroutineScope = rememberCoroutineScope()

            fun switchSource(source: FlagboardSource) {
                coroutineScope.launch {
                    isLoading.value = true
                    try {
                        when (source) {
                            is FlagboardSource.Firebase -> FlagboardInternal.activateRemoteSource()
                            is FlagboardSource.Local    -> FlagboardInternal.activateLocalSource()
                        }
                        activeSource.value = source
                        flags.value = FlagboardInternal.getFlags()
                    } catch (e: Exception) {
                        log("Source switch failed: ${e.message}")
                    } finally {
                        isLoading.value = false
                    }
                }
            }

            FlagboardTheme {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            context = this,
                            showRemoteTab = FlagboardInternal.isRemoteConfigured,
                            activeSource = activeSource.value,
                            isLoading = isLoading.value,
                            onSourceSelected = ::switchSource,
                        )
                    },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        Column {
                            SearchView(textState)
                            FlagList(textState = textState, flags = flags.value)
                        }
                    }
                }
            }
        }
    }

    internal companion object {
        fun openFlagBoard(context: Context?) {
            if (context != null) {
                val intent = Intent(context, FlagboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }
}
