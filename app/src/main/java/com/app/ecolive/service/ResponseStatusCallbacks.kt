package com.app.ecolive.service


data class ResponseStatusCallbacks<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> success(data: T): ResponseStatusCallbacks<T> =
            ResponseStatusCallbacks(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): ResponseStatusCallbacks<T> =
            ResponseStatusCallbacks(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): ResponseStatusCallbacks<T> =
            ResponseStatusCallbacks(status = Status.LOADING, data = data, message = null)
    }
}