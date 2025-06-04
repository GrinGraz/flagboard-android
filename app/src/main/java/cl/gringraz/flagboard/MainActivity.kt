package cl.gringraz.flagboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.gringraz.flagboard.ui.theme.FlagboardTheme
import cl.gringraz.flagboard_android.Flagboard
import cl.gringraz.flagboard_android.data.ConflictStrategy


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlagboardTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Dashboard()
                }
            }
        }
    }
}

@Composable
fun Dashboard() {
    val context = LocalContext.current
    Flagboard.init(context)

    val map = mapOf(
        "Boolean flag1" to true,
        "Boolean flag2" to false,
        "Boolean flag3" to true,
        "Boolean flag4" to true,
        "Boolean flag5" to false,
        "Boolean flag6" to true,
        "Boolean flag7" to true,
        "Boolean flag8" to false,
        "Boolean flag9" to true,
        "Boolean flag10" to true,
        "Boolean flag11" to false,
        "Boolean flag12" to true,
        "Boolean flag13" to false,
        "Boolean flag14" to true,
        "Boolean flag15" to true,
        "Boolean flag16" to false,
        "Boolean flag17" to true,
        "String flag1" to "hello",
        "Int flag1" to 1,
        "Float flag1" to 1.5F,
        "Double flag1" to 5.0,
        "Json flag1" to "{\"key\":\"value\"}",
        "Json flag2" to "[{\"key\":\"value\"}]",
        "Json flag3" to "{\"key\"\"value\"}"
    )

    Flagboard.loadFlags(map, ConflictStrategy.Replace)

    val textModifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            Flagboard.open(context)
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Click to test feature flag loading", modifier = textModifier)

        Flagboard.getAll().entries.forEach { entry ->
            Text(text = "${entry.key}: ${entry.value}", modifier = textModifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlagboardTheme {
        Dashboard()
    }
}
