package com.ite.sws.domain.sharelist.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ite.sws.databinding.FragmentExternalShareListBinding

/**
 * 외부인 공유 체크 리스트 프래그먼트
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * </pre>
 */
class ExternalShareListFragment : Fragment() {
    // View Binding 객체
    private var _binding: FragmentExternalShareListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExternalShareListBinding.inflate(inflater, container, false)

        return binding.root
    }
}