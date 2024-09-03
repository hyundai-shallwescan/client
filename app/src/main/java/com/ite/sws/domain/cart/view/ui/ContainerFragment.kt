package com.ite.sws.domain.cart.view.ui

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.ite.sws.R
import com.ite.sws.domain.sharelist.view.ui.ShareListFragment

/**
 * 스캔앤고 내부 컨테이너(장바구니+일행요청리스트) 총괄 프래그먼트
 *
 * : 각 버튼(장바구니, 일행 요청 리스트)을 클릭하면 해당되는 프래그먼트를 로드하고 슬라이더 애니메이션을 실행
 *
 * @author 김민정
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  김민정       최초 생성
 * 2024.09.02  김민정       주어진 프래그먼트를 fragment_container에 로드
 * 2024.09.02  김민정       슬라이더 버튼 애니메이션
 * 2024.09.03  김민정       버튼 색상 업데이트
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
            updateButtonColors(btnCart, btnShareList)
            loadFragment(ScanFragment())
        }

        btnShareList.setOnClickListener {
            moveSliderToShareList()
            updateButtonColors(btnShareList, btnCart)
            loadFragment(ShareListFragment())
        }

        return view
    }

    /**
     * 주어진 프래그먼트를 fragment_container에 로드
     * @param fragment 로드할 프래그먼트
     */
    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * 슬라이더를 장바구니 버튼 위치로 이동시키는 애니메이션을 실행
     */
    private fun moveSliderToCart() {
        val animator = ValueAnimator.ofFloat(slider.translationX, 0f)
        animator.addUpdateListener { animation ->
            slider.translationX = animation.animatedValue as Float
        }
        animator.start()
    }

    /**
     * 슬라이더를 일행 요청 리스트 버튼 위치로 이동시키는 애니메이션을 실행
     */
    private fun moveSliderToShareList() {
        val animator = ValueAnimator.ofFloat(slider.translationX, slider.width.toFloat())
        animator.addUpdateListener { animation ->
            slider.translationX = animation.animatedValue as Float
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
}
