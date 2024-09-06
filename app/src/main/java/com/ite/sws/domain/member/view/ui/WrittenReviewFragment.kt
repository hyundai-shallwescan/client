package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ite.sws.databinding.FragmentMyReviewWrittenBinding
import com.ite.sws.domain.member.data.GetMemberReviewRes
import com.ite.sws.domain.member.view.adapter.MyReviewRecyclerViewAdapter

/**
 * 작성 리뷰 조회 프래그먼트
 * @author 정은지
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	정은지       최초 생성
 * </pre>
 */
class WrittenReviewFragment : Fragment() {

    private var _binding: FragmentMyReviewWrittenBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: MyReviewRecyclerViewAdapter
    private val reviewList = mutableListOf<GetMemberReviewRes>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReviewWrittenBinding.inflate(inflater, container, false)

        setupRecyclerView()

        loadReviews()

        return binding.root
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.rvWrittenReviews.layoutManager = gridLayoutManager

        reviewAdapter = MyReviewRecyclerViewAdapter(reviewList)
        binding.rvWrittenReviews.adapter = reviewAdapter
    }

    // 리뷰 데이터를 로드하는 메서드
    private fun loadReviews() {
        val sampleReview1 = GetMemberReviewRes(1L, "url", "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmDj1d%2FbtroWDhqP4j%2FolDh8NFhgtLtWQ6krrJdxK%2Fimg.png")
        val sampleReview2 = GetMemberReviewRes(2L, "url", "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmDj1d%2FbtroWDhqP4j%2FolDh8NFhgtLtWQ6krrJdxK%2Fimg.png")

        reviewList.add(sampleReview1)
        reviewList.add(sampleReview2)

        reviewAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
