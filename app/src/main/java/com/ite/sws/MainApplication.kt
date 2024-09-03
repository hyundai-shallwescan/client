package com.ite.sws

import android.app.Application
import com.ite.sws.util.SharedPreferencesUtil

/**
 * 전역 상태 관리를 위한 클래스
 * @author 정은지
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	정은지       최초 생성
 * </pre>
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesUtil.init(this)
    }
}