package com.ite.sws.domain.cart.view.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ite.sws.R
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.common.WebSocketClient
import com.ite.sws.common.data.replaceFragmentWithAnimation
import com.ite.sws.databinding.FragmentScanBinding
import com.ite.sws.domain.chat.view.ui.ChatFragment
import ua.naiksoftware.stomp.dto.LifecycleEvent

/**
 * 스캔앤고 프래그먼트
 * @author 정은지
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	정은지       최초 생성
 * 2024.09.01  	남진수       WebSocket 연결
 * </pre>
 */
class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jwtToken = SharedPreferencesUtil.getString(requireContext(), "jwt_token")

        if (jwtToken.isNullOrEmpty() || !isTokenValid(jwtToken)) {
            findNavController().navigate(R.id.action_scanFragment_to_cartLoginFragment)
            return
        }

        /**
         * WebSocket 연결
         */
        WebSocketClient.connect(jwtToken) { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("STOMP", "WebSocket opened")
                    val cartId = SharedPreferencesUtil.getLong(requireContext(), "cart_id")
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
        setupClickListeners()
    }

    private fun isTokenValid(token: String): Boolean {

        return true
    }

    /**
     * 특정 장바구니에 구독
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

    /**
     * 채팅으로 이동
     */
    private fun setupClickListeners() {
        binding.button3.setOnClickListener {
            replaceFragmentWithAnimation(
                containerId = R.id.container_main,
                fragment = ChatFragment()
            )
        }
    }

}