package cl.gringraz.flagboard_android.ui

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.gringraz.flagboard_android.FlagboardSource
import cl.gringraz.flagboard_android.R
import cl.gringraz.flagboard_android.data.models.FeatureFlag
import cl.gringraz.flagboard_android.data.models.Param
import cl.gringraz.flagboard_android.presentation.FlagboardInternal
import org.json.JSONObject
import java.util.Locale

@Composable
internal fun AppTopBar(
    context: Context,
    showRemoteTab: Boolean,
    activeSource: FlagboardSource,
    isLoading: Boolean,
    onSourceSelected: (FlagboardSource) -> Unit,
) {
    val selectedTabIndex = if (activeSource is FlagboardSource.Firebase) 1 else 0

    Column {
        TopAppBar(
            backgroundColor = Color.Black,
            contentColor = Color.White,
            title = { Text(text = "Flagboard") },
            navigationIcon = {
                IconButton(onClick = { (context as ComponentActivity).finish() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp),
                    )
                }
            }
        )
        if (showRemoteTab) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.Black,
                contentColor = Color.White,
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { onSourceSelected(FlagboardSource.Local) },
                    text = { Text("Local") },
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        val firebaseSource = FlagboardInternal.currentSource
                            .takeIf { it is FlagboardSource.Firebase }
                            ?: FlagboardInternal.configuredSource
                        onSourceSelected(firebaseSource)
                    },
                    text = { Text("Firebase RC") },
                )
            }
        }
    }
}

@Composable
internal fun FlagList(textState: MutableState<TextFieldValue>, flags: List<FeatureFlag>) {
    val context = LocalContext.current
    var filteredList: MutableList<FeatureFlag>
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        val searchedText = textState.value.text
        filteredList = if (searchedText.isEmpty()) {
            flags.toMutableList()
        } else {
            val resultList = mutableListOf<FeatureFlag>()
            for (flag in flags) {
                when (flag) {
                    is FeatureFlag.BooleanFlag -> {
                        if (filter(flag.param.key.value, searchedText)) resultList.add(flag)
                    }
                    is FeatureFlag.NumberFlag  -> {
                        if (filter(flag.param.key.value, searchedText)) resultList.add(flag)
                    }
                    is FeatureFlag.JsonFlag    -> {
                        if (filter(flag.param.key.value, searchedText)) resultList.add(flag)
                    }
                    is FeatureFlag.StringFlag  -> {
                        if (filter(flag.param.key.value, searchedText)) resultList.add(flag)
                    }
                    is FeatureFlag.UnknownFlag -> {
                        if (filter(flag.param.key.value, searchedText)) resultList.add(flag)
                    }
                }
            }
            resultList
        }
        itemsIndexed(items = filteredList) { index, item ->
            when (item) {
                is FeatureFlag.BooleanFlag -> ItemRow(param = item.param)
                is FeatureFlag.NumberFlag  -> ItemRow(param = item.param, onRowClick = {
                    context.showToast(item.param.value.toString())
                })
                is FeatureFlag.JsonFlag    -> ItemRow(param = item.param, onRowClick = {
                    context.showToast(item.param.value.toString())
                })
                is FeatureFlag.StringFlag  -> ItemRow(param = item.param, onRowClick = {
                    context.showToast(item.param.value)
                })
                is FeatureFlag.UnknownFlag -> ItemRow(param = item.param, onRowClick = {
                    context.showToast(item.param.value.toString())
                })
            }
            if (index < filteredList.lastIndex) Divider(color = Color.LightGray, thickness = 0.5.dp)
        }
    }
}

fun filter(flagName: String, term: String): Boolean {
    return flagName.lowercase(Locale.getDefault()).contains(term.lowercase(Locale.getDefault()))
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
fun SearchView(state: MutableState<TextFieldValue>) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value = TextFieldValue("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = Color.DarkGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun AddSwitch(param: Param<Boolean>) {
    val checkedState = remember(param.key, param.value) { mutableStateOf(param.value) }

    Switch(checked = checkedState.value, onCheckedChange
    = {
        checkedState.value = it
        FlagboardInternal.save(param.key.value, it)
    })
}

@Composable
private fun AddIcon(param: Param<*>) {
    val icon = when (param.value) {
        is Boolean    -> R.drawable.ic_boolean
        is String     -> R.drawable.ic_abc
        is Int        -> R.drawable.ic_number
        is JSONObject -> R.drawable.ic_json
        else          -> R.drawable.ic_number
    }
    Icon(painter = painterResource(id = icon), contentDescription = null)
}

private fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
