package cl.gringraz.flagboard_android

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.data.models.Param
import org.json.JSONObject

@Composable
fun MyTopBar(context: Context) {
    TopAppBar(
        backgroundColor = Color(com.google.android.material.R.color.design_default_color_primary),
        contentColor = Color.White,
        title = { Text(text = "Flagboard") },
        navigationIcon = {
            IconButton(onClick = { (context as ComponentActivity).finish() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun FlagList() {
    val context = LocalContext.current
    val items = FlagBoard.parseToFeatureFlags(FlagBoard.thisFeatureFlagsMap?.toMap()!!)
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        itemsIndexed(items = items) { index, item ->
            when (item) {
                is FeatureFlag.BooleanFlag -> ItemRow(param = item.param)
                is FeatureFlag.IntFlag     -> ItemRow(param = item.param) {
                    Toast.makeText(
                        context, "${item.param.value}", Toast.LENGTH_SHORT).show()
                }
                is FeatureFlag.JsonFlag    -> ItemRow(param = item.param) {
                    Toast.makeText(
                        context, "${item.param.value}", Toast.LENGTH_SHORT).show()
                }
                is FeatureFlag.StringFlag  -> ItemRow(param = item.param) {
                    Toast.makeText(
                        context, item.param.value, Toast.LENGTH_SHORT).show()
                }
                is FeatureFlag.UnknownFlag -> ItemRow(param = item.param) {
                    Toast.makeText(
                        context, "${item.param.value}", Toast.LENGTH_SHORT).show()
                }
            }
            if (index < items.lastIndex) Divider(color = Color.LightGray, thickness = 0.5.dp)
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
internal fun ItemRow(modifier: Modifier = Modifier, param: Param<*>, onRowClick: () -> Unit = {}) {
    val rowModifier = modifier
        .fillMaxWidth()
        .clickable(onClick = onRowClick)
        .padding(PaddingValues(vertical = 12.dp))
    Row(modifier = rowModifier, verticalAlignment = Alignment.CenterVertically) {
        AddIcon(param = param)
        Text(text = param.key.value, fontSize = 16.sp, modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp))
        if ((param.value as? Boolean) != null) {
            AddSwitch(param = param as Param<Boolean>)
        }
    }
}

@Composable
private fun AddSwitch(param: Param<Boolean>) {
    val pref = LocalContext.current.getSharedPreferences("flagboard", Context.MODE_PRIVATE)
    val checkedState = remember { mutableStateOf(param.value) }
    Switch(checked = checkedState.value, onCheckedChange
    = {
        checkedState.value = it
        pref.edit().putBoolean(param.key.value, it).apply()
        FlagBoard.thisFeatureFlagsMap?.set(param.key.value, it)
    })
}

@Composable
private fun AddIcon(param: Param<*>) {
    val icon = when (param.value) {
        is Boolean    -> R.drawable.ic_number
        is String     -> R.drawable.ic_abc
        is Int        -> R.drawable.ic_number
        is JSONObject -> R.drawable.ic_json
        else          -> R.drawable.ic_number
    }

    Icon(painter = painterResource(id = icon), contentDescription = null)
}
