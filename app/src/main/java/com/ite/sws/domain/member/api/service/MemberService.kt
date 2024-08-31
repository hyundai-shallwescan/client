package com.ite.sws.domain.member.api.service

import com.ite.sws.domain.member.data.JwtToken
import com.ite.sws.domain.member.data.PostLoginReq
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 멤버 서비스 인터페이스
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
interface MemberService {

    @POST("/members/login")
    fun login(@Body loginRequest: PostLoginReq
    ): Call<JwtToken>
}