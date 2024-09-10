package com.ite.sws.domain.review.view.ui


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import com.ite.sws.R
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
import androidx.fragment.app.Fragment
import com.ite.sws.util.CustomDialog
import com.ite.sws.databinding.FragmentMyReviewUploadBinding
import com.ite.sws.domain.review.api.repository.ReviewRepository
import com.ite.sws.domain.review.data.PostCreateReviewReq
import androidx.constraintlayout.widget.ConstraintLayout
import setupToolbar
import java.io.File
import java.io.FileOutputStream


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
class ReviewUploadFragment : Fragment() {
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

        videoView.visibility = View.GONE
        btnVideoPlay.visibility = View.GONE
        btnDelete.visibility = View.GONE
        btnUpload.visibility = View.GONE

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

    private fun openVideoPicker() {
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

    private fun toggleVideoPlayPause() {
        if (videoView.isPlaying) {
            videoView.pause()
            btnVideoPlay.visibility = View.VISIBLE
        } else {
            videoView.start()
            videoView.background = null
            btnVideoPlay.visibility = View.GONE
            videoView.setOnCompletionListener {
                btnVideoPlay.visibility = View.VISIBLE
            }
        }
    }

    private fun removeVideo() {
        videoView.stopPlayback()
        videoUri = null
        hideVideoUI()
    }

    fun VideoView.setBackgroundBitmap(bitmap: Bitmap) {
        val drawable = BitmapDrawable(resources, bitmap)
        this.background = drawable
    }

    private fun showVideoUI(uri: Uri) {
        val thumbnailPath = captureThumbnail(uri)
        val thumbnailBitmap = BitmapFactory.decodeFile(thumbnailPath)

        videoView.setBackgroundBitmap(thumbnailBitmap)
        videoView.visibility = View.VISIBLE
        btnVideoPlay.visibility = View.VISIBLE
        btnDelete.visibility = View.VISIBLE
        btnUpload.visibility = View.VISIBLE
        imgUploadBackground.visibility = View.GONE
        btnReviewUpload.visibility = View.GONE
    }

    private fun hideVideoUI() {
        videoView.visibility = View.GONE
        btnVideoPlay.visibility = View.GONE
        btnDelete.visibility = View.GONE
        btnUpload.visibility = View.GONE
        imgUploadBackground.visibility = View.VISIBLE
        btnReviewUpload.visibility = View.VISIBLE
    }

    private fun showDeleteDialog(title: String) {
        val dialog = CustomDialog(
            layoutId = R.layout.dialog_text2_btn2,
            title = title,
            confirmText = "확인",
            cancelText = "취소",
            onConfirm = { removeVideo() }
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

            val thumbnail = captureThumbnail(uri)

            val postCreateReviewReq = PostCreateReviewReq(paymentId, productId)

            val videoFile = File(uriToFilePath(uri))
            val imageFile = File(thumbnail)

            ReviewRepository().createReview(
                postCreateReviewReq = postCreateReviewReq,
                shortFormFile = videoFile,
                image = imageFile,
                onSuccess = {
                    Toast.makeText(requireContext(), "영상이 업로드되었습니다", Toast.LENGTH_SHORT).show()
                    resizeVideoView()
                },
                onFailure = { throwable ->
                    Toast.makeText(requireContext(), "업로드 실패", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun resizeVideoView() {
        binding.btnUpload.visibility = View.GONE
        binding.btnDelete.visibility = View.GONE

        val videoViewParam = binding.reviewVideoBackground.layoutParams as ConstraintLayout.LayoutParams
        videoViewParam.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        videoViewParam.height = 0
        videoViewParam.topToBottom = binding.toolbar.toolbar.id
        videoViewParam.setMargins(0, 0, 0, 0)

        binding.reviewVideoBackground.layoutParams = videoViewParam
    }

    private fun captureThumbnail(uri: Uri): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireContext(), uri)
        val bitmap = retriever.frameAtTime
        val file = File(requireContext().cacheDir, "${productName}_thumbnail.jpg")
        FileOutputStream(file).use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        retriever.release()
        return file.path
    }

    private fun uriToFilePath(uri: Uri): String {
        var filePath = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
            }
        }
        return filePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
