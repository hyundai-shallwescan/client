package com.ite.sws.domain.member.api.service

import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.domain.member.data.GetMemberRes
import com.ite.sws.domain.member.data.GetMemberReviewRes
import com.ite.sws.domain.member.data.PatchMemberReq
import com.ite.sws.domain.member.data.PostLoginReq
import com.ite.sws.domain.member.data.PostLoginRes
import com.ite.sws.domain.member.data.PostMemberReq
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

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
 * 2024.09.04   정은지        회원가입 API 호출
 * 2024.09.04   정은지        로그인 아이디 중복 체크 API 호출
 * 2024.09.04   정은지        회원 정보 수정 API 호출
 * 2024.09.05   정은지        회원 구매 내역 조회 API 호출
 * 2024.09.06   정은지        작성 리뷰 조회 API 호출
 * 2024.09.10   남진수        FCM 토큰 발급 추가
 * 2024.09.12   정은지        액세스 토큰 재발급 API 호출
 * </pre>
 */
interface MemberService {

    /**
     * 로그인
     */
    @POST("/members/login")
    fun login(@Header("FCM-TOKEN") fcmToken: String, @Body loginRequest: PostLoginReq): Call<PostLoginRes>

    /**
     * 회원 정보 조회
     */
    @GET("/members")
    fun getMyInfo(): Call<GetMemberRes>

    /**
     * 로그아웃
     */
    @POST("/members/logout")
    fun logout(@Header("X-Refresh-Token") refreshToken: String): Call<Void>

    /**
     * 회원탈퇴
     */
    @DELETE("/members")
    fun withdraw(@Header("X-Refresh-Token") refreshToken: String): Call<Void>

    /**
     * 회원가입
     */
    @POST("/members/signup")
    fun signup(@Body signupRequest: PostMemberReq): Call<Void>

    /**
     * 로그인 아이디 중복 체크
     */
    @GET("/members/check-id")
    fun isLoginIdAvailable(@Query("login-id") loginId: String): Call<Void>

    /**
     * 회원 정보 수정
     */
    @PATCH("/members")
    fun modifyMember(@Body modifyRequest: PatchMemberReq): Call<Void>

    /**
     * 구매 내역 조회
     */
    @GET("/members/payments")
    fun findPaymentItemList(): Call<List<GetMemberPaymentRes>>


    /**
     * 작성 리뷰 조회
     */
    @GET("/members/reviews")
    suspend fun findReviewList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<GetMemberReviewRes>>

    /**
     * 액세스 토큰 재발급
     */
    @POST("/members/reissue")
    fun reissueAccessToken(@Header("X-Refresh-Token") refreshToken: String): Call<Void>
}