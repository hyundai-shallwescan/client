package com.ite.sws.domain.member.view.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentMyReviewBinding
import com.ite.sws.domain.review.view.ui.WriteReviewFragment
import com.ite.sws.util.hideBottomNavigation

import setupToolbar

/**
 * 리뷰 관리 프래그먼트
 * @author 정은지
 * @since 2024.09.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자       수정내용
 * ----------  --------    ---------------------------
 * 2024.09.02  	정은지       최초 생성
 * </pre>
 */
class MyReviewFragment : Fragment() {

    private var _binding: FragmentMyReviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReviewBinding.inflate(inflater, container, false)

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        setupToolbar(binding.toolbar.toolbar, "리뷰 관리", true)

        // 기본 프래그먼트 설정: 작성한 리뷰 프래그먼트를 기본으로 보여줌
        replaceFragment(WrittenReviewFragment())

        btnSettings()

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_review, fragment)
        transaction.commit()


    }

    /**
     * 버튼 이벤트 설정
     */
    private fun btnSettings() {
        // 작성한 리뷰 버튼
        binding.btnWrittenReview.setOnClickListener {
            moveSliderToWrittenReview()
            updateButtonColors(binding.btnWrittenReview, binding.btnWriteReview)
            replaceFragment(WrittenReviewFragment())
        }

        // 리뷰 작성 버튼
        binding.btnWriteReview.setOnClickListener {
            moveSliderToWriteReview()
            updateButtonColors(binding.btnWriteReview, binding.btnWrittenReview)
            replaceFragment(WriteReviewFragment())
        }
    }

    /**
     * 슬라이더를 작성한 리뷰 버튼 위치로 이동시키는 애니메이션을 실행
     */
    private fun moveSliderToWrittenReview() {
        val animator = ValueAnimator.ofFloat(binding.slider.translationX, 0f)
        animator.addUpdateListener { animation ->
            binding.slider.translationX = animation.animatedValue as Float
        }
        animator.start()
    }

    /**
     * 슬라이더를 리뷰 작성 버튼 위치로 이동시키는 애니메이션을 실행
     */
    private fun moveSliderToWriteReview() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}