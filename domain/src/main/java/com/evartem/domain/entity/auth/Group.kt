package com.evartem.domain.entity.auth

/**
 * A group represents a few permissions combined together. A user that is a member of a group, has all the
 * permissions that the group has. A user can be a member of a few groups.
 *
 * @property id the group's id on the back-end
 * @property description a name/description of the group
 * @property permissions a list of permissions that the group owns
 */
data class Group(
    val id: Int,
    val description: String,
    val permissions: List<Permission>
)