package com.meergruen.time_sheets

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.core.android.Auth
import com.meergruen.time_sheets.DropboxClient.init

/**
 * Base class for Activities that require auth tokens
 * Will redirect to auth flow if needed
 */
abstract class DropboxActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("thoughtful", Context.MODE_PRIVATE)

        var accessToken = prefs.getString("dropbox-access-token", null)
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token()
            if (accessToken != null) {
                prefs.edit().putString("dropbox-access-token", accessToken).apply()
                initAndLoadData(accessToken)
            }
        } else {
            initAndLoadData(accessToken)
        }

        val uid = Auth.getUid()
        val storedUid = prefs.getString("user-id", null)
        if (uid != null && uid != storedUid) {
            prefs.edit().putString("user-id", uid).apply()
        }
    }

    private fun initAndLoadData(accessToken: String) {

        init(accessToken)
        loadData()
    }


    protected abstract fun loadData()

    protected fun hasToken(): Boolean {
        val prefs = getSharedPreferences("thoughtful", Context.MODE_PRIVATE)
        val accessToken = prefs.getString("dropbox-access-token", null)
        return accessToken != null

    }

    companion object {
        @JvmStatic
        fun startOAuth2Authentication(context: Context?, app_key: String?) {
            Auth.startOAuth2Authentication(context, app_key)
        }


    }

}
