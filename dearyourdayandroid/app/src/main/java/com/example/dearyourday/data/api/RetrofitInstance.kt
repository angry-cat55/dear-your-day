package com.example.dearyourday.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // 안드로이드 에뮬레이터에서 백엔드(localhost) 접속 주소
    //    private const val BASE_URL = "http://10.0.2.2:8080/"
    // 갤럭시 실기기에서 접속 주소
    private  const val BASE_URL = "http://192.168.0.15:8080/"

    // 1. 로그를 찍어주는 감시자 (통신 내용 확인용)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청/응답 내용을 다 보여줌
    }

    // 2. 통신 클라이언트 (타임아웃 설정 등)
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS) // 연결 타임아웃 30초
        .readTimeout(60, TimeUnit.SECONDS)    // 읽기 타임아웃 30초
        .build()

    // 3. Retrofit 본체 생성
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()) // JSON -> DTO 자동 변환기
        .build()

    // 4. 서비스(메뉴판) 개방
    // 나중에 화면에서 RetrofitInstance.userApi.login() 이렇게 씁니다.
    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val diaryApi: DiaryApiService by lazy { retrofit.create(DiaryApiService::class.java) }
    val aiApi: AiSummaryApiService by lazy { retrofit.create(AiSummaryApiService::class.java) }
}