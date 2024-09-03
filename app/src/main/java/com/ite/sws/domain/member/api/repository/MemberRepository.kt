package com.ite.sws.domain.member.api.repository

import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.member.api.service.MemberService
import com.ite.sws.domain.member.data.JwtToken
import com.ite.sws.domain.member.data.PostLoginReq
import com.ite.sws.util.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import com.ite.sws.domain.member.data.GetMemberRes


/**
 * 회원 Repository class
 * @author 정은지
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31   정은지        최초 생성
 * </pre>
 */
class MemberRepository {

    private val memberService =
        RetrofitClient.instance.create(MemberService::class.java)

    /**
     * 로그인
     */
    fun login(
        postLoginReq: PostLoginReq,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val call = memberService.login(postLoginReq)
        call.enqueue(object : Callback<JwtToken> {
            override fun onResponse(call: Call<JwtToken>, response: Response<JwtToken>) {
                if (response.isSuccessful) {
                    response.body()?.let { jwtToken ->
                        // 로그인 성공 시 액세스 토큰을 SharedPreferences에 저장
                        SharedPreferencesUtil.setAccessToken(jwtToken.accessToken)
                        onSuccess()
                    }

                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBodyString, ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<JwtToken>, t: Throwable) {
                t.printStackTrace()
                val networkError = ErrorRes(
                    status = 0,
                    errorCode = "NETWORK_ERROR",
                    message = t.localizedMessage ?: "Unknown network error"
                )
                onFailure(networkError)
            }
        })
    }

    /**
     * 마이페이지 정보 요청
     */
    fun getMyPageInfo(
        onSuccess: (GetMemberRes) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        // SharedPreferences에서 JWT 토큰을 가져오기
        val token = SharedPreferencesUtil.getAccessToken()

        // 토큰이 null이 아닌 경우에만 요청 수행
        if (token != null) {
            val authHeader = "Bearer $token"
            val call = memberService.getMyPageInfo(authHeader)

            call.enqueue(object : Callback<GetMemberRes> {
                override fun onResponse(call: Call<GetMemberRes>, response: Response<GetMemberRes>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        }
                    } else {
                        val errorBodyString = response.errorBody()?.string()
                        val errorRes = Gson().fromJson(errorBodyString, ErrorRes::class.java)
                        onFailure(errorRes)
                    }
                }

                override fun onFailure(call: Call<GetMemberRes>, t: Throwable) {
                    t.printStackTrace()
                    val networkError = ErrorRes(
                        status = 0,
                        errorCode = "NETWORK_ERROR",
                        message = t.localizedMessage ?: "Unknown network error"
                    )
                    onFailure(networkError)
                }
            })
        } else {
            // 토큰이 없을 경우 실패 처리
            onFailure(ErrorRes(0, "NO_TOKEN", "JWT 토큰이 없습니다."))
        }
    }
}