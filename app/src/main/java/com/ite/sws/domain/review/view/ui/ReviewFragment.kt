package com.ite.sws.domain.review.view.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.domain.review.api.repository.ReviewRepository

import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.domain.review.view.adapter.ReviewAdapter
import com.ite.sws.databinding.FragmentReviewBinding
import com.ite.sws.domain.product.view.ui.ProductFragment
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 리뷰 프래그먼트
 * @author 정은지
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	정은지       최초 생성
 * </pre>
 */
class ReviewFragment : Fragment() {
    private var currentProductId: Long? = null
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var reviews: MutableList<GetReviewRes>
    private var currentPlayingViewHolder: ReviewAdapter.ReviewViewHolder? = null
    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)

        setupToolbar(binding.toolbar.toolbar, "리뷰", true)
        setupRecyclerView()

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, false)
        }


        loadReviews(currentPage)

        binding.productDetailBtn.setOnClickListener {
            navigateToProductDetail()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewReviews.layoutManager = LinearLayoutManager(context)
        reviews = mutableListOf()
        reviewAdapter = ReviewAdapter(reviews)
        binding.recyclerViewReviews.adapter = reviewAdapter

        binding.recyclerViewReviews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleAutoPlayVideo()

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && !isLastPage && lastVisibleItemPosition >= totalItemCount - 1) {
                    currentPage++
                    loadReviews(currentPage)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handleAutoPlayVideo()
                }
            }
        })
    }

    private fun loadReviews(page: Int) {
        isLoading = true

        ReviewRepository().getReviews(page = page, size = pageSize, onSuccess = { fetchedReviews ->
            if (fetchedReviews.isNotEmpty()) {
                reviews.addAll(fetchedReviews)
                reviewAdapter.notifyDataSetChanged()


                if (fetchedReviews.size < pageSize) {
                    isLastPage = true
                }
            } else {
                isLastPage = true
            }
            isLoading = false
        }, onFailure = { throwable ->
            Toast.makeText(requireContext(), "리뷰 영상 보기 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
        })
    }

    private fun handleAutoPlayVideo() {
        val layoutManager = binding.recyclerViewReviews.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
            val viewHolder = binding.recyclerViewReviews.findViewHolderForAdapterPosition(i) as? ReviewAdapter.ReviewViewHolder
            viewHolder?.let {
                if (isFullyVisible(it.itemView)) {
                    if (currentPlayingViewHolder != viewHolder) {
                        currentPlayingViewHolder?.stopVideo()
                        currentPlayingViewHolder = viewHolder
                        currentProductId = reviews[i].productId
                        viewHolder.playVideo()
                    }
                } else {
                    viewHolder.stopVideo()
                }
            }
        }
    }

    private fun isFullyVisible(view: View): Boolean {
        val itemRect = Rect()
        val isVisible = view.getGlobalVisibleRect(itemRect)
        return isVisible && itemRect.height() == view.height
    }

    private fun navigateToProductDetail() {
        val productFragment = ProductFragment()
        val bundle = Bundle().apply {
            currentProductId?.let { value -> putLong("productId", value) }
        }
        productFragment.arguments = bundle
        replaceFragmentWithAnimation(R.id.container_main, productFragment, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
