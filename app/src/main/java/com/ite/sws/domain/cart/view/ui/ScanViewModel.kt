package com.ite.sws.domain.cart.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ite.sws.domain.cart.api.repository.ScanRepository
import com.ite.sws.domain.cart.data.PutCartItemReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 스캔앤고 ViewModel
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
class ScanViewModel : ViewModel() {

    private val scanRepository = ScanRepository()

    // 서버 요청 결과를 저장하는 LiveData
    private val _scanResult = MutableLiveData<Result<Unit>>()
    val scanResult: LiveData<Result<Unit>> = _scanResult

    /**
     * 장바구니 아이템 추가
     * - 스캔한 바코드를 서버로 전송
     */
    fun putCartItem(barcode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                scanRepository.addCartItem(PutCartItemReq(barcode))
                _scanResult.postValue(Result.success(Unit))
            } catch (e: Exception) {
                _scanResult.postValue(Result.failure(e))
            }
        }
    }

}