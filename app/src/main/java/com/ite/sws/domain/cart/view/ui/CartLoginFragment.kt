package com.ite.sws.domain.cart.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.databinding.FragmentCartLoginBinding
import com.ite.sws.domain.cart.api.repository.CartRepository
import com.ite.sws.domain.cart.data.PostCartLoginReq
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.hideBottomNavigation

/**
 * 장바구니 로그인 프래그먼트
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	남진수       최초 생성
 * 2024.09.17  	김민정       로그인 후, 딥링크에 따른 프래그먼트 전환 처리
 * </pre>
 */
class CartLoginFragment : Fragment() {

    private var _binding: FragmentCartLoginBinding? = null
    private val binding get() = _binding!!
    private val cartRepository = CartRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartLoginBinding.inflate(inflater, container, false)

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cartLoginBtn.setOnClickListener {
            val loginId = binding.tvNicknameInput.text.toString().trim()
            val password = binding.tvPasswordInput.text.toString().trim()
            val cartId = SharedPreferencesUtil.getCartId()

            val postCartLoginReq = PostCartLoginReq(loginId, password, cartId)

            if (loginId.isNotEmpty() && password.isNotEmpty()) {
                // 로그인 요청
                cartRepository.login(postCartLoginReq) { result ->
                    result.fold(
                        onSuccess = { jwtToken ->
                            saveAccessToken(jwtToken.accessToken)
                            saveName(loginId)
                            (activity as? MainActivity)?.navigateToNextScreenAfterLogin() // 로그인 후 딥링크 처리
                        },
                        onFailure = { errorRes ->
                            CustomDialog(
                                layoutId = R.layout.dialog_text1_btn1,
                                title = "${errorRes.message}",
                                confirmText = "확인",
                                onConfirm = {}
                            ).show(activity?.supportFragmentManager!!, "CustomDialog")
                        }
                    )
                }
            } else {
                Toast.makeText(requireContext(), "닉네임과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * JWT 토큰 저장
     */
    private fun saveAccessToken(token: String) {
        SharedPreferencesUtil.setAccessToken(token)
    }

    /**
     * 사용자 이름 저장
     */
    private fun saveName(name: String) {
        SharedPreferencesUtil.setCartMemberName(name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
