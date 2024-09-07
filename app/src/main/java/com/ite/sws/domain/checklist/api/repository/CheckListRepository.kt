package com.ite.sws.domain.checklist.api.repository

import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.CheckStatus
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.checklist.api.service.CheckListService
import com.ite.sws.domain.checklist.data.GetCheckListRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 회원 Repository
 * @author 정은지
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  정은지        최초생성
 * 2024.09.06  정은지        체크리스트 조회 추가
 * 2024.09.06  정은지        체크리스트 상태 변경 추가
 * </pre>
 */
class CheckListRepository {

    private val checkListService = RetrofitClient.instance.create(CheckListService::class.java)

    /**
     * 체크리스트 조회
     */
    fun findMyCheckList(
        onSuccess: (List<GetCheckListRes>) -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        checkListService.findMyCheckList().enqueue(object : Callback<List<GetCheckListRes>> {
            override fun onResponse(
                call: Call<List<GetCheckListRes>>,
                response: Response<List<GetCheckListRes>>
            ) {
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()

                    val modifiedItems = items.map { item ->
                        item.copy(status = CheckStatus.fromStatus(item.status.toString()))
                    }

                    onSuccess(modifiedItems)
                } else {
                    val errorRes =
                        Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }
            override fun onFailure(call: Call<List<GetCheckListRes>>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 체크리스트 상태 변경
     */
    fun modifyCheckListItemStatus(
        myCheckListItemId: Long,
        onSuccess: () -> Unit,
        onFailure: (ErrorRes) -> Unit
    ) {
        checkListService.modifyCheckListItemStatus(myCheckListItemId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorRes = Gson().fromJson(response.errorBody()?.string(), ErrorRes::class.java)
                    onFailure(errorRes)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                handleFailure(call, t, onFailure)
            }
        })
    }

    /**
     * 공통 처리 함수
     */
    private fun <T> handleFailure(call: Call<T>, t: Throwable, onFailure: (ErrorRes) -> Unit) {
        val networkError = ErrorRes(
            status = 0,
            errorCode = "NETWORK_ERROR",
            message = t.localizedMessage ?: "Unknown network error"
        )
        onFailure(networkError)
    }
}