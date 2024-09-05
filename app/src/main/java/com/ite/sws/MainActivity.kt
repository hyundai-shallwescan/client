package com.ite.sws

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ite.sws.databinding.ActivityMainBinding
import com.ite.sws.domain.cart.view.ui.CartLoginFragment
import com.ite.sws.domain.cart.view.ui.ContainerFragment
import com.ite.sws.domain.member.view.ui.LoginFragment
import com.ite.sws.util.SharedPreferencesUtil

/**
 * 메인 액티비티
 * @author 정은지
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	정은지       최초 생성 및 네비게이션바 추가
 * 2024.08.31   정은지       화면 전환에 따른 FAB 아이콘 및 배경 변경
 * 2024.09.01   김민정       ContainerFragment로 시작 프래그먼트
 * 2024.09.02   남진수       딥링크 연결 처리
 * 2024.09.03   정은지       로그인 여부에 따른 화면 이동 처리
 * </pre>
 */
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationMain.itemIconTintList = null

        val navController =
            supportFragmentManager.findFragmentById(binding.containerMain.id)?.findNavController()
        navController?.let {
            binding.navigationMain.setupWithNavController(it)
        }

        // 딥링크 처리
        if (intent?.action == Intent.ACTION_VIEW && intent?.data != null) {
            // 딥링크로 접근한 경우에는 멤버 로그인 여부를 확인하지 않고 딥링크 처리
            handleDeeplink(intent)
        } else {
            // 딥링크로 접근하지 않은 경우 멤버 로그인 여부를 확인
            val accessToken = SharedPreferencesUtil.getAccessToken()
            if (accessToken.isNullOrEmpty()) {
                // 액세스 토큰이 없으면 로그인 화면으로 이동
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container_main, LoginFragment())
                    .addToBackStack(null)
                    .commit()
                return
            }

            // 액세스 토큰이 있을 경우 초기 프래그먼트 설정
//            if (savedInstanceState == null) {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, ContainerFragment())
//                    .commit()
//            }
        }

        // 화면 전환 시 FAB 아이콘 및 배경 변경
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_container -> {
                    // 스캔앤고 메뉴가 선택되었을 때
                    binding.btnScan.setImageResource(R.drawable.ic_nav_scan_on)
                    binding.btnScan.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.main)
                }
                else -> {
                    // 다른 메뉴가 선택되었을 때
                    binding.btnScan.setImageResource(R.drawable.ic_nav_scan_off)
                    binding.btnScan.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.white)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleDeeplink(it) }
    }

    /**
     * 딥링크 처리
     */
    private fun handleDeeplink(intent: Intent) {
        val action: String? = intent.action
        val data: Uri? = intent.data

        if (action == Intent.ACTION_VIEW) {
            val cartIdString = data?.getQueryParameter("cartId")
            val cartId: Long? = cartIdString?.toLongOrNull()

            if (cartId != null) {
                Log.d("DeepLink", "cartId: $cartId")
                SharedPreferencesUtil.setCartId(cartId)
                navigateToCartLogin(cartId)
            } else {
                Log.d("DeepLink", "잘못된 cartId")
            }
        }
    }

    /**
     * 장바구니 로그인 화면으로 이동
     */
    private fun navigateToCartLogin(cartId: Long) {
        val fragment = CartLoginFragment().apply {
            arguments = Bundle().apply {
                putLong("cartId", cartId)
            }
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .replace(R.id.container_main, fragment)
            .addToBackStack(null)
            .commit()
    }
}