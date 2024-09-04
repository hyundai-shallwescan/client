package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentMyReviewBinding
import com.ite.sws.util.hideBottomNavigation

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

        return binding.root
    }
}