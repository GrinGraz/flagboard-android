package cl.gringraz.flagboard_android.util

sealed class Either<out L, out R> {

    data class Error<out L>(val error: L) : Either<L, Nothing>()

    data class Success<out R>(val success: R) : Either<Nothing, R>()

    val isSuccess get() = this is Success<R>
    val isError get() = this is Error<L>

    fun <T> fold(fnL: (L) -> T, fnR: (R) -> T): T {
        return when (this) {
            is Error   -> fnL(error)
            is Success -> fnR(success)
        }
    }

    fun getSuccessOrNull(): R? = if (this is Success<R>) {
        this.success
    } else {
        null
    }

    fun getFailureOrNull(): L? = if (this is Error<L>) {
        this.error
    } else {
        null
    }
}
