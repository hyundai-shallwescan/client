package com.ite.sws.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * LiveData를 한 번만 관찰하는 유틸리티 메서드
 * @author 김민정
 * @since 2024.09.17
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.17   김민정       최초 생성
 * </pre>
 *
 * @param lifecycleOwner 관찰을 수행할 LifecycleOwner
 * @param observer 관찰할 Observer 객체
 */
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this) // 관찰 후 즉시 제거하여 중복 관찰 방지
        }
    })
}