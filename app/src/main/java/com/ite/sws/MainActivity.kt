package com.ite.sws

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ite.sws.databinding.ActivityMainBinding
import com.ite.sws.domain.cart.view.ui.ContainerFragment

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
 * </pre>
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationMain.itemIconTintList = null

        val navController = supportFragmentManager.findFragmentById(binding.containerMain.id)?.findNavController()
        navController?.let {
            binding.navigationMain.setupWithNavController(it)
        }

        // 화면 전환 시 FAB 아이콘 및 배경 변경
        navController?.addOnDestinationChangedListener() { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_container -> {
                    // 스캔앤고 메뉴가 선택되었을 때
                    binding.btnScan.setImageResource(R.drawable.ic_nav_scan_on)
                    binding.btnScan.backgroundTintList = ContextCompat.getColorStateList(this, R.color.main)
                }
                else -> {
                    // 다른 메뉴가 선택되었을 때
                    binding.btnScan.setImageResource(R.drawable.ic_nav_scan_off)
                    binding.btnScan.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                }
            }
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ContainerFragment())
                .commit()
        }

    }
}