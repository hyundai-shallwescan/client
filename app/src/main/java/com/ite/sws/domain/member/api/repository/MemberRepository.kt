package com.ite.sws.domain.member.api.repository

import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.member.api.service.MemberService
import com.ite.sws.domain.member.data.JwtToken
import com.ite.sws.domain.member.data.PostLoginReq
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        onSuccess: (JwtToken) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val call = memberService.login(postLoginReq)
        call.enqueue(object : Callback<JwtToken> {
            override fun onResponse(call: Call<JwtToken>, response: Response<JwtToken>) {
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
}