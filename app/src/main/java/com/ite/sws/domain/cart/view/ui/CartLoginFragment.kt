package com.ite.sws.domain.cart.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ite.sws.R
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.databinding.FragmentCartLoginBinding
import com.ite.sws.domain.cart.api.repository.CartRepository
import com.ite.sws.domain.cart.data.PostCartLoginReq
import com.ite.sws.util.replaceFragmentWithAnimation

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

            val postCartLoginReq = PostCartLoginReq(loginId, password)

            if (loginId.isNotEmpty() && password.isNotEmpty()) {
                // 로그인 요청
                cartRepository.login(postCartLoginReq,
                    onSuccess = { jwtToken ->
                        saveJwtToken(jwtToken.accessToken)
                        // 로그인 성공 시 사용자 이름 저장
                        SharedPreferencesUtil.saveString(requireContext(), "name", loginId)
                        binding.nicknameTitle.text = "로그인 성공 AccessToken: ${jwtToken.accessToken}"
                        navigateToNextScreen()
                    },
                    onFailure = { errorRes ->
                        binding.passwordTitle.text = "로그인 실패: ${errorRes.message}"
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Please enter both ID and Password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * JWT 토큰 저장
     */
    private fun saveJwtToken(token: String) {
        SharedPreferencesUtil.saveString(requireContext(), "jwt_token", token)
    }

    /**
     * 로그인 이후 화면 전환
     */
    private fun navigateToNextScreen() {
        replaceFragmentWithAnimation(
            R.id.container_main,
            ScanFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
