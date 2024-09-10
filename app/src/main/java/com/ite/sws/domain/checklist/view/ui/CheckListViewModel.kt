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

    private val _checkListItems = MutableLiveData<MutableList<GetCheckListRes>>()
    val checkListItems: LiveData<MutableList<GetCheckListRes>> get() = _checkListItems

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
                _checkListItems.value = items.toMutableList()
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
                _checkListItems.value?.let { list ->
                    val updatedList = list.map { currentItem ->
                        if (currentItem.myCheckListItemId == item.myCheckListItemId) {
                            currentItem.copy(status = if (currentItem.status == CheckStatus.CHECKED) CheckStatus.UNCHECKED else CheckStatus.CHECKED)
                        } else {
                            currentItem
                        }
                    }.toMutableList()
                    _checkListItems.value = updatedList
                }
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
                // 새로운 아이템을 리스트 맨 상단에 추가
                val newItem = GetCheckListRes(
                    myCheckListItemId = it.myCheckListItemId,
                    itemName = postCheckListReq.item,
                    status = CheckStatus.UNCHECKED // 기본 상태로 추가
                )
                // 기존 리스트를 MutableList로 변환
                val updatedList = _checkListItems.value?.toMutableList() ?: mutableListOf()

                updatedList.add(0, newItem) // 리스트 상단에 추가
                _checkListItems.value = updatedList // LiveData 업데이트
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
                _checkListItems.value?.let { list ->
                    val updatedList = list.map { currentItem ->
                        if (currentItem.myCheckListItemId == itemId) {
                            currentItem.copy(itemName = newName)
                        } else {
                            currentItem
                        }
                    }.toMutableList()
                    _checkListItems.value = updatedList
                }
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }

    /**
     * 체크리스트 아이템 삭제
     */
    fun deleteItem(checkListId: Long) {
        checkListRepository.deleteMyCheckListItem(checkListId,
            onSuccess = {
                val updatedList = _checkListItems.value?.filterNot { it.myCheckListItemId == checkListId }?.toMutableList()
                _checkListItems.value = updatedList
            },
            onFailure = { errorRes ->
                _errorMessage.value = errorRes.message
            }
        )
    }
}