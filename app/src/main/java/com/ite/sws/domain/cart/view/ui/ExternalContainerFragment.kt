package com.ite.sws.domain.cart.view.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.common.WebSocketClient
import com.ite.sws.databinding.FragmentExternalContainerBinding
import com.ite.sws.domain.chat.view.ui.ChatFragment
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import ua.naiksoftware.stomp.dto.LifecycleEvent

/**
 * [외부일행] 스캔앤고 내부 컨테이너(장바구니+요청리스트) 총괄 프래그먼트
 *
 * : 각 버튼(장바구니, 요청 리스트)을 클릭하면 해당되는 프래그먼트를 로드하고 슬라이더 애니메이션을 실행
 *
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * 2024.09.03  김민정       주어진 프래그먼트를 fragment_container에 로드
 * 2024.09.03  김민정       슬라이더 버튼 애니메이션
 * 2024.09.03  김민정       버튼 색상 업데이트
 * 2024.09.03  김민정       채팅 프래그먼트 전환
 * 2024.09.04  남진수       WebSocket 연결
 * </pre>
 */
class ExternalContainerFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentExternalContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalContainerBinding.inflate(inflater, container, false)

        // 네비게이션 바 invisible
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 프래그먼트 설정
        loadFragment(ExternalCartFragment())

        // 버튼 이벤트 설정
        btnSettings()

        // WebSocket 연결
        connectWebSocket()
    }

    /**
     * 주어진 프래그먼트를 fragment_container에 로드
     * @param fragment 로드할 프래그먼트
     */
    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_external_container, fragment)
            .commit()
    }

    /**
    * 버튼 이벤트 설정
    */
    private fun btnSettings() {
        // 장바구니 버튼
        binding.btnCart.setOnClickListener {
            moveSliderToCart()
            updateButtonColors(binding.btnCart, binding.btnShareList)
            loadFragment(ExternalCartFragment())
        }

        // 공유 체크 리스트 버튼
        binding.btnShareList.setOnClickListener {
            moveSliderToShareList()
            updateButtonColors(binding.btnShareList, binding.btnCart)
            loadFragment(ExternalCartFragment())
        }

        // 채팅 버튼
        binding.btnScannerChat.setOnClickListener{
            replaceFragmentWithAnimation(R.id.container_main, ChatFragment(), true)
        }
    }

    /**
     * 슬라이더를 장바구니 버튼 위치로 이동시키는 애니메이션을 실행
     */
    private fun moveSliderToCart() {
        val animator = ValueAnimator.ofFloat(binding.slider.translationX, 0f)
        animator.addUpdateListener { animation ->
            binding.slider.translationX = animation.animatedValue as Float
        }
        animator.start()
    }

    /**
     * 슬라이더를 일행 요청 리스트 버튼 위치로 이동시키는 애니메이션을 실행
     */
    private fun moveSliderToShareList() {
        val animator = ValueAnimator.ofFloat(binding.slider.translationX, binding.slider.width.toFloat())
        animator.addUpdateListener { animation ->
            binding.slider.translationX = animation.animatedValue as Float
        }
        animator.start()
    }

    /**
     * 두 개의 버튼의 텍스트 색상을 업데이트
     * @param activeButton 활성화된 상태의 버튼
     * @param inactiveButton 비활성화된 상태의 버튼
     */
    private fun updateButtonColors(activeButton: Button, inactiveButton: Button) {
        activeButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white))
        inactiveButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black))
    }

    /**
     * WebSocket 연결
     */
    private fun connectWebSocket() {
        val accessToken = SharedPreferencesUtil.getAccessToken()
        if (accessToken != null) {
            WebSocketClient.connect(accessToken) { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.d("STOMP", "WebSocket opened")
                        val cartId = SharedPreferencesUtil.getCartId()
                        Log.d("STOMP", "Subscribing to cart $cartId")
                        // 연결이 열리면 특정 장바구니에 구독
                        subscribeToCart(cartId)
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        Log.d("STOMP", "WebSocket closed")
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Log.e("STOMP", "WebSocket error", event.exception)
                    }

                    else -> {
                        Log.d("STOMP", "WebSocket event: ${event.message}")
                    }
                }
            }
        } else {
            Log.e("STOMP", "accessToken is null")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_external_container, CartLoginFragment())
                .commit()
        }
    }

    /**
     * 장바구니 구독
     */
    private fun subscribeToCart(cartId: Long) {
        if (cartId == 0L) {
            Log.e("ScanFragment", "Invalid cartId: $cartId")
            return
        }

        val subscriptionPath = "/sub/chat/$cartId"
        Log.d("ScanFragment", "Subscribing to $subscriptionPath")

        WebSocketClient.subscribe(subscriptionPath) { message ->
            Log.i("STOMP", "Received message for cart $cartId: $message")
            activity?.runOnUiThread {

            }
        }
    }

}