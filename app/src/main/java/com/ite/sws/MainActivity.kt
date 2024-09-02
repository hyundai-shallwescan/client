package com.ite.sws

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ite.sws.util.SharedPreferencesUtil
import com.ite.sws.databinding.ActivityMainBinding

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
 * </pre>
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_main)
        binding.navigationMain.itemIconTintList = null

        val navController = supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()
        navController?.let {
            binding.navigationMain.setupWithNavController(it)
        }
        intent?.let { handleDeeplink(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleDeeplink(it) }
    }

    private fun handleDeeplink(intent: Intent) {
        val action: String? = intent.action
        val data: Uri? = intent.data

        if (action == Intent.ACTION_VIEW) {
            val cartIdString = data?.getQueryParameter("cartId")
            val cartId: Long? = cartIdString?.toLongOrNull()

            if (cartId != null) {
                Log.d("DeepLink", "Received cartId as Long: $cartId")
                SharedPreferencesUtil.saveLong(this, "cart_id", cartId)
                navigateToCart(cartId)
            } else {
                Log.d("DeepLink", "cartId is null or not a valid Long")
            }
        }
    }

    private fun navigateToCart(cartId: Long) {
        val navController = supportFragmentManager.findFragmentById(R.id.container_main)?.findNavController()
        if (navController != null) {
            val bundle = Bundle().apply {
                putLong("cartId", cartId)
            }
            navController.navigate(R.id.navigation_scan, bundle)
        } else {
            Log.e("MainActivity", "NavController is not set on container_main")
        }
    }

}