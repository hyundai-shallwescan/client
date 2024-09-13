package com.ite.sws.domain.sharelist.data

/**
 * 공유체크리스트 아이템 조회 DTO
 * @author 김민정
 * @since 2024.09.12
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.12  김민정       최초 생성
 * </pre>
 */
data class GetShareListRes (
    val cartId: Long,
    val items: List<ShareListItem>
)