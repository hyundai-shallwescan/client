package com.ite.sws.util

import androidx.fragment.app.Fragment

/**
 * 프래그먼트 전환 공통 처리
 * @author 남진수
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02	남진수       최초 생성
 * </pre>
 */
fun Fragment.replaceFragmentWithAnimation(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    val fragmentManager = this.parentFragmentManager
    val transaction = fragmentManager.beginTransaction()

    /**
     * 프레그먼트 전환 애니메이션
     */
    transaction.setCustomAnimations(
        android.R.animator.fade_in,  // 새 프래그먼트가 들어올 때의 애니메이션
        android.R.animator.fade_out, // 현재 프래그먼트가 나갈 때의 애니메이션
        android.R.animator.fade_in,  // 뒤로 가기 할 때 새 프래그먼트가 들어올 때의 애니메이션
        android.R.animator.fade_out  // 뒤로 가기 할 때 현재 프래그먼트가 나갈 때의 애니메이션
    )

    transaction.replace(containerId, fragment)

    /**
     * 백스택에 추가 여부
     */
    if (addToBackStack) {
        transaction.addToBackStack(null)
    }
    transaction.commit()
}

