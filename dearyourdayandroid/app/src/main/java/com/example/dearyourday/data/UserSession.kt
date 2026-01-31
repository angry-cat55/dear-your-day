package com.example.dearyourday.data

object UserSession {
    var userId: Long = -1L
    var nickname: String = ""

    fun clear() {
        userId = -1L
        nickname = ""
    }
}