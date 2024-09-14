package com.ite.sws

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.ite.sws.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 애니메이션 로드
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim_move_laser)
        binding.lineLaser.startAnimation(animation)

        // 3초 후에 메인 액티비티로 전환
        binding.lineLaser.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}