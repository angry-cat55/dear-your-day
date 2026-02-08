package com.example.dearyourday.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 이름 settings로 DataStore 생성
val Context.dataStore by preferencesDataStore(name = "settings")

class AutoLoginManager(private val context: Context) {
    // 저장할 키 정의
    companion object {
        private val KEY_LOGIN_ID = stringPreferencesKey("login_id")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_NICKNAME = stringPreferencesKey("nickname")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_CREATED_AT = stringPreferencesKey("created_at")
    }

    // 저장하기 - 로그인 성공 시 호출
    suspend fun saveLoginData(
        userId: Long,
        nickname: String,
        loginId: String,
        password: String,
        email: String,
        createdAt: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
            preferences[KEY_NICKNAME] = nickname
            preferences[KEY_LOGIN_ID] = loginId
            preferences[KEY_PASSWORD] = password
            preferences[KEY_EMAIL] = email
            preferences[KEY_CREATED_AT] = createdAt
        }
    }

    // 불러오기 - 앱 켜질 때 호출
    val loginDataFlow: Flow<Pair<String, String>?> = context.dataStore.data
        .map { preferences ->
            val loginId = preferences[KEY_LOGIN_ID]
            val password = preferences[KEY_PASSWORD]

            if (loginId != null && password != null) {
                // 아이디랑 비번 둘 다 있으면 반환
                Pair(loginId, password)
            } else {
                null
            }
        }

    // 닉네임 변경
    suspend fun updateNickname(newNickname: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NICKNAME] = newNickname
        }
    }

    // 삭제하기 - 로그아웃 시 호출
    suspend fun clearLoginData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}