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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                Scaffold(
                    topBar = { MyTopBar(context = this) },
                ) {
                    FlagsList()
//                    Surface(
//                        modifier = Modifier.fillMaxSize(),
//                        color = MaterialTheme.colors.background
//                    ) {
//                        FlagsList()
//                    }
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
private fun MyTopBar(context: Context) {
    TopAppBar(
        title = { Text(text = "FlagBoard") },
        navigationIcon = {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun FlagsList() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items = FlagBoard.featureFlags) {
            when (it) {
                is FeatureFlag.BooleanFlag -> RowBoolean(param = it.param)
                is FeatureFlag.IntFlag -> RowBoolean(param = it.param)
                is FeatureFlag.JsonFlag -> RowBoolean(param = it.param)
                is FeatureFlag.StringFlag -> RowBoolean(param = it.param)
                is FeatureFlag.UnknownFlag -> RowBoolean(param = it.param)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
private fun RowBoolean(param: Param<*>) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(text = param.key.value, modifier = Modifier
            .clickable { mToast(context, "${param.value}") }
            .padding(8.dp))
        if ((param.value as? Boolean) != null) {
            val checkedState = remember { mutableStateOf(param.value) }
            Switch(modifier = Modifier.padding(0.dp), checked = checkedState.value, onCheckedChange
            = {
                checkedState.value = it
            })
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
