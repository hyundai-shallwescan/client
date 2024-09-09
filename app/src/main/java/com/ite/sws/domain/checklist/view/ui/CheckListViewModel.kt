package com.ite.sws.domain.checklist.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ite.sws.common.data.CheckStatus
import com.ite.sws.domain.checklist.api.repository.CheckListRepository
import com.ite.sws.domain.checklist.data.GetCheckListRes
import com.ite.sws.domain.checklist.data.PostCheckListReq

/**
 * 체크리스트 ViewModel
 * @author 정은지
 * @since 2024.09.07
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.07  정은지        최초 생성
 * 2024.09.07  정은지        체크리스트 조회
 * 2024.09.07  정은지        체크 상태 변경
 * 2024.09.08  정은지        체크리스트 아이템 추가
 * 2024.09.09  정은지        체크리스트 아이템 수정
 * 2024.09.09  정은지        체크리스트 아이템 삭제
 * </pre>
 */
class CheckListViewModel() : ViewModel() {

    private val checkListRepository = CheckListRepository()

    private val _checkListItems = MutableLiveData<List<GetCheckListRes>>()
    val checkListItems: LiveData<List<GetCheckListRes>> get() = _checkListItems

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        loadCheckListItems()
    }

    /**
     * 체크리스트 조회
     */
    fun loadCheckListItems() {
        checkListRepository.findMyCheckList(
            onSuccess = { items ->
                _checkListItems.value = items
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }

    /**
     * 체크 상태 변경
     */
    fun updateItemStatus(item: GetCheckListRes) {
        checkListRepository.modifyCheckListItemStatus(
            myCheckListItemId = item.myCheckListItemId,
            onSuccess = {
                val updatedList = _checkListItems.value?.map {
                    if (it.myCheckListItemId == item.myCheckListItemId) {
                        it.copy(status = if (it.status == CheckStatus.CHECKED) CheckStatus.UNCHECKED else CheckStatus.CHECKED)
                    } else {
                        it
                    }
                }
                _checkListItems.value = updatedList
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }

    /**
     * 체크리스트 아이템 추가
     */
    fun addCheckListItem(postCheckListReq: PostCheckListReq) {
        checkListRepository.addMyCheckListItem(postCheckListReq,
            onSuccess = {
                loadCheckListItems()
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }

    /**
     * 체크리스트 아이템 변경
     */
    fun editCheckListItem(itemId: Long, newName: String) {
        checkListRepository.modifyMyCheckListItem(
            itemId, newName,
            onSuccess = {
                loadCheckListItems()
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }

    /**
     * 체크리스트 아이템 삭제
     */
    fun deleteItem(itemId: Long) {
        checkListRepository.deleteMyCheckListItem(
            itemId,
            onSuccess = {
                loadCheckListItems()
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }
}