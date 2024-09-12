package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentPaymentCardBinding
import com.ite.sws.util.NumberFormatterUtil.formatWithComma
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 결제 방법 선택 프래그먼트
 * @author 김민정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10   김민정       최초 생성
 * </pre>
 */
class PaymentCardFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentPaymentCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentCardBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "결제", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 결제 총 금액 설정
        setupProductInfo()

        // 버튼 설정
        btnSettings()
    }

    /**
     * 결제 총 금액을 View에 설정
     */
    private fun setupProductInfo() {
        binding.apply {
            tvTotalPrice.text = arguments?.getInt("totalPrice")
                ?.let { formatWithComma(it) }
        }
    }

    /**
     * 버튼 이벤트 설정
     */
    private fun btnSettings() {
        // 각 레이아웃에 클릭 이벤트 설정
        binding.layoutAppCardPayment.setOnClickListener {
            navigateToPaymentPasswordFragment()
        }

        binding.layoutEasyPayment.setOnClickListener {
            navigateToPaymentPasswordFragment()
        }

        binding.layoutGeneralPayment.setOnClickListener {
            navigateToPaymentPasswordFragment()
        }
    }

    /**
     * 결제 비밀번호 프래그먼트로 이동 및 데이터 전달
     */
    private fun navigateToPaymentPasswordFragment() {
        val fragment = PaymentPasswordFragment()
        val bundle = Bundle()

        // 이전 프래그먼트에서 받은 데이터를 번들에 추가
        arguments?.let {
            bundle.putAll(it)
        }

        fragment.arguments = bundle

        replaceFragmentWithAnimation(
            R.id.container_main,
            fragment,
            false,
            false
        )
    }
}