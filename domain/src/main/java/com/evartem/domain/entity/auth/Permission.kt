package com.evartem.domain.entity.auth

/**
 * A permission to perform a certain action in the system
 */
enum class Permission {
    /** add a new invoice */ ADD_INVOICE,
    /** view existing invoices */ VIEW_INVOICE,
    /** edit an invoice */ EDIT_INVOICE,
    /** process an invoice - add serials to products */ PROCESS_INVOICE,
    /** edit the result (serials) */  EDIT_RESULT,
    /** enter serials manually */ ENTER_SERIAL_MANUALLY
}
