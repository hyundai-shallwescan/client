package com.ite.sws.domain.sharelist.view.ui

import androidx.lifecycle.ViewModel
import com.ite.sws.domain.sharelist.data.ShareListItem

/**
 * 공유 체크 리스트 ViewModel 추상 클래스
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
abstract class BaseShareListViewModel : ViewModel() {
    abstract fun findShareList(cartId: Long)
    abstract fun modifyShareListCheck(cartId: Long, productId: Long)
    abstract fun removeShareListItem(cartId: Long, productId: Long)
    abstract fun updateShareListItems(newItems: List<ShareListItem>)
}