package cl.gringraz.flagboard_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.gringraz.flagboard_android.ui.theme.FlagboardTheme
import cl.gringraz.flagboard_android.ui.theme.Purple200

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlagboardTheme {
        val map = mapOf("Boolean flag Boolean flag Boolean flag Boolean flag" to true,
            "String flag" to "hello", "Int" +
                    " flag"
                    to 1, "Json flag" to "{\"key\":\"value\"}")
        FlagBoard.thisFeatureFlagsMap = map.toMutableMap()
        FlagList()
    }
}
