package com.webianks.hatkemessenger.services

import android.app.IntentService
import android.content.ContentValues
import android.content.Intent
import android.net.Uri

/**
 * Created by R Ankit on 29-12-2016.
 */
class UpdateSMSService : IntentService("UpdateSMSReceiver") {
    override fun onHandleIntent(intent: Intent?) {
        markSmsRead(intent!!.getLongExtra("id", -123))
    }

    fun markSmsRead(messageId: Long) {
        try {
            val cv = ContentValues()
            cv.put("read", "1")
            contentResolver.update(Uri.parse("content://sms/$messageId"), cv, null, null)
        } catch (e: Exception) {
        }
    }
}