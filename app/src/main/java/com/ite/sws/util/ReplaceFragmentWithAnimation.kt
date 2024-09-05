package com.ite.sws.util

import androidx.fragment.app.Fragment
import com.ite.sws.R

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
 * 2024.09.04   정은지       커스텀 애니메이션 설정
 * </pre>
 */
fun Fragment.replaceFragmentWithAnimation(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    setAnimation: Boolean = true
    ) {
    val fragmentManager = requireActivity().supportFragmentManager
    val transaction = fragmentManager.beginTransaction()

    /**
     * 프레그먼트 전환 애니메이션
     */
    if (setAnimation) {
        transaction.setCustomAnimations(
            R.anim.anim_fade_in,  // 들어올 때 애니메이션
            R.anim.anim_fade_out, // 나갈 때 애니메이션
            R.anim.anim_fade_in,  // 뒤로 돌아올 때 애니메이션
            R.anim.anim_fade_out  // 뒤로 갈 때 애니메이션
        )
    }

    transaction.replace(containerId, fragment)

    /**
     * 백스택에 추가 여부
     */
    if (addToBackStack) {
        transaction.addToBackStack(null)
    }
    transaction.commit()
}

