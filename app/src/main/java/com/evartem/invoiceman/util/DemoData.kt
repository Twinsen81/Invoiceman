package com.evartem.invoiceman.util

import com.evartem.domain.entity.auth.Group
import com.evartem.domain.entity.auth.Permission
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.auth.UserStatus

val DEMO_USER = User(
    "demo@invoiceman.com", "Demo Demov",
    "http://www.avatarsdb.com/avatars/einteins_tongue.jpg", "Warehouse worker",
    listOf(
        Group(
            1,
            "Simple worker",
            listOf(
                Permission.VIEW_INVOICE,
                Permission.PROCESS_INVOICE,
                Permission.ENTER_SERIAL_MANUALLY,
                Permission.EDIT_RESULT
            )
        )
    )
    , UserStatus.READY
)