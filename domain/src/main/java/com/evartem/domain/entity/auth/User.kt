package com.evartem.domain.entity.auth

/**
 * A user of the system
 *
 * @property id the user's id on the back-end
 * @property fullName the user's full name
 * @property position the user's position in the company (e.g. Senior accountant)
 * @property authGroups the authorization groups that the user belongs to
 * @property status the state of the user (busy, off-duty, waiting for a task, etc.)
 */
data class User(
    val id: String,
    val fullName: String,
    val avatarUrl: String,
    val position: String,
    val authGroups: List<Group>,
    val status: UserStatus
)