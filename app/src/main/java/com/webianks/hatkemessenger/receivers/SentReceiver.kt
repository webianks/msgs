package com.webianks.hatkemessenger.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast

/**
 * Created by R Ankit on 30-12-2016.
 */
class SentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, "Generic failure",
                    Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(context, "No service",
                    Toast.LENGTH_SHORT).show()
            SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT)
                    .show()
            SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, "No network",
                    Toast.LENGTH_SHORT).show()
        }
    }
}