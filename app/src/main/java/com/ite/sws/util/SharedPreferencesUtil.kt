package com.ite.sws.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferencesUtil
 * @author 남진수
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	남진수       최초 생성
 * 2024.09.03   정은지       액세스 토큰 저장 및 추출 함수 추가
 * </pre>
 */
object SharedPreferencesUtil {

    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveString(context: Context, key: String, value: String) {
        val editor = getPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String, defaultValue: String? = null): String? {
        return getPreferences(context).getString(key, defaultValue)
    }

    fun saveInt(context: Context, key: String, value: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }

    fun saveLong(context: Context, key: String, value: Long) {
        val editor = getPreferences(context).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(context: Context, key: String, defaultValue: Long = 0): Long {
        return getPreferences(context).getLong(key, defaultValue)
    }

    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }

    fun remove(context: Context, key: String) {
        val editor = getPreferences(context).edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

    /**
     * 액세스 토큰 저장하기
     */
    fun setAccessToken(accessToken: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    /**
     * 액세스 토큰 가져오기
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
}