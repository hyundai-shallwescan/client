package com.ite.sws.domain.review.view.ui

import android.graphics.Rect
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.R
import com.ite.sws.domain.review.api.repository.ReviewRepository

import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.domain.review.view.adapter.ReviewAdapter
import com.ite.sws.databinding.FragmentReviewBinding
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

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviews: List<GetReviewRes>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_reviews)
        recyclerView.layoutManager = LinearLayoutManager(context)
        setupToolbar(binding.toolBar, "리뷰", true)

        ReviewRepository().getReviews(page = 0, size = 10, onSuccess = { fetchedReviews ->
            reviews = fetchedReviews
            reviewAdapter = ReviewAdapter(reviews) { review ->
                playVideoAutomatically(view.findViewById(R.id.video_view), Uri.parse(review.shortFormUrl))
            }
            recyclerView.adapter = reviewAdapter
        }, onFailure = { throwable ->
            Toast.makeText(requireContext(), "리뷰 영상 보기 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleAutoPlayVideo()
            }
        })

        return view
    }


        private fun handleAutoPlayVideo() {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? ReviewAdapter.ReviewViewHolder
                viewHolder?.let {
                    if (isFullyVisible(it.itemView)) {
                        val videoUri = Uri.parse(reviews[i].shortFormUrl)
                        playVideoAutomatically(it.itemView.findViewById(R.id.video_view), videoUri)
                    } else {
                        it.itemView.findViewById<VideoView>(R.id.video_view).stopPlayback()
                    }
                }
            }
        }

    private fun playVideoAutomatically(videoView: VideoView, videoUri: Uri) {

        videoView.setVideoURI(Uri.parse("https://shall-we-scan-reviews.s3.ap-northeast-2.amazonaws.com/%E1%84%8C%E1%85%A5%E1%86%BC%E1%84%89%E1%85%A1%E1%86%AB%E1%84%8B%E1%85%AE%E1%86%B7%E1%84%8D%E1%85%A1%E1%86%AF%E1%84%8B%E1%85%B1%E1%84%92%E1%85%A1%E1%86%AB%E1%84%8B%E1%85%A7%E1%86%BC%E1%84%89%E1%85%A1%E1%86%BC_1.mp4"))
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
        }
        videoView.setOnErrorListener { mp, what, extra ->
            val errorMessage = when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown media error"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Media server died"
                MediaPlayer.MEDIA_ERROR_IO -> "I/O error"
                MediaPlayer.MEDIA_ERROR_MALFORMED -> "Malformed media"
                MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Unsupported media"
                MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "Timed out"
                else -> "Unknown error"
            }
            Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_LONG).show()
            true
        }
    }

    private fun isFullyVisible(view: View): Boolean {
        val itemRect = Rect()
        val isVisible = view.getGlobalVisibleRect(itemRect)
        return isVisible && itemRect.height() == view.height
    }
}

