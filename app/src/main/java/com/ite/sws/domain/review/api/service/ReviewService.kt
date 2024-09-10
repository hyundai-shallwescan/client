package com.ite.sws.domain.review.api.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


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
}