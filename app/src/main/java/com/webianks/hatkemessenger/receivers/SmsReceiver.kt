package com.webianks.hatkemessenger.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import com.webianks.hatkemessenger.R
import com.webianks.hatkemessenger.activities.SmsDetailedView
import com.webianks.hatkemessenger.constants.Constants
import com.webianks.hatkemessenger.services.SaveSmsService

/**
 * Created by R Ankit on 24-12-2016.
 */
class SmsReceiver : BroadcastReceiver() {
    private val TAG = SmsReceiver::class.java.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            Log.e(TAG, "smsReceiver")
            val bundle = intent.extras
            if (bundle != null) {
                val pdu_Objects = bundle["pdus"] as Array<Any>?
                if (pdu_Objects != null) {
                    for (aObject in pdu_Objects) {
                        val currentSMS = getIncomingMessage(aObject, bundle)
                        val senderNo = currentSMS.displayOriginatingAddress
                        val message = currentSMS.displayMessageBody
                        //Log.d(TAG, "senderNum: " + senderNo + " :\n message: " + message);
                        issueNotification(context, senderNo, message)
                        saveSmsInInbox(context, currentSMS)
                    }
                    abortBroadcast()
                    // End of loop
                }
            }
        } // bundle null
    }

    private fun saveSmsInInbox(context: Context, sms: SmsMessage) {
        val serviceIntent = Intent(context, SaveSmsService::class.java)
        serviceIntent.putExtra("sender_no", sms.displayOriginatingAddress)
        serviceIntent.putExtra("message", sms.displayMessageBody)
        serviceIntent.putExtra("date", sms.timestampMillis)
        context.startService(serviceIntent)
    }

    private fun issueNotification(context: Context, senderNo: String, message: String) {
        val icon = BitmapFactory.decodeResource(context.resources,
                R.mipmap.ic_launcher)
        val mBuilder = NotificationCompat.Builder(context)
                .setLargeIcon(icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(senderNo)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setContentText(message)
        val resultIntent = Intent(context, SmsDetailedView::class.java)
        resultIntent.putExtra(Constants.CONTACT_NAME, senderNo)
        resultIntent.putExtra(Constants.FROM_SMS_RECIEVER, true)
        val resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotifyMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mNotificationId = 101
        mNotifyMgr.notify(mNotificationId, mBuilder.build())
    }

    private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
        val currentSMS: SmsMessage
        currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else {
            SmsMessage.createFromPdu(aObject as ByteArray)
        }
        return currentSMS
    }
}