package com.ite.sws.domain.review.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.ite.sws.R
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentReviewBinding
import com.ite.sws.domain.product.view.ui.ProductFragment
import com.ite.sws.domain.review.api.repository.ReviewRepository
import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar


class ReviewDetailFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private var shortFormId: Long = 0
    private val binding get() = _binding!!
    private lateinit var review: GetReviewRes
    private var isVideoPlaying = false
    private var productId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)

        setupToolbar(binding.toolbar.toolbar, "리뷰", true)

        shortFormId = arguments?.getLong("shortFormId") ?: 0

        loadReview(shortFormId)

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        isVideoPlaying = false


        binding.productDetailBtn.setOnClickListener {
            navigateToProductDetail()
        }

        return binding.root
    }

    private fun loadReview(reviewId: Long) {
        ReviewRepository().getReviewDetail(reviewId, onSuccess = { fetchedReview ->
            review = fetchedReview
            productId = fetchedReview.productId
            setupVideoPlayer(fetchedReview.shortFormUrl)
        }, onFailure = { throwable ->
            Toast.makeText(
                requireContext(),
                "리뷰 영상 보기 실패: ${throwable.message}",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun setupVideoPlayer(videoUrl: String?) {
        videoUrl?.let { url ->
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(binding.reviewDetailView)

            mediaController.visibility = View.GONE

            binding.reviewDetailView.apply {
                setMediaController(mediaController)
                setVideoPath(url)

                setOnPreparedListener { mediaPlayer ->
                    adjustVideoViewSize(mediaPlayer.videoWidth to mediaPlayer.videoHeight)
                    mediaPlayer.isLooping = true
                    isVideoPlaying = true

                    this@apply.start()
                }

                setOnCompletionListener {
                    isVideoPlaying = false
                }

                setOnErrorListener { _, what, extra ->
                    Toast.makeText(requireContext(), "Error playing video: $what", Toast.LENGTH_SHORT).show()
                    true
                }

                setOnTouchListener { _, _ ->
                    mediaController.visibility = View.VISIBLE
                    true
                }
            }

            binding.reviewDetailView.visibility = View.VISIBLE
        } ?: run {
            Toast.makeText(requireContext(), "비디오 URL을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToProductDetail() {
        val productFragment = ProductFragment()
        val bundle = Bundle().apply {
            productId?.let { putLong("productId", it) }
        }
        productFragment.arguments = bundle
        replaceFragmentWithAnimation(R.id.container_main, productFragment, true)
    }

    private fun adjustVideoViewSize(dimensions: Pair<Int, Int>) {
        val (videoWidth, videoHeight) = dimensions
        val videoViewParam = binding.reviewDetailView.layoutParams as ConstraintLayout.LayoutParams

        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val toolbarHeight = binding.toolbar.toolbar.height

        val availableHeight = screenHeight - toolbarHeight

        val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val newHeight = (screenWidth / aspectRatio).toInt()

        videoViewParam.width = screenWidth
        videoViewParam.height = if (newHeight > availableHeight) availableHeight else newHeight

        binding.reviewDetailView.layoutParams = videoViewParam
        binding.reviewDetailView.requestLayout()
    }

    override fun onPause() {
        super.onPause()
        if (!isVideoPlaying) {
            binding.reviewDetailView.start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVideoPlaying) {
            binding.reviewDetailView.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
