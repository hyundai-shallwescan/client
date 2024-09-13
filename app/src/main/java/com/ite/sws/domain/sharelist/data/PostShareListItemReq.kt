package com.ite.sws.domain.sharelist.data

/**
 * 공유 체크 리스트 아이템 등록 DTO
 * @author 김민정
 * @since 2024.09.13
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.13  김민정       최초 생성
 * </pre>
 */
data class PostShareListItemReq(
    val cartId : Long,
    val productId : Long
)
