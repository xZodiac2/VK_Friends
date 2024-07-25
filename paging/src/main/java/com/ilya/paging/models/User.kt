package com.ilya.paging.models

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val photoUrl: String,
    val isClosed: Boolean
)