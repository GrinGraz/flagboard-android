package cl.gringraz.flagboard_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
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
                println("FlagBoard is either not initialized or in a inconsistent state. " +
                        "Recall FlagBoard.init(@NonNull featureFlagsMap: Map<String, Any>) function")
                FlagBoard.flagBoardState = FlagBoard.FlagBoardState.UNKNOWN
            }
        }
    }
}


@Composable
private fun Greeting() {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item { Text(text = "Feature Flag name") }
        items(items = FlagBoard.featureFlags, key = { it.hashCode() }) {
            Row {
                when (it) {
                    is FlagBoard.BooleanFlag -> Text(text = it.param.key.value, modifier = Modifier
                        .padding
                            (8.dp)
                        .clickable { mToast(context, "${it.param.value}") })
                    is FlagBoard.IntFlag -> Text(text = it.param.key.value, modifier = Modifier
                        .padding
                            (8.dp)
                        .clickable { mToast(context, "${it.param.value}") })
                    is FlagBoard.JsonFlag -> Text(text = it.param.key.value, modifier = Modifier
                        .padding
                            (8.dp)
                        .clickable { mToast(context, "${it.param.value}") })
                    is FlagBoard.StringFlag -> Text(text = it.param.key.value, modifier = Modifier
                        .padding
                            (8.dp)
                        .clickable { mToast(context, it.param.value) })
                    is FlagBoard.UnknownFlag -> Text(text = "Unknown flag type", modifier = Modifier
                        .padding
                            (8.dp)
                        .clickable { mToast(context, it.type.javaClass.name) })
                }
            }
        }
    }
}

private fun mToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlagboardTheme {
        FlagBoard.init(
            mapOf(
                "Boolean flag" to true, "String flag" to "hello", "Int flag"
                        to 1, "Json flag" to "{\"key\":\"value\"}", "Unknown flag" to 0.0F
            )
        )
    }
}
