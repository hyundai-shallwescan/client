package com.ite.sws.domain.sharelist.api.repository

import com.ite.sws.common.BaseRepository
import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.sharelist.api.service.ShareListService
import com.ite.sws.domain.sharelist.data.PostShareListItemReq
import com.ite.sws.domain.sharelist.data.ShareListItem

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
class ShareListRepository : BaseRepository() {

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
}
