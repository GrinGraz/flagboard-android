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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import cl.gringraz.flagboard_android.ui.theme.FlagboardTheme

internal class FlagboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val textState = remember { mutableStateOf(TextFieldValue("")) }
            FlagboardTheme {
                Scaffold(
                    topBar = { 
                        AppTopBar(context = this)
                             },
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        Column {
                            SearchView(textState)
                            FlagList(textState)
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
                context.startActivity(intent, null)
            }
        }
    }
}
