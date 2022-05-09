package cl.gringraz.flagboard_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.core.content.ContextCompat
import cl.gringraz.flagboard_android.ui.theme.FlagboardTheme

internal class FlagBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlagboardTheme {
                Scaffold(
                    topBar = { MyTopBar(context = this) },
                ) {
                    FlagList()
                }
            }
        }
    }

    companion object {
        fun openFlagBoard(context: Context?) {
            if (context != null && FlagBoard.flagBoardState == FlagBoard.FlagBoardState.INITIALIZED) {
                val intent = Intent(context, FlagBoardActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            } else {
                println(
                    "FlagBoard is either not initialized or in a inconsistent state. " +
                            "Recall FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function"
                )
                FlagBoard.flagBoardState = FlagBoard.FlagBoardState.UNKNOWN
            }
        }
    }
}
