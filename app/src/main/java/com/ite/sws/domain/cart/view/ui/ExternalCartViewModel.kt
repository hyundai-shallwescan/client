package com.ite.sws.domain.cart.view.ui

import androidx.lifecycle.ViewModel
import com.ite.sws.domain.cart.api.repository.CartRepository

/**
 * 외부인 장바구니 ViewModel
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * </pre>
 */
class ExternalCartViewModel : ViewModel() {

    private val cartRepository = CartRepository()
}