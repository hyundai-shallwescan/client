package com.ite.sws.domain.member.api.service

import com.ite.sws.domain.member.data.GetMemberRes
import com.ite.sws.domain.member.data.JwtToken
import com.ite.sws.domain.member.data.PostLoginReq
import com.ite.sws.domain.member.data.PostLoginRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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
 * 2024.08.31   정은지        로그인 API 호출
 * 2024.09.02   정은지        회원 정보 조회 API 호출
 * 2024.09.03   정은지        로그아웃 API 호출
 * 2024.09.03   정은지        회원탈퇴 API 호출
 * </pre>
 */
interface MemberService {

    @POST("/members/login")
    fun login(@Body loginRequest: PostLoginReq
    ): Call<PostLoginRes>

    @GET("/members")
    fun getMyPageInfo(): Call<GetMemberRes>

    @POST("/members/logout")
    fun logout(): Call<Void>

    @DELETE("/members")
    fun withdraw(): Call<Void>
}