package com.ite.sws.domain.payment.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentPaymentBinding
import com.ite.sws.domain.payment.view.adapter.PaymentRecyclerAdapter
import com.ite.sws.util.NumberFormatterUtil.formatCurrencyWithCommas
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 상품 결제 프래그먼트
 * @author 김민정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09   김민정       최초 생성
 * 2024.09.09   김민정       결제 수단 스피너 설정
 * 2024.09.09   김민정       결제를 위한 장바구니 아이템 조회
 * 2024.09.10   김민정       추가 결제 상품 추천
 * </pre>
 */
class PaymentFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: PaymentViewModel
    private lateinit var recyclerAdapter: PaymentRecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)

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

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        // 리사이클러뷰 설정
        setupRecyclerView()

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()

        // 스피너 설정
        setupSpinner()

        // 장바구니 아이템 가져오기
        viewModel.findPaymentItemList(SharedPreferencesUtil.getCartId())

        // 버튼 설정
        btnSettings()
    }

    /**
     * 리사이클러뷰 설정
     */
    private fun setupRecyclerView() {
        recyclerView = binding.recyclerviewPaymentProduct
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {
        // 장바구니 아이템 조회 결과 관찰
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItemsResponse ->
            recyclerAdapter = PaymentRecyclerAdapter(cartItemsResponse.items)
            recyclerView.adapter = recyclerAdapter

            binding.tvPaymentTotalPrice.text = "${formatCurrencyWithCommas(cartItemsResponse.totalPrice)}"
        }

        // 추가 상품 추천 조회 결과 관찰
        viewModel.recommendation.observe(viewLifecycleOwner) { recommendationResponse ->
            if (recommendationResponse.remainingParkingFee != null
                && recommendationResponse.remainingParkingFee != 0) {
                val bottomSheetFragment = ProductRecommendBottomSheet(
                    recommendationResponse.thumbnailImage,
                    recommendationResponse.productName,
                    formatCurrencyWithCommas(recommendationResponse.productPrice)
                ) {
                    // 결제 버튼 클릭 시 처리
                    navigateToPaymentCardFragment()
                }
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            } else {
                navigateToPaymentCardFragment()
            }
        }

        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
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
     * 버튼 이벤트 설정
     */
    private fun btnSettings() {
        // 결제 버튼
        binding.btnPay.setOnClickListener {
            viewModel.cartItems.value?.let { cartItemsResponse ->
                viewModel.findRecommendProduct(
                    SharedPreferencesUtil.getCartId(),
                    cartItemsResponse.totalPrice
                )
            } ?: Toast.makeText(requireContext(), "장바구니 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 결제 카드 선택 프레그먼트로 이동
     */
    private fun navigateToPaymentCardFragment() {
        val fragment = PaymentCardFragment()
        val bundle = Bundle()

        // 여기서 productId와 quantity 정보를 번들에 추가할 수 있음
        viewModel.cartItems.value?.let { cartItemsResponse ->
            val productIdArray = cartItemsResponse.items.map { it.productId }.toLongArray()
            val quantityArray = cartItemsResponse.items.map { it.quantity }.toIntArray()
            val totalPrice = cartItemsResponse.totalPrice

            bundle.putLongArray("productIds", productIdArray)
            bundle.putIntArray("quantities", quantityArray)
            bundle.putInt("totalPrice", totalPrice)
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