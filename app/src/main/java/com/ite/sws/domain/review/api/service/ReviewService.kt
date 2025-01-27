package com.ite.sws.domain.review.api.service

import com.ite.sws.domain.review.data.GetReviewRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Review service class
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06   구지웅        최초 생성

 */
interface ReviewService {

    //리뷰 생성
    @Multipart
    @POST("/reviews")
    fun uploadReview(
        @Part("postCreateReviewReq") postCreateReviewReq: RequestBody,
        @Part image: MultipartBody.Part,
        @Part shortForm: MultipartBody.Part): Call<Void>

    //리뷰 전체 조회
    @GET("/reviews")
    fun getReviews(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<GetReviewRes>>

    //리뷰 삭제
    @DELETE("/reviews/{reviewId}")
    fun deleteReview(
        @Path("reviewId") reviewId: Long
    ): Call<Void>
    //리뷰 단일 조회
    @GET("/reviews/{reviewId}")
    fun getReviewDetail(
        @Path("reviewId") reviewId: Long
    ): Call<GetReviewRes>
}