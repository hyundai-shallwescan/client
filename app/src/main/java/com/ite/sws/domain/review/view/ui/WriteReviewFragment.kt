package com.ite.sws.domain.review.view.ui


import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.MainActivity
import com.ite.sws.databinding.FragmentMyReviewListBinding
import com.ite.sws.domain.review.view.adapter.ReviewWriteRecyclerViewAdapter
import com.ite.sws.domain.review.data.GetMemberPaymentProductReviewRes
import com.ite.sws.util.hideBottomNavigation
import setupToolbar

/**
 * 리뷰 작성 프래그먼트
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
class WriteReviewFragment : Fragment() {
    private var _binding: FragmentMyReviewListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPaymentReviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReviewListBinding.inflate(inflater, container, false)

        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, true)
        }

        setupToolbar(binding.toolbar.toolbar, "구매 내역", true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMyReviewList(
            onSuccess = { reviewItems ->
                if (reviewItems.isNotEmpty()) {
                    displayReviewList(reviewItems)
                } else {
                    showEmptyView()
                }
            },
            onFailure = { errorMsg ->
                showError(errorMsg)
            }
        )
    }

    private fun showEmptyView() {
        binding.rvPayment.visibility = View.GONE
        binding.layoutEmpty.visibility = View.VISIBLE
    }

    private fun displayReviewList(items: List<GetMemberPaymentProductReviewRes>) {
        binding.rvPayment.visibility = View.VISIBLE

        binding.rvPayment.layoutManager = LinearLayoutManager(context)

        val adapter = ReviewWriteRecyclerViewAdapter(items) { reviewItem ->
            onCreateReviewClick(reviewItem)
        }

        binding.rvPayment.adapter = adapter
    }

    private fun onCreateReviewClick(item: GetMemberPaymentProductReviewRes) {
        val reviewUploadFragment = ReviewUploadFragment()

        val bundle = Bundle().apply {
            putParcelable("reviewItem", item)
        }
        reviewUploadFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_review, reviewUploadFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



//create the button and ic,  apply the recyclable view in here
//MyPaymentRecyclerViewAdapter 적용

//when this fragment is executed. the getMyReviewList method will be executed from the ReviewRepository(this is the rest api)
/*
    data class GetMemberReviewRes(
    val createdAt: LocalDateTime,
    val paymentItemId : Long,
    val name: String,
    val thumbnailImage: String,
    val isReviewWritten: Char
)
 */
//There is the Recyclable view in the WriteReviewFragment which is at the fragment_my_review_list.xml
//If the isReviewWritten is 'N' show the icon which is at drawable/fragment_my_review_already_created_review_img.xml otherwise show the drawable/fragment_my_review_create_btn
//When click the input button which name is the drawable/fragment_my_review_create_btn go to the new Fragment which name is the ReviewUploadFragment