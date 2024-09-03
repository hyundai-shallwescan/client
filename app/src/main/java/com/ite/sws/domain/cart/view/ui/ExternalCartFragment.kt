package com.ite.sws.domain.cart.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ite.sws.databinding.FragmentExternalCartBinding

/**
 * 외부인 장바구니 프래그먼트
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	김민정       최초 생성
 * </pre>
 */
class ExternalCartFragment : Fragment() {

    // View Binding 객체
    private var _binding: FragmentExternalCartBinding? = null
    private val binding get() = _binding!!

    // ViewModel 객체
    private lateinit var viewModel: ExternalCartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalCartBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ExternalCartViewModel::class.java)

        return binding.root
    }
}