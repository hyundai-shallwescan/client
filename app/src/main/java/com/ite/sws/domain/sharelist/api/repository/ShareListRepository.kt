package com.ite.sws.domain.sharelist.api.repository

import com.google.gson.Gson
import com.ite.sws.common.RetrofitClient
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.sharelist.api.service.ShareListService
import com.ite.sws.domain.sharelist.data.PostShareListItemReq
import com.ite.sws.domain.sharelist.data.ShareListItem
import retrofit2.Response

/**
 * 공유 체크 리스트 Repository
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * 2024.09.12  김민정       공유체크리스트 아이템 조회
 * 2024.09.12  김민정       공유체크리스트 아이템 추가
 * 2024.09.12  김민정       공유체크리스트 아이템 체크 상태 변경
 * 2024.09.12  김민정       공유체크리스트 아이템 체크 삭제
 * 2024.09.12  김민정       공통 응답 처리 함수
 * 2024.09.12  김민정       공통 네트워크 예외 처리 함수
</pre> *
 */
class ShareListRepository {

    private val shareListService =
        RetrofitClient.instance.create(ShareListService::class.java)

    /**
     * 공유체크리스트 아이템 추가
     */
    suspend fun saveShareListItem(request: PostShareListItemReq) {
        try {
            val response = shareListService.saveShareListItem(request)
            handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 공유체크리스트 아이템 조회
     */
    suspend fun findShareList(cartId: Long): List<ShareListItem> {
        return try {
            val response = shareListService.findShareList(cartId)
            handleResponse(response)?.items ?: emptyList()
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 공유체크리스트 아이템 삭제
     */
    suspend fun removeShareListItem(cartId: Long, productId: Long) {
        try {
            val response = shareListService.removeShareListItem(cartId, productId)
            handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 공유체크리스트 아이템 수량 변경
     */
    suspend fun modifyshareListItem(cartId: Long, productId: Long) {
        try {
            val response = shareListService.modifyshareListItem(cartId, productId)
            handleResponse(response)
        } catch (e: Exception) {
            throw handleNetworkException(e)
        }
    }

    /**
     * 공통 응답 처리 함수
     */
    private fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception(response.errorBody()?.string())
        }
    }

    /**
     * 공통 네트워크 예외 처리 함수
     */
    private fun handleNetworkException(e: Exception): Exception {
        return try {
            // 에러 메시지가 JSON 형식일 경우 ErrorRes로 파싱
            val errorRes = Gson().fromJson(e.message, ErrorRes::class.java)
            Exception(errorRes.message)
        } catch (jsonEx: Exception) {
            // JSON 파싱 실패 시, 일반 네트워크 에러로 처리
            Exception("Network error: ${e.localizedMessage}")
        }
    }
}
