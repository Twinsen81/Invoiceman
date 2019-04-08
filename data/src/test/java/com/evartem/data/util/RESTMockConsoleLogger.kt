package com.evartem.data.util

import io.appflate.restmock.logging.RESTMockLogger

class RESTMockConsoleLogger: RESTMockLogger {
    override fun error(errorMessage: String?, exception: Throwable?) {
        println("RESTMock - error: $errorMessage, ${exception?.printStackTrace()}")
    }

    override fun log(message: String?) {
        println("RESTMock: $message")
    }

    override fun error(errorMessage: String?) {
        println("RESTMock - error: $errorMessage")
    }
}