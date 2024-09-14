package com.ite.sws.domain.sharelist.view.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ite.sws.common.data.ErrorRes
import com.ite.sws.domain.product.api.repository.ProductRepository
import com.ite.sws.domain.product.data.GetProductRes
import com.ite.sws.domain.sharelist.api.repository.ShareListRepository
import com.ite.sws.domain.sharelist.data.PostShareListItemReq
import com.ite.sws.domain.sharelist.data.ShareListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * 외부일행 공유 체크 리스트 ViewModel
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
 * 2024.09.13  김민정       공유체크리스트 아이템 조회
 * 2024.09.13  김민정       공유체크리스트 아이템 추가
 * 2024.09.13  김민정       공유체크리스트 아이템 체크 상태 변경
 * 2024.09.13  김민정       공유체크리스트 아이템 삭제
 * 2024.09.13  김민정       상품 이름으로 검색
 * </pre>
 */
class ExternalShareListViewModel : BaseShareListViewModel() {

    private val shareListRepository = ShareListRepository()
    private val productRepository = ProductRepository()

    // 서버 요청 결과를 저장하는 LiveData
    private val _shareListItems = MutableLiveData<List<ShareListItem>>()
    val shareListItems: LiveData<List<ShareListItem>> = _shareListItems

    private val _productSearchResults = MutableLiveData<List<GetProductRes>>()
    val productSearchResults: LiveData<List<GetProductRes>> = _productSearchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 공유체크리스트 아이템 추가
     */
    fun saveShareListItem(cartId: Long, productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shareListRepository.saveShareListItem(PostShareListItemReq(cartId, productId))
            } catch (e: Exception) {
                if (e is HttpException) {
                    val errorJson = e.response()?.errorBody()?.string()
                    val errorRes = Gson().fromJson(errorJson, ErrorRes::class.java)

                    if (errorRes.status == 409) {
                        _error.postValue(errorRes.message)
                    }
                } else {
                    Log.e("ShareCheckList Error: ", e.message.toString())
                }
            }
        }
    }

    /**
     * 공유체크리스트 아이템 조회
     */
    override fun findShareList(cartId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = shareListRepository.findShareList(cartId)
                _shareListItems.postValue(items)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 공유체크리스트 아이템 체크 상태 변경
     */
    override fun modifyShareListCheck(cartId: Long, productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shareListRepository.modifyshareListItem(cartId, productId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 공유체크리스트 아이템 삭제
     */
    override fun removeShareListItem(cartId: Long, productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                shareListRepository.removeShareListItem(cartId, productId)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    /**
     * 공유 체크리스트 아이템 업데이트
     */
    override fun updateShareListItems(newItems: List<ShareListItem>) {
        _shareListItems.value = newItems
    }

    /**
     * 상품 이름으로 검색
     */
    fun findProductListByName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val products = productRepository.findProductListByName(name)
                _productSearchResults.postValue(products)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}