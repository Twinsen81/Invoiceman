package com.evartem.domain.entity.auth

/**
 * The availability of the user to perform an invoice processing
 */
enum class UserStatus {
    /** the user is not available (not at work) */ OFF_DUTY,
    /** the user is idling and ready to process invoices */ READY,
    /** the user is processing invoices at the moment */ BUSY
}