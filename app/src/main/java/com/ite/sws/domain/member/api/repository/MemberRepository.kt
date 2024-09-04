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
import android.util.Log
import com.ite.sws.domain.member.data.GetMemberRes
import com.ite.sws.domain.member.data.PostLoginRes
import com.ite.sws.domain.member.data.PostMemberReq


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
 * 2024.08.31   정은지        로그인 추가
 * 2024.09.02   정은지        회원 정보 조회 추가
 * 2024.09.03   정은지        로그아웃 추가
 * 2024.09.03   정은지        회원 탈퇴 추가
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
        call.enqueue(object : Callback<PostLoginRes> {
            override fun onResponse(call: Call<PostLoginRes>, response: Response<PostLoginRes>) {
                if (response.isSuccessful) {
                    val accessToken = response.headers()["Authorization"]
                    accessToken?.let {
                        // 액세스 토큰 추출
                        val token = it.removePrefix("Bearer ")

                        // 액세스 토큰 SharedPreferences에 저장
                        SharedPreferencesUtil.setAccessToken(token)
                    }

                    // 장바구니 아이디 SharedPreferences에 저장
                    response.body()?.cartId?.let { cartId ->
                        SharedPreferencesUtil.setCartId(cartId)
                        Log.d("MemberRepository", "장바구니 아이디: $cartId")
                    }

                    onSuccess()
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBodyString, ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<PostLoginRes>, t: Throwable) {
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
     * 로그아웃
     */
    fun logout(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = memberService.logout()
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 액세스 토큰 삭제
                    SharedPreferencesUtil.removeAccessToken()
                    onSuccess()
                } else {
                    onFailure(Throwable("로그아웃 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t)
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

        if (token != null) {
            val call = memberService.getMyInfo()

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

    /**
     * 회원 탈퇴
     */
    fun withdraw(
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val call = memberService.withdraw()
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // SharedPrefer 비우기
                    SharedPreferencesUtil.clearAll()
                    onSuccess()
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBodyString, ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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
     * 회원가입
     */
    fun signup(
        signupRequest: PostMemberReq,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = memberService.signup(signupRequest)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(Throwable("Failed to sign up: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    /**
     * 로그인 아이디 중복 체크
     */
    fun isLoginIdAvailable(
        loginId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val call = memberService.isLoginIdAvailable(loginId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorBody, ErrorRes::class.java)
                    onFailure(errorRes.message)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t.localizedMessage ?: "Network error occurred")
            }
        })
    }
}
