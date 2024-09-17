package com.ite.sws.domain.parking.api.repository

import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.parking.api.service.ParkingService
import com.ite.sws.domain.parking.data.GetParkingRes
import com.ite.sws.domain.payment.data.PostParkingPaymentsReq
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 주차 Repository
 * @author 남진수
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08   남진수       최초 생성
 * 2024.09.08   남진수       주차 정산 정보 조회
 * 2024.09.18   남진수       주차 정산 요청
 * </pre>
 */
class ParkingRepository {

    private val parkingService =
        RetrofitClient.instance.create(ParkingService::class.java)

    /**
     * 주차 정산 정보 조회
     */
    fun getParkingInfo(
        onSuccess: (GetParkingRes) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val call = parkingService.getParkingInfo()
        call.enqueue(object : Callback<GetParkingRes> {
            override fun onResponse(call: Call<GetParkingRes>, response: Response<GetParkingRes>) {
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

            override fun onFailure(call: Call<GetParkingRes>, t: Throwable) {
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
     * 주차 정산 요청
     */
    fun postParkingPayments(
        postParkingPaymentsReq: PostParkingPaymentsReq,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        val call = parkingService.postParkingPayments(postParkingPaymentsReq)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorRes = if (!errorBodyString.isNullOrEmpty()) {
                        try {
                            Gson().fromJson(errorBodyString, ErrorRes::class.java)
                        } catch (e: Exception) {
                            ErrorRes(status = response.code(), errorCode = "PARSE_ERROR", message = "Error parsing error response")
                        }
                    } else {
                        ErrorRes(status = response.code(), errorCode = "UNKNOWN_ERROR", message = "Unknown error occurred")
                    }
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

}
