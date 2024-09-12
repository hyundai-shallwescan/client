package com.ite.sws.domain.review.view.ui


import com.ite.sws.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.databinding.FragmentMyReviewListBinding
import com.ite.sws.domain.member.data.GetMemberPaymentRes
import com.ite.sws.domain.review.view.adapter.WriteReviewRecyclerViewAdapter
import com.ite.sws.util.replaceFragmentWithAnimation

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.findPaymentItemList(
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

    private fun displayReviewList(items: List<GetMemberPaymentRes>) {
        binding.rvPayment.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE

        binding.rvPayment.layoutManager = LinearLayoutManager(context)


        val adapter = WriteReviewRecyclerViewAdapter(items) { paymentItem ->
            val bundle = Bundle().apply {
                putLong("paymentItemId",paymentItem.paymentItemId)
                putLong("productId",paymentItem.productId)
                putString("productName",paymentItem.name)
            }
            val fragment = ReviewUploadFragment().apply {
                arguments = bundle
            }

            replaceFragmentWithAnimation(R.id.container_main,fragment)

        }
        binding.rvPayment.adapter = adapter
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

