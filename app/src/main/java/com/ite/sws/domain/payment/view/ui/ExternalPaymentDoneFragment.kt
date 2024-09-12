package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentExternalPaymentDoneBinding
import com.ite.sws.util.hideBottomNavigation
import setupToolbar

/**
 * 외부고객 결제 완료 프래그먼트
 * @author 김민정
 * @since 2024.09.11
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.11   김민정       최초 생성
 * </pre>
 */
class ExternalPaymentDoneFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentExternalPaymentDoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalPaymentDoneBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "결제 완료", false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}