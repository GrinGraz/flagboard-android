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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    FlagsList()
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

@Composable
private fun FlagsList() {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(items = FlagBoard.featureFlags) {
            when (it) {
                is FeatureFlag.BooleanFlag -> ItemRow(param = it.param)
                is FeatureFlag.IntFlag     -> ItemRow(param = it.param) { Toast.makeText(
                    context, "${it.param.value}", Toast.LENGTH_SHORT).show() }
                is FeatureFlag.JsonFlag    -> ItemRow(param = it.param) { Toast.makeText(
                    context, "${it.param.value}", Toast.LENGTH_SHORT).show() }
                is FeatureFlag.StringFlag  -> ItemRow(param = it.param) { Toast.makeText(
                    context, it.param.value, Toast.LENGTH_SHORT).show() }
                is FeatureFlag.UnknownFlag -> ItemRow(param = it.param) { Toast.makeText(
                    context, "${it.param.value}", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlagboardTheme {
        FlagBoard.init(featureFlagsMap =
            mapOf(
                "Boolean flag" to true, "String flag" to "hello", "Int flag"
                        to 1, "Json flag" to "{\"key\":\"value\"}", "Unknown flag" to 0.0F
            )
        )
        FlagsList()
    }
}
