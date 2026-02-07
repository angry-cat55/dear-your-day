package com.example.dearyourday.data

object UserSession {
    var userId: Long = -1L
    var nickname: String = ""
    var loginId: String = ""
    var email: String = ""
    var createdAt: String = ""


    fun clear() {
        userId = -1L
        nickname = ""
        loginId = ""
        email = ""
        createdAt = ""
    }
}