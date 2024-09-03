package com.ite.sws.domain.sharelist.api.repository

import com.ite.sws.common.RetrofitClient
import com.ite.sws.domain.sharelist.api.service.ShareListService

/**
 * 공유 체크 리스트 Repository
 * @author 김민정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일       수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  김민정       최초 생성
</pre> *
 */
internal class ShareListRepository {

    private val shareListService =
        RetrofitClient.instance.create(ShareListService::class.java)
}
