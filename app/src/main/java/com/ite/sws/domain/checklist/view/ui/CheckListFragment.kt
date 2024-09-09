package com.ite.sws.domain.checklist.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ite.sws.MainActivity
import com.ite.sws.R
import com.ite.sws.databinding.FragmentChecklistBinding
import com.ite.sws.domain.chatbot.view.ui.ChatBotFragment
import com.ite.sws.domain.checklist.data.GetCheckListRes
import com.ite.sws.domain.checklist.data.PostCheckListReq
import com.ite.sws.domain.checklist.view.adapter.CheckListRecyclerViewAdapter
import com.ite.sws.util.hideBottomNavigation
import com.ite.sws.util.replaceFragmentWithAnimation
import setupToolbar

/**
 * 체크리스트 프래그먼트
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
class CheckListFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CheckListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)

        // 툴바 설정
        setupToolbar(binding.toolbar.toolbar, "체크리스트", false)
        val toolbarIcon = binding.toolbar.toolbarIcon
        toolbarIcon?.visibility = View.VISIBLE

        // 내비게이션 바 노출
        (activity as? MainActivity)?.let { mainActivity ->
            hideBottomNavigation(mainActivity.binding, false)
        }

        // RecyclerView 설정
        setupRecyclerView()

        // 데이터 업데이트
        viewModel.checkListItems.observe(viewLifecycleOwner) { items ->
            updateRecyclerView(items)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 추가 아이콘 클릭 시
        binding.toolbar.toolbarIcon.setOnClickListener {
            val dialog = CheckListAddDialog()
            dialog.show(parentFragmentManager, "CheckListAddDialog")
        }

        parentFragmentManager.setFragmentResultListener("requestKey", this) { requestKey, bundle ->
            val result = bundle.getString("newItem")
            if (result != null) {
                addCheckListItem(result)
            }
        }

        binding.btnChatbot.setOnClickListener {
            replaceFragmentWithAnimation(R.id.container_main, ChatBotFragment(), true)
        }
    }

    private fun setupRecyclerView() {
        binding.rvChecklist.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateRecyclerView(items: List<GetCheckListRes>) {
        val adapter = CheckListRecyclerViewAdapter(items, ::onItemChecked, ::onItemEdited)
        binding.rvChecklist.adapter = adapter
    }

    /**
     * 체크리스트 체크 상태 변경
     */
    private fun onItemChecked(item: GetCheckListRes, isChecked: Boolean) {
        viewModel.updateItemStatus(item)
    }

    /**
     * 체크리스트 아이템 추가
     */
    private fun addCheckListItem(item: String) {
        val postCheckListReq = PostCheckListReq(item)
        viewModel.addCheckListItem(postCheckListReq)
    }

    /**
     * 체크리스트 아이템 수정
     */
    private fun onItemEdited(item: GetCheckListRes, newText: String) {
        viewModel.editCheckListItem(item.myCheckListItemId, newText)
    }

    // 체크리스트 아이템 삭제
    private fun onItemRemoved(item: GetCheckListRes) {
        viewModel.deleteItem(item.myCheckListItemId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val toolbarIcon = binding.toolbar.toolbarIcon
        toolbarIcon?. visibility = View.GONE

        _binding = null
    }
}