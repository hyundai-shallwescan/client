package com.ite.sws.domain.review.view.ui


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.ite.sws.R
import com.ite.sws.common.util.VideoUtil
import com.ite.sws.databinding.FragmentMyReviewUploadBinding
import com.ite.sws.domain.member.view.ui.MyReviewFragment
import com.ite.sws.domain.review.api.repository.ReviewRepository
import com.ite.sws.domain.review.data.PostCreateReviewReq
import com.ite.sws.util.CustomDialog
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar
import java.io.File


/**
 * 리뷰 업로드 프래그먼트
 * @author 구지웅
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	구지웅       최초 생성
 * </pre>
 */
class ReviewUploadFragment : BaseVideoFragment() {
    private var _binding: FragmentMyReviewUploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var imgUploadBackground: ImageView
    private lateinit var btnReviewUpload: Button
    private lateinit var videoView: VideoView
    private lateinit var btnVideoPlay: ImageButton
    private lateinit var btnDelete: Button
    private lateinit var btnUpload: Button
    private var videoUri: Uri? = null
    private var productName: String? = null
    private var paymentId: Long? = null
    private var productId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReviewUploadBinding.inflate(inflater, container, false)
        imgUploadBackground = binding.imgUploadBackground
        btnReviewUpload = binding.btnReviewUpload
        videoView = binding.reviewVideoBackground
        btnVideoPlay = binding.btnVideoPlay
        btnDelete = binding.btnDelete
        btnUpload = binding.btnUpload

        hideVideoUI()

        productName = arguments?.getString("productName")
        paymentId = arguments?.getLong("paymentItemId")
        productId = arguments?.getLong("productId")

        binding.tvProductName.text = productName

        btnReviewUpload.setOnClickListener { openVideoPicker() }
        btnVideoPlay.setOnClickListener { toggleVideoPlayPause() }
        btnDelete.setOnClickListener { showDeleteDialog("영상을 정말로 삭제하시겠습니까?") }
        btnUpload.setOnClickListener { showUploadDialog("리뷰 영상을 업로드 하시겠습니까?") }

        setupToolbar(binding.toolbar.toolbar, "리뷰 작성", true)

        return binding.root
    }

    override fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        videoPickerLauncher.launch(intent)
    }

    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            videoUri = result.data?.data
            videoUri?.let {
                videoView.setVideoURI(it)
                showVideoUI(it)
            }
        }
    }

    override fun toggleVideoPlayPause() {
        if (videoView.isPlaying) {
            videoView.pause()
            btnVideoPlay.visibility = View.VISIBLE
        } else {
            videoUri?.let { uri ->
                videoView.start()
                videoView.background = null
                btnVideoPlay.visibility = View.GONE
                videoView.setOnCompletionListener {
                    btnVideoPlay.visibility = View.VISIBLE
                    showVideoUI(uri)
                    resizeVideoView()
                }
            }
        }
    }

    override fun showVideoUI(uri: Uri) {
        resizeVideoView()

        val thumbnailPath =
            VideoUtil.captureThumbnail(requireContext(), uri, "${paymentId}_${productId}_video")
        val thumbnailBitmap = BitmapFactory.decodeFile(thumbnailPath)

        videoView.background = BitmapDrawable(resources, thumbnailBitmap)
        videoView.visibility = View.VISIBLE
        btnVideoPlay.visibility = View.VISIBLE
        btnDelete.visibility = View.VISIBLE
        btnUpload.visibility = View.VISIBLE
        imgUploadBackground.visibility = View.GONE
        btnReviewUpload.visibility = View.GONE
    }

    override fun hideVideoUI() {
        videoView.visibility = View.GONE
        btnVideoPlay.visibility = View.GONE
        btnDelete.visibility = View.GONE
        btnUpload.visibility = View.GONE
        imgUploadBackground.visibility = View.VISIBLE
        btnReviewUpload.visibility = View.VISIBLE
    }

    override fun resizeVideoView() {
        val videoDimensions =
            videoUri?.let { VideoUtil.getVideoDimensions(requireContext(), it) } ?: return
        val (videoWidth, videoHeight) = videoDimensions
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        val toolbarHeight = binding.toolbar.toolbar.height
        val productNameHeight = binding.tvProductName.height + 10
        val buttonHeight = binding.btnUpload.height + binding.btnDelete.height + 10

        val availableHeight = screenHeight - toolbarHeight - productNameHeight - buttonHeight

        val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val newHeight = (screenWidth / aspectRatio).toInt()

        val videoViewParam =
            binding.reviewVideoBackground.layoutParams as ConstraintLayout.LayoutParams
        videoViewParam.width = screenWidth
        videoViewParam.height = if (newHeight > availableHeight) availableHeight else newHeight

        binding.reviewVideoBackground.layoutParams = videoViewParam
        binding.reviewVideoBackground.requestLayout()
    }

    private fun showDeleteDialog(title: String) {
        val dialog = CustomDialog(
            layoutId = R.layout.dialog_text2_btn2,
            title = title,
            confirmText = "확인",
            cancelText = "취소",
            onConfirm = { VideoUtil.removeVideo(videoView, ::hideVideoUI) }
        )
        dialog.show(parentFragmentManager, "DeleteDialog")
    }

    private fun showUploadDialog(title: String) {
        val dialog = CustomDialog(
            layoutId = R.layout.dialog_text2_btn2,
            title = title,
            confirmText = "확인",
            cancelText = "취소",
            onConfirm = { uploadReview() }
        )
        dialog.show(parentFragmentManager, "UploadDialog")
    }
    private fun uploadReview() {
        videoUri?.let { uri ->
            val compressedFilePath =
                File(requireContext().cacheDir, "${paymentId}_${productId}_compressed.mp4").path

            VideoUtil.compressVideo(
                context = requireContext(),
                inputUri = uri,
                outputFilePath = compressedFilePath,
                onSuccess = { compressedPath ->
                    val thumbnailPath = VideoUtil.captureThumbnail(
                        requireContext(),
                        uri,
                        "${paymentId}_${productId}"
                    )
                    val postCreateReviewReq = PostCreateReviewReq(paymentId, productId)
                    val videoFile = File(compressedPath)
                    val imageFile = File(thumbnailPath)

                    ReviewRepository().createReview(
                        postCreateReviewReq = postCreateReviewReq,
                        shortFormFile = videoFile,
                        image = imageFile,
                        onSuccess = {
                            Toast.makeText(
                                requireContext(),
                                "영상이 압축되어 업로드되었습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                            replaceFragmentWithAnimation(
                                R.id.container_main,
                                MyReviewFragment(), true
                            )
                        },
                        onFailure = { throwable ->
                            Toast.makeText(
                                requireContext(),
                                "업로드 실패: ${throwable.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                onFailure = { throwable ->
                    Toast.makeText(
                        requireContext(),
                        "비디오 압축 실패: ${throwable.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        } ?: run {
            Toast.makeText(requireContext(), "비디오를 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
