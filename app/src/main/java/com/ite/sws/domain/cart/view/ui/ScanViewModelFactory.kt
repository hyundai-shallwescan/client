package com.ite.sws.domain.cart.view.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 스캔앤고 ViewModelFactory
 * : ScanViewModel을 생성할 때 Context를 주입하기 위해 사용
 *
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03   김민정       최초 생성
 * </pre>
 */
class ScanViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    /**
     * ViewModel의 인스턴스를 생성 및 반환
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}