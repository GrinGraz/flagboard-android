package cl.gringraz.flagboard_android

sealed class FBState {
    object Unknown : FBState()
    data class Initialized(val ffLoaded: FBDataState) : FBState()
}