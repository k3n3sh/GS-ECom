package com.k3n3sh.gsecom.domain.model

// Success or a typed error
sealed interface Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>
    data class Failure(val error: DataError) : Outcome<Nothing>
}

sealed interface DataError {
    // Offline before the request
    data object NoInternet : DataError

    // Failed mid-request
    data object Network : DataError

    data class Server(val code: Int) : DataError

    data object InvalidData : DataError

    data object NotFound : DataError

    data object Unknown : DataError
}
