package com.ite.sws.domain.review.api.repository

import android.media.Image
import android.util.Log
import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.review.api.service.ReviewService
import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.domain.review.data.PostCreateReviewReq
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Review Repository class
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06   구지웅        최초 생성

 */
class ReviewRepository {

    private val reviewService = RetrofitClient.instance.create(ReviewService::class.java)

    fun createReview(
        postCreateReviewReq: PostCreateReviewReq,
        shortFormFile: File,
        image: File,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val gson = Gson()
        val postCreateReviewReqJson = gson.toJson(postCreateReviewReq)
        val postCreateReviewReqBody = createRequestBody(postCreateReviewReqJson, "application/json")
        val shortFormPart = createMultipartBodyPart("shortForm", shortFormFile, "File")
        val imagePart = createMultipartBodyPart("image", image, "File")
        reviewService.uploadReview(postCreateReviewReqBody,imagePart, shortFormPart).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(Exception("Failed to receive a valid response."))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t)
            }
        })
    }
    fun getReviews(
        page: Int?=null,
        size: Int?=null,
        onSuccess: (List<GetReviewRes>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val actualPage = page ?: 0
        val actualSize = size ?: 10

        reviewService.getReviews(actualPage, actualSize).enqueue(object : Callback<List<GetReviewRes>> {
            override fun onResponse(call: Call<List<GetReviewRes>>, response: Response<List<GetReviewRes>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    }
                } else {
                    onFailure(Exception("응답에 실패했습니다."))
                }
            }

            override fun onFailure(call: Call<List<GetReviewRes>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getReviewDetail(
        reviewId: Long,
        onSuccess: (GetReviewRes) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        reviewService.getReviewDetail(reviewId).enqueue(object : Callback<GetReviewRes> {
            override fun onResponse(call: Call<GetReviewRes>, response: Response<GetReviewRes>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: onFailure(Exception("No data received."))
                } else {
                    onFailure(Exception("Failed to receive a valid response."))
                }
            }

            override fun onFailure(call: Call<GetReviewRes>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteReview(
        reviewId: Long,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        reviewService.deleteReview(reviewId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(Exception("Failed to delete the review."))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t)
            }
        })
    }


    private fun createRequestBody(content: String, mediaType: String)=
        content.toRequestBody(mediaType.toMediaTypeOrNull())

    private fun createMultipartBodyPart(partName: String, file: File, mediaType: String): MultipartBody.Part {
        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }


}

