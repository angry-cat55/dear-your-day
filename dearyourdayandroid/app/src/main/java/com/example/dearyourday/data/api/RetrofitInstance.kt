package com.example.dearyourday.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // AWS EC2 퍼블릭 IP 주소로 연결
    private const val BASE_URL = "http://13.209.69.43:8080/"

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
        .addConverterFactory(ScalarsConverterFactory.create()) // Response<String> 타입 처리
        .addConverterFactory(GsonConverterFactory.create()) // JSON -> DTO 자동 변환기
        .build()

    // 4. 서비스 개방
    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val diaryApi: DiaryApiService by lazy { retrofit.create(DiaryApiService::class.java) }
    val aiApi: AiSummaryApiService by lazy { retrofit.create(AiSummaryApiService::class.java) }
}