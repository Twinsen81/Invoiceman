package com.evartem.domain.gateway

/**
 * Defines an error that occurred while running an operation on the gateway. This can be an
 * error on the server's side or an error while processing the server's response locally.
 *
 * @property code the code of the reason for the error. Set if the reason could be determined, i.g. from
 * the server HTTP-response code. This code can be used to display a localized text message on the UI.
 * @property message the description of the reason for the error. In English, should be used only for logging,
 * not for displaying on UI.
 * @property exception contains the exception that caused this instance to be created (if it was crated because
 * of the exception)
 */
data class GatewayError(val code: ErrorCode, val message: String? = null, val exception: Throwable? = null) {

    enum class ErrorCode(val value: Int) {

        UNKNOWN_ERROR(0),
        SUCCESS(200),
        INCONSISTENT_DATA(400),
        NO_PERMISSIONS(401),
        NOT_FOUND(404),
        ALREADY_TAKEN_BY_OTHER(409),
        INTERNAL_SERVER_ERROR(500),
        GENERAL_ERROR(1000),
        WRONG_SERVER_RESPONSE(1001);

        companion object {
            private val values = values()
            fun getByValue(value: Int): ErrorCode = values.firstOrNull { it.value == value } ?: UNKNOWN_ERROR
        }
    }
}

typealias GatewayErrorCode = GatewayError.ErrorCode