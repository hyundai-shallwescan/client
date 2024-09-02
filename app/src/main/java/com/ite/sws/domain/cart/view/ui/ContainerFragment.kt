package com.ite.sws.domain.cart.view.ui

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.ite.sws.R
import com.ite.sws.domain.sharelist.view.ui.ShareListFragment

/**
 * 스캔앤고 내부 컨테이너(장바구니+일행요청리스트) 총괄 프래그먼트
 * @author 김민정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * </pre>
 */
class ContainerFragment : Fragment() {

    private lateinit var slider: View
    private lateinit var btnCart: Button
    private lateinit var btnShareList: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_container, container, false)

        slider = view.findViewById(R.id.slider)
        btnCart = view.findViewById(R.id.btn_cart)
        btnShareList = view.findViewById(R.id.btn_share_list)

        // 초기 프래그먼트 설정
        loadFragment(ScanFragment())

        btnCart.setOnClickListener {
            moveSliderToCart()
            loadFragment(ScanFragment())
        }

        btnShareList.setOnClickListener {
            moveSliderToShareList()
            loadFragment(ShareListFragment())
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun moveSliderToCart() {
        val animator = ValueAnimator.ofFloat(slider.translationX, 0f)
        animator.addUpdateListener { animation ->
            slider.translationX = animation.animatedValue as Float
        }
        animator.start()
    }

    private fun moveSliderToShareList() {
        val animator = ValueAnimator.ofFloat(slider.translationX, slider.width.toFloat())
        animator.addUpdateListener { animation ->
            slider.translationX = animation.animatedValue as Float
        }
        animator.start()
    }
}
