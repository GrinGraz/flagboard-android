package cl.gringraz.flagboard_android.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.core.content.ContextCompat
import cl.gringraz.flagboard_android.presentation.FlagboardInternal
import cl.gringraz.flagboard_android.ui.theme.FlagboardTheme

internal class FlagboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlagboardTheme {
                Scaffold(
                    topBar = { AppTopBar(context = this) },
                ) {
                    FlagList(FlagboardInternal.getFlags())
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
