package com.ite.sws.domain.cart.view.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ite.sws.R
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentExternalCartBinding
import com.ite.sws.domain.cart.data.CartItemDetail
import com.ite.sws.domain.cart.view.adapter.ExternalCartRecyclerAdapter
import com.ite.sws.domain.payment.data.PaymentDoneDTO
import com.ite.sws.domain.payment.view.ui.ExternalPaymentDoneFragment
import com.ite.sws.domain.payment.view.ui.PaymentQRFragment
import com.ite.sws.domain.product.view.ui.ProductFragment
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.SwipeHelperCallback
import com.ite.sws.util.replaceFragmentWithAnimation

/**
 * 외부일행 장바구니 프래그먼트
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	김민정       최초 생성
 * 2024.09.04  	김민정       장바구니 아이템 조회
 * 2024.09.04  	김민정       장바구니 아이템 수량 변경
 * 2024.09.04  	김민정       장바구니 아이템 삭제
 * 2024.09.05   김민정       웹소켓을 통해 실시간으로 장바구니 아이템 변경 사항을 구독
 * 2024.09.11   김민정       결제 완료 화면 이동 처리
 * 2024.09.17   김민정       상품 상세 페이지로 이동
 * </pre>
 */
class ExternalCartFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentExternalCartBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: ExternalCartViewModel
    private lateinit var recyclerAdapter: ExternalCartRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this).get(ExternalCartViewModel::class.java)

        // 리사이클러뷰 설정
        setupRecyclerView(viewModel)

        // 장바구니 아이템 가져오기
        viewModel.findCartItemList(SharedPreferencesUtil.getCartId())

        // ViewModel에서 발생한 이벤트를 관찰
        observeViewModel()

        // 공동 장바구니 아이템 변경 사항 관찰
        observeCartItemUpdate()
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView(viewModel : ExternalCartViewModel) {
        recyclerAdapter = ExternalCartRecyclerAdapter(viewModel)
        binding.recyclerviewCart.apply {
            layoutManager = LinearLayoutManager(requireContext()) // LayoutManager 설정
            adapter = recyclerAdapter
        }

        // 리사이클러뷰 아이템 터치 시 ProductFragment로 이동
        recyclerAdapter.onViewDetail = { cartItem ->
            navigateToProductDetail(cartItem.productId)
        }

        itemTouchHelperSetting()
    }

    /**
     * ItemTouchHelper 설정
     * : 스와이프 동작 추가
     */
    private fun itemTouchHelperSetting() {
        val swipeHelperCallback = SwipeHelperCallback(recyclerAdapter, viewModel)
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewCart)
    }

    /**
     * ViewModel의 LiveData 관찰
     */
    private fun observeViewModel() {

        // 장바구니 아이템 조회 결과 관찰
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (items.isNotEmpty()) {
                binding.recyclerviewCart.visibility = View.VISIBLE
                binding.layoutCartNotfound.visibility = View.GONE
                recyclerAdapter.submitList(items)
            } else {
                binding.recyclerviewCart.visibility = View.GONE
                binding.layoutCartNotfound.visibility = View.VISIBLE
            }
        }

        // 에러 상태 관찰
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 웹소켓을 통해 실시간으로 장바구니 아이템 변경 사항을 구독
     */
    private fun observeCartItemUpdate() {
        val cartId = SharedPreferencesUtil.getCartId()
        val subscriptionPath = "/sub/cart/$cartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.d("STOMP EXTERNAL CART", "Received message: $message")

            // JSON 메시지를 Map으로 파싱하여 type을 확인
            val messageMap = Gson().fromJson(message, Map::class.java)
            val messageType = messageMap["type"] as? String

            // 메시지 타입에 따라 처리
            when (messageType) {
                "cartUpdate" -> {
                    val cartItemDto = Gson().fromJson(message, CartItemDetail::class.java)
                    activity?.runOnUiThread {
                        updateCartRecyclerView(cartItemDto)
                    }
                }
                "paymentDone" -> {
                    // 결제 완료 화면 이동 처리
                    val paymentDoneDto = Gson().fromJson(message, PaymentDoneDTO::class.java)
                    activity?.runOnUiThread {
                        handlePaymentDone(paymentDoneDto)
                    }
                }
                else -> Log.e("STOMP CART", "Unknown message type: $messageType")
            }
        }
    }

    /**
     * 수신된 메시지에 따라 장바구니 리사이클러뷰 업데이트
     */
    private fun updateCartRecyclerView(cartItemDto: CartItemDetail) {
        when (cartItemDto.action) {
            "create" -> recyclerAdapter.addNewItem(cartItemDto)
            "increase" -> recyclerAdapter.increaseItemQuantity(cartItemDto)
            "decrease" -> recyclerAdapter.decreaseItemQuantity(cartItemDto)
            "delete" -> recyclerAdapter.removeItem(cartItemDto)
        }
    }

    /**
     * 결제 완료 화면 이동 처리
     */
    private fun handlePaymentDone(paymentDoneDto: PaymentDoneDTO) {
        // 회원이라면, QR(결제 인증) 화면으로 이동
        if (paymentDoneDto.cartOwnerName == SharedPreferencesUtil.getCartMemberName()) {
            // 소켓 연결 해제
            WebSocketClient.unsubscribe("/sub/cart/${SharedPreferencesUtil.getCartId()}")

            // 새로운 장바구니 ID로 변경
            SharedPreferencesUtil.setCartId(paymentDoneDto.newCartId)
            observeNewCartWebSocket(SharedPreferencesUtil.getCartId())  // 새로운 장바구니 ID로 소켓 재구독

            // QR URL 번들에 추가
            val fragment = PaymentQRFragment()
            val bundle = Bundle()
            bundle.putString("qrUrl", paymentDoneDto.qrUrl)
            fragment.arguments = bundle

            replaceFragmentWithAnimation(
                R.id.container_main,
                fragment,
                false,
                false
            )
        } else {
            // 비회원이라면, 결제 완료 화면으로 이동
            // 소켓 연결 해제
            WebSocketClient.unsubscribe("/sub/cart/${SharedPreferencesUtil.getCartId()}")

            replaceFragmentWithAnimation(
                R.id.container_main,
                ExternalPaymentDoneFragment(),
                false,
                false
            )
        }
    }

    /**
     * ProductFragment로 이동하는 메소드
     */
    private fun navigateToProductDetail(productId: Long) {
        val productFragment = ProductFragment()
        val bundle = Bundle().apply {
            putLong("productId", productId)
        }
        productFragment.arguments = bundle

        replaceFragmentWithAnimation(R.id.container_main, productFragment, true, false)
    }

    /**
     * 웹소켓 재구독
     */
    private fun observeNewCartWebSocket(newCartId: Long) {
        val subscriptionPath = "/sub/cart/$newCartId"

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.d("STOMP CART", "New cart subscription: $message")
        }
    }
}
