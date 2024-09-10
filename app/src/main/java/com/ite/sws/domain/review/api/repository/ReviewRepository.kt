package com.ite.sws.domain.review.api.repository

import android.media.Image
import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.review.api.service.ReviewService
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


    private fun createRequestBody(content: String, mediaType: String)=
        content.toRequestBody(mediaType.toMediaTypeOrNull())

    private fun createMultipartBodyPart(partName: String, file: File, mediaType: String): MultipartBody.Part {
        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }
}

