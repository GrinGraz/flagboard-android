package cl.gringraz.flagboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cl.gringraz.flagboard.ui.theme.FlagboardTheme
import cl.gringraz.flagboard_android.FlagBoard


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlagboardTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val context = LocalContext.current
    val map = mapOf(
        "Boolean flag" to true,
        "Boolean flag2" to false,
        "Boolean flag3" to true,
        "String flag" to "hello",
        "Int flag" to 1,
        "Json flag" to "{\"key\":\"value\"}")
    FlagBoard.init(context = context)
    FlagBoard.loadFlags(featureFlagsMap = map)

    Text(text = "Hello $name", modifier = Modifier.clickable {
        FlagBoard.open(context = context)
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlagboardTheme {
        Greeting("Android")
    }
}
