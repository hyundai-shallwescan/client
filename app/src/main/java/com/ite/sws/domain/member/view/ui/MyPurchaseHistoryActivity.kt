package com.ite.sws.domain.member.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ite.sws.R

/**
 * 구매내역 액티비티
 * @author 정은지
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일         수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01   정은지       최초 생성
 * </pre>
 */
class MyPurchaseHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_purchase_history)
    }
}