package cl.gringraz.flagboard_android

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.json.JSONObject

@Composable
fun MyTopBar(context: Context) {
    TopAppBar(
        title = { Text(text = "FlagBoard") },
        navigationIcon = {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Suppress("UNCHECKED_CAST")
@Composable
internal fun ItemRow(modifier: Modifier = Modifier, param: Param<*>, onRowClick: () -> Unit = {}) {
    val rowModifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onRowClick)
    Row(modifier = rowModifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = getIcon(param)), modifier = Modifier.padding(8.dp), contentDescription = null)
        Text(text = param.key.value, modifier = Modifier
            .weight(1f)
            .padding(start = 0.dp, top = 8.dp, bottom = 8.dp))
        if ((param.value as? Boolean) != null) {
            val checkedState = remember { mutableStateOf(param.value) }
            Switch(checked = checkedState.value, onCheckedChange
            = {
                checkedState.value = it
                FlagBoard.thisFeatureFlagsMap?.set(param.key.value, it)
            })
        }
    }
}

internal fun getIcon(param: Param<*>): Int {
    return when(param.value) {
        is Boolean -> R.drawable.ic_resource_boolean
        is String -> R.drawable.ic_abc
        is Int -> R.drawable.ic_number
        is JSONObject -> R.drawable.ic_json
        else -> R.drawable.ic_number
    }
}

private fun mToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun RowBooleanPreview() {
    val context = LocalContext.current
    ItemRow(param = Param(Key(value = "This is a feature flag key"), "value"), onRowClick = {
        mToast(context, "Show value")
    })
}

@Preview(showBackground = true)
@Composable
fun RowBooleanPreview2() {
    ItemRow(param = Param(Key(value = "This is a feature flag keyThis is a feature flag keyThis is a feature flag key"), true))
}