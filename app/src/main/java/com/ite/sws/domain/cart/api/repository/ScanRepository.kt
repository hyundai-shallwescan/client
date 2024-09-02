package com.ite.sws.domain.cart.api.repository

import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.cart.api.service.ScanService
import com.ite.sws.domain.cart.data.PutCartItemReq

/**
 * 스캔앤고 Repository class
 * @author 김민정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  김민정       최초 생성
 * 2024.08.31  김민정       장바구니 아이템 추가
 * </pre>
 */
class ScanRepository {

    private val scanService =
        RetrofitClient.instance.create(ScanService::class.java)

    /**
     * 장바구니 아이템 추가
     */
    suspend fun addCartItem(request: PutCartItemReq) {
        scanService.addCartItem(request)
    }
}