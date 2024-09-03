package com.ite.sws.domain.cart.api.repository

import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.cart.api.service.ScanService
import com.ite.sws.domain.cart.data.CartItem
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
 * 2024.09.02  김민정       장바구니 아이템 조회
 * </pre>
 */
class ScanRepository {

    private val scanService =
        RetrofitClient.instance.create(ScanService::class.java)

    /**
     * 장바구니 아이템 추가
     */
    suspend fun saveCartItem(request: PutCartItemReq) {
        scanService.saveCartItem(request)
    }

    /**
     * 장바구니 아이템 조회
     */
    suspend fun findCartItemList(cartId: Int): List<CartItem> {
        return scanService.findCartItemList(cartId).items
    }
}