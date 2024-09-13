package com.ite.sws.domain.review.view.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentReviewBinding
import com.ite.sws.domain.review.api.repository.ReviewRepository
import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.util.hideBottomNavigation
import setupToolbar


class ReviewDetailFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private var shortFormId: Long = 0
    private val binding get() = _binding!!
    private lateinit var review: GetReviewRes
    private var isVideoPlaying = false

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

        return binding.root
    }

    private fun loadReview(reviewId: Long) {
        ReviewRepository().getReviewDetail(reviewId, onSuccess = { fetchedReview ->
            review = fetchedReview
            setupVideoPlayer(fetchedReview.shortFormUrl)
        }, onFailure = { throwable ->
            Toast.makeText(
                requireContext(),
                "리뷰 영상 보기 실패: ${throwable.message}",
                Toast.LENGTH_SHORT
            ).show()
        })
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

    private fun setupVideoPlayer(videoUrl: String?) {
        videoUrl?.let { url ->
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(binding.reviewDetailView)

            binding.reviewDetailView.apply {
                setMediaController(mediaController)
                setVideoPath(url)

                setOnPreparedListener { mediaPlayer ->
                    adjustVideoViewSize(mediaPlayer.videoWidth to mediaPlayer.videoHeight)
                    mediaPlayer.isLooping = true
                    start()
                    isVideoPlaying = true
                }

                setOnCompletionListener {
                    isVideoPlaying = false
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "비디오 URL을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isVideoPlaying) {
            binding.reviewDetailView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isVideoPlaying) {
            binding.reviewDetailView.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

