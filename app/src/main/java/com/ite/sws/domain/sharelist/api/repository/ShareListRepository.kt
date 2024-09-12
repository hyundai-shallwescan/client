package com.ite.sws.domain.sharelist.api.repository

import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.sharelist.api.service.ShareListService
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
</pre> *
 */
class ShareListRepository {

    private val shareListService =
        RetrofitClient.instance.create(ShareListService::class.java)

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
     * 공통 응답 처리 함수
     */
    private fun <T> handleResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception("Error: ${response.errorBody()?.string()}")
        }
    }

    /**
     * 공통 네트워크 예외 처리 함수
     */
    private fun handleNetworkException(e: Exception): Exception {
        return Exception("Network error: ${e.localizedMessage}")
    }
}
