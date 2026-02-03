package com.example.dearyourday.data

import android.content.Context
import androidx.datastore.preferences.core.edit
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
    }

    // 저장하기 - 로그인 성공 시 호출
    suspend fun saveLoginData(loginId: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOGIN_ID] = loginId
            preferences[KEY_PASSWORD] = password
        }
    }

    // 불러오기 - 앱 켜질 때 호출
    val loginDataFlow: Flow<Pair<String, String>?> = context.dataStore.data
        .map { preferences ->
            val loginId = preferences[KEY_LOGIN_ID]
            val password = preferences[KEY_PASSWORD]

            if (loginId != null && password != null) {
                Pair(loginId, password) // 둘 다 있으면 반환
            } else {
                null // 없으면 null
            }
        }

    // 삭제하기 - 로그아웃 시 호출
    suspend fun clearLoginData() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_LOGIN_ID)
            preferences.remove(KEY_PASSWORD)
        }
    }
}