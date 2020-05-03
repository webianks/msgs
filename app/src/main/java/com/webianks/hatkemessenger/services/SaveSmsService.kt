package com.webianks.hatkemessenger.services

import android.app.IntentService
import android.content.ContentValues
import android.content.Intent
import com.webianks.hatkemessenger.constants.SmsContract

/**
 * Created by R Ankit on 26-12-2016.
 */
class SaveSmsService : IntentService("SaveService") {
    override fun onHandleIntent(intent: Intent?) {
        val senderNo = intent!!.getStringExtra("sender_no")
        val message = intent.getStringExtra("message")
        val time = intent.getLongExtra("date", 0)
        val values = ContentValues()
        values.put("address", senderNo)
        values.put("body", message)
        values.put("date_sent", time)
        contentResolver.insert(SmsContract.ALL_SMS_URI, values)
        val i = Intent("android.intent.action.MAIN").putExtra("new_sms", true)
        this.sendBroadcast(i)
    }
}