package com.evartem.domain.entity.doc

/**
 * The status of processing a packaging by an employee - represents the state of
 * the operation. If the employee successfully scanned the serial number, then
 * the operation is completed, else - failed.
 */
enum class ResultStatus {
    /** the processing succeeded - got the result */ COMPLETED,
    /** the processing failed - no result */ FAILED
}