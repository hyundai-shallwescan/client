package com.ite.sws.util

import android.view.View
import com.ite.sws.databinding.ActivityMainBinding

/**
 * 내비게이션 바 노출 설정 함수
 * @author 정은지
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자         수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02   정은지        최초 생성
 * </pre>
 *
 * @param binding ActivityMainBinding 객체
 * @param state ture일 경우 숨김, false일 경우 노출
 */
fun hideBottomNavigation(binding: ActivityMainBinding, state: Boolean) {
    if (state) {
        binding.navigationMain.visibility = View.GONE
        binding.btnScan.visibility = View.GONE
    } else {
        binding.navigationMain.visibility = View.VISIBLE
        binding.btnScan.visibility = View.VISIBLE
    }
}
