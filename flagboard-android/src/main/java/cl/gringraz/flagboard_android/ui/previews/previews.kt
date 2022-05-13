package cl.gringraz.flagboard_android.ui.previews

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cl.gringraz.flagboard_android.ui.ItemRow
import cl.gringraz.flagboard_android.data.models.Key
import cl.gringraz.flagboard_android.data.models.Param
import cl.gringraz.flagboard_android.ui.theme.FlagboardTheme

@Preview(showBackground = true)
@Composable
internal fun DefaultPreview() {
    FlagboardTheme {
        val map = mapOf("Boolean flag" to true,
            "String flag" to "hello", "Int" +
                    " flag"
                    to 1, "Json flag" to "{\"key\":\"value\"}")
        //FlagBoard.thisFeatureFlagsMap = map.toMutableMap()
        //FlagList(flagBoardInternal.getFlags())
    }
}

@Preview(showBackground = true)
@Composable
internal fun RowBooleanPreview() {
    val context = LocalContext.current
    ItemRow(param = Param(Key(value = "This is a feature flag key"), "value"), onRowClick = {
        Toast.makeText(context, "value", Toast.LENGTH_SHORT).show()
    })
}

@Preview(showBackground = true)
@Composable
internal fun RowBooleanPreview2() {
    ItemRow(param = Param(Key(value = "This is a feature flag key"), true))
}