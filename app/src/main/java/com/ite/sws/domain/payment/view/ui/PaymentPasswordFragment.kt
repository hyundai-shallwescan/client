package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentPaymentPasswordBinding
import com.ite.sws.domain.payment.data.PaymentItemDto
import com.ite.sws.domain.payment.data.PostPaymentReq
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import setupToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 결제 비밀번호 입력 프래그먼트
 * @author 김민정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10   김민정       최초 생성
 * 2024.09.10   김민정       결제 요청
 * </pre>
 */
class PaymentPasswordFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentPaymentPasswordBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: PaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentPasswordBinding.inflate(inflater, container, false)

        // 바텀 네비게이션 바 숨김
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        // 툴바 타이틀 설정
        setupToolbar(binding.toolbar.toolbar, "결제하기", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()

        // 버튼 설정
        btnSettings()
    }

    /**
     * 버튼 이벤트 설정
     */
    private fun btnSettings() {
        // 결제하기
        binding.btnPay.setOnClickListener {
            makePaymentRequest()
        }
    }

    /**
     * 결제 요청 처리
     */
    private fun makePaymentRequest() {
        // Bundle로부터 결제 정보 가져오기
        val cartId = SharedPreferencesUtil.getCartId()
        val paymentType = arguments?.getString("paymentType") ?: return
        val totalPrice = arguments?.getInt("totalPrice") ?: return
        val productIds = arguments?.getLongArray("productIds") ?: return
        val quantities = arguments?.getIntArray("quantities") ?: return

        // 현재 시간 가져오기
        val paymentTime = getCurrentTime()

        // 결제 아이템 리스트 생성
        val items = productIds.zip(quantities.map { it.toLong() }).map { (productId, quantity) ->
            PaymentItemDto(productId, quantity.toInt())
        }

        // 결제 요청 DTO 생성
        val paymentRequestDto = PostPaymentReq(
            cartId = cartId,
            totalPrice = totalPrice,
            card = paymentType,
            paymentKey = "heendyheendy",  // 결제 키 정보
            paymentTime = paymentTime,
            items = items
        )

        // ViewModel을 통해 결제 요청
        viewModel.savePayment(paymentRequestDto, onSuccess = {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }, onFailure = { errorMessage ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 현재 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 반환
     */
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {
        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}