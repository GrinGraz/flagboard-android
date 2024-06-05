package cl.gringraz.flagboard_android

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FlagboardInstrumentedTest {

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val wrongKey: String = "wrong key"
    private val emptyFlagMap = emptyMap<String, Any>()
    private val booleanFlagMap = mapOf("true key" to true, "false key" to false)
    private val stringFlagMap = mapOf("first key" to "first value", "second key" to "second value")
    private val intFlagMap = mapOf("one key" to 1, "two key" to 2)
    private val longFlagMap = mapOf("one key" to 1L, "two key" to 2L)
    private val jsonFlagMap = mapOf("json key" to "{\"key\":\"value\"}")
    private val doubleFlagMap = mapOf("one key" to 1.0, "two key" to 2.0)

    @Before
    fun reset() {
        Flagboard.init(context = context)
        Flagboard.reset()
    }

    @Test
    fun openFlagboardWithLoadedFlagsShouldBeInitializedAndLoaded() {
        Flagboard.loadFlags(emptyFlagMap).open(context)
        assertEquals(FBState.Initialized(FBDataState.FF_LOADED), Flagboard.getState())
    }

    @Test
    fun openFlagboardWithoutLoadedFlagsShouldBeInitializedAndNoLoaded() {
        Flagboard.open(context)
        assertEquals(FBState.Initialized(FBDataState.FF_NOT_LOADED), Flagboard.getState())
    }

    @Test
    fun loadFlagboardWithEmptyFlagsShouldReturnZeroFlags() {
        assertTrue(Flagboard.loadFlags(emptyFlagMap).getAll().isEmpty())
    }

    @Test
    fun loadFlagboardWithFlagsShouldReturnOneOrMoreFlags() {
        assertTrue(Flagboard.loadFlags(booleanFlagMap).getAll().isNotEmpty())
    }

    @Test
    fun getFlagboardFalseFlagShouldReturnFalse() {
        assertFalse(Flagboard.loadFlags(booleanFlagMap).getBoolean("false key"))
    }

    @Test
    fun getFlagboardTrueFlagShouldReturnTrue() {
        assertTrue(Flagboard.loadFlags(booleanFlagMap).getBoolean("true key"))
    }

    @Test
    fun getFlagboardWrongBooleanKeyFlagShouldReturnFalse() {
        assertFalse(Flagboard.loadFlags(booleanFlagMap).getBoolean(wrongKey))
    }

    @Test
    fun getFlagboardStringFlagShouldReturnString() {
        assertEquals("first value", Flagboard.loadFlags(stringFlagMap).getString("first key"))
    }

    @Test
    fun getFlagboardWrongStringKeyFlagShouldReturnEmptyString() {
        assertEquals("", Flagboard.loadFlags(booleanFlagMap).getString(wrongKey))
    }

    @Test
    fun getFlagboardIntFlagShouldReturnInt() {
        assertEquals(1, Flagboard.loadFlags(intFlagMap).getInt("one key"))
    }

    @Test
    fun getFlagboardWrongStringKeyFlagShouldReturnMinusOne() {
        assertEquals(-1, Flagboard.loadFlags(intFlagMap).getInt(wrongKey))
    }

    @Test
    fun getFlagboardLongFlagShouldReturnLong() {
        assertEquals(1L, Flagboard.loadFlags(longFlagMap).getLong("one key"))
    }

    @Test
    fun getFlagboardWrongLongKeyFlagShouldReturnMinusOne() {
        assertEquals(-1, Flagboard.loadFlags(longFlagMap).getLong(wrongKey))
    }

    @Test
    fun getFlagboardDoubleFlagShouldReturnDouble() {
        assertEquals(1.0, Flagboard.loadFlags(doubleFlagMap).getDouble("one key"), 0.0)
    }

    @Test
    fun getFlagboardWrongDoubleKeyFlagShouldReturnMinusOne() {
        assertEquals(-1.0, Flagboard.loadFlags(doubleFlagMap).getDouble(wrongKey), 0.0)
    }

    @Test
    fun getFlagboardJsonFlagShouldReturnJson() {
        assertEquals("{\"key\":\"value\"}", Flagboard.loadFlags(jsonFlagMap).getString("json key"))
    }

    @Test
    fun getFlagboardWrongJsonKeyFlagShouldReturnEmptyString() {
        assertEquals("", Flagboard.loadFlags(jsonFlagMap).getString(wrongKey))
    }

    @Test
    fun resetFlagboardShouldChangeStateToInitializedAndNoLoaded() {
        Flagboard.loadFlags(emptyFlagMap)
        assertEquals(FBState.Initialized(FBDataState.FF_LOADED), Flagboard.getState())
        Flagboard.reset()
        assertEquals(FBState.Initialized(FBDataState.FF_NOT_LOADED), Flagboard.getState())
    }

    @Test
    fun resetFlagboardShouldReturnZeroFlags() {
        Flagboard.loadFlags(booleanFlagMap)
        assertTrue(Flagboard.loadFlags(booleanFlagMap).getAll().isNotEmpty())
        Flagboard.reset()
        assertTrue(Flagboard.loadFlags(emptyFlagMap).getAll().isEmpty())
    }
}
