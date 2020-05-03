package com.webianks.hatkemessenger.receivers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.webianks.hatkemessenger.R

/**
 * Created by R Ankit on 30-12-2016.
 */
class DeliverReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> Toast.makeText(context, R.string.sms_delivered,
                    Toast.LENGTH_SHORT).show()
            Activity.RESULT_CANCELED -> Toast.makeText(context, R.string.sms_not_delivered,
                    Toast.LENGTH_SHORT).show()
        }
    }
}