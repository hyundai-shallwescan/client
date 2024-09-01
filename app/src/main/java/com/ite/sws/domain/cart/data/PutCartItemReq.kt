package com.ite.sws.domain.cart.data

/**
 * 장바구니 아이템 등록 DTO
 * @author 김민정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  김민정       최초 생성
 * </pre>
 */
data class PutCartItemReq (
    val barcode : String
)