package com.ite.sws.common

import android.util.Log
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.google.gson.Gson
import com.ite.sws.BuildConfig
import com.google.gson.GsonBuilder
import com.ite.sws.BuildConfig.BASE_URL
import com.ite.sws.common.RetrofitClient.httpClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.member.api.repository.MemberRepository
import com.ite.sws.domain.member.api.service.MemberService
import com.ite.sws.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit Client
 * @author 정은지
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	정은지       최초 생성
 * 2024.09.02   정은지       인터셉터 설정
 * 2024.09.13   정은지       액세스 토큰 재발급 추가
 * </pre>
 */

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL
    private val memberService: MemberService by lazy {
        instance.create(MemberService::class.java)
    }

    val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()

                // SharedPreferencesUtil에서 액세스 토큰 가져오기
                val accessToken = SharedPreferencesUtil.getAccessToken()
                Log.d("RetrofitClient", "accessToken: $accessToken")

                // 액세스 토큰이 있으면 Authorization 헤더에 추가
                if (!accessToken.isNullOrEmpty()) {
                    request = request.newBuilder()
                        .header("Authorization", "Bearer $accessToken")
                        .build()
                }

                val response = chain.proceed(request)
                Log.d("RetrofitClient", "Response message 확인: ${response.message}")
                //  응답이 발생하면 리프레시 토큰을 사용해 액세스 토큰 재발급
                if (response.code == 401) {
                    // 리프레시 토큰 가져오기
                    val refreshToken = SharedPreferencesUtil.getRefreshToken()

                    if (!refreshToken.isNullOrEmpty()) {
                        // 액세스 토큰 재발급 요청
                        val newAccessTokenResponse = memberService.reissueAccessToken("Bearer $refreshToken").execute()

                        // 재발급 성공 시
                        if (newAccessTokenResponse.isSuccessful) {

                            val newAccessToken = newAccessTokenResponse.headers()["Authorization"]?.replace("Bearer ", "")
                            if (!newAccessToken.isNullOrEmpty()) {
                                // 새 액세스 토큰을 SharedPreferences에 저장
                                SharedPreferencesUtil.setAccessToken(newAccessToken)

                                // 재발급된 액세스 토큰으로 기존 요청을 다시 실행
                                val newRequest = request.newBuilder()
                                    .header("Authorization", "Bearer $newAccessToken")
                                    .build()

                                // 재요청 실행
                                return@addInterceptor chain.proceed(newRequest)
                            }
                        }
                    }
                }
                // 원래 응답 반환
                response
            }
            .build()
    }


    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}