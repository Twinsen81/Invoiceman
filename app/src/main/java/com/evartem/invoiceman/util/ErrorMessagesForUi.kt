package com.evartem.invoiceman.util

import android.content.res.Resources
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.GatewayErrorCode
import com.evartem.invoiceman.R

/**
 * Error codes of the app as translatable string resources.
 */
fun getErrorMessageForUi(resources: Resources, gatewayError: GatewayError?): String {
    if (gatewayError == null) return R.string.network_error_general.toString(resources).format(0)
    return when (gatewayError.code) {
        GatewayErrorCode.INCONSISTENT_DATA -> R.string.network_error_inconsistent_data.toString(resources)
        GatewayErrorCode.NO_PERMISSIONS -> R.string.network_error_no_permissions.toString(resources)
        GatewayErrorCode.NOT_FOUND -> R.string.network_error_not_found.toString(resources)
        GatewayErrorCode.ALREADY_TAKEN_BY_OTHER -> R.string.network_error_taken.toString(resources)
        GatewayErrorCode.INTERNAL_SERVER_ERROR -> R.string.network_error_server.toString(resources)
        GatewayErrorCode.GENERAL_ERROR -> R.string.network_error_server_not_available.toString(resources)
        GatewayErrorCode.WRONG_SERVER_RESPONSE -> R.string.network_error_server_wrong_response.toString(resources)
        else -> R.string.network_error_general.toString(resources).format(gatewayError.code.value)
    }
}