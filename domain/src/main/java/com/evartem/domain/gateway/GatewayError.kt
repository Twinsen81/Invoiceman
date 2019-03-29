package com.evartem.domain.gateway

data class GatewayError(val code: ErrorCode, val message: String? = null, val exception: Throwable? = null) {

    enum class ErrorCode(val value: Int) {

        UNKNOWN_ERROR(0),
        SUCCESS(200),
        INCONSISTENT_DATA (400),
        NO_PERMISSIONS (401),
        NOT_FOUND (404),
        ALREADY_TAKEN_BY_OTHER (409),
        INTERNAL_SERVER_ERROR (500),
        GENERAL_NETWORK_ERROR (1000),
        WRONG_SERVER_RESPONSE (1001);

        companion object  {
            private val vals = values()
            fun getByValue(value: Int): ErrorCode = values().firstOrNull { it.value == value } ?: UNKNOWN_ERROR
        }
    }

}

typealias GatewayErrorCode = GatewayError.ErrorCode
