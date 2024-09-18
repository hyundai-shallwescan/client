package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentParkingPaymentBinding
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 주차정산 결제 프래그먼트
 * @author 남진수
 * @since 2024.09.18
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.18  	남진수       최초 생성
 * </pre>
 */
class ParkingPaymentFragment : Fragment() {
    private var _binding: FragmentParkingPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkingPaymentBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "주차 정산", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스피너 설정
        setupSpinner()

        // 버튼 설정
        btnSettings()

        // 결제 금액 설정
        setupPrice()
    }

    /**
     * 결제 수단 스피너 설정
     */
    private fun setupSpinner() {
        // Spinner에 표시될 결제 수단 배열
        val paymentMethods = arrayOf("현대백화점카드", "무통장입금", "신용카드", "토스페이", "네이버페이")

        // Spinner 설정
        val spinner: Spinner = binding.spinnerPaymentMethod
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_payment_method,  // 각 항목의 레이아웃
            paymentMethods                        // 항목 데이터
        )
        spinner.adapter = adapter
    }

    /**
     * 결제 금액 설정
     */
    private fun setupPrice() {
        // Bundle에서 totalPrice 값을 가져와서 표시
        val totalPrice = arguments?.getInt("totalPrice") ?: 0
        binding.tvPaymentTotalPrice.text = "${formatCurrencyWithCommas(totalPrice)}"
    }

    private fun btnSettings() {
        // 결제 버튼 (btnPay)
        binding.btnPay.setOnClickListener {
            navigateToPaymentCardFragment()
        }
    }

    /**
     * 결제 카드 선택 프레그먼트로 이동
     */
    private fun navigateToPaymentCardFragment() {
        val fragment = PaymentCardFragment()
        val bundle = Bundle()

        arguments?.let {
            bundle.putAll(it)
        }

        // 결제 수단 번들에 추가
        bundle.putString("paymentType", binding.spinnerPaymentMethod.selectedItem.toString())

        fragment.arguments = bundle

        replaceFragmentWithAnimation(
            R.id.container_main,
            fragment,
            false,
            false
        )
    }
}