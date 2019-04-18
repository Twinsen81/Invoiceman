package com.evartem.invoiceman.util

import android.content.res.Resources
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.GatewayErrorCode
import com.evartem.invoiceman.R

/**
 * Error codes of the app as translatable string resources.
 */
fun getErrorMessageForUi(resources: Resources, gatewayError: GatewayError?): String {
    if (gatewayError == null) return R.string.network_error_general.resToString(resources).format(0)
    return when (gatewayError.code) {
        GatewayErrorCode.INCONSISTENT_DATA -> R.string.network_error_inconsistent_data.resToString(resources)
        GatewayErrorCode.NO_PERMISSIONS -> R.string.network_error_no_permissions.resToString(resources)
        GatewayErrorCode.NOT_FOUND -> R.string.network_error_not_found.resToString(resources)
        GatewayErrorCode.ALREADY_TAKEN_BY_OTHER -> R.string.network_error_taken.resToString(resources)
        GatewayErrorCode.INTERNAL_SERVER_ERROR -> R.string.network_error_server.resToString(resources)
        GatewayErrorCode.GENERAL_ERROR -> R.string.network_error_server_not_available.resToString(resources)
        GatewayErrorCode.WRONG_SERVER_RESPONSE -> R.string.network_error_server_wrong_response.resToString(resources)
        else -> R.string.network_error_general.resToString(resources).format(gatewayError.code.value)
    }
}

private fun Int.resToString(resources: Resources) = resources.getString(this)