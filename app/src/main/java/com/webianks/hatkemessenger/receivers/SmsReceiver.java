package com.webianks.hatkemessenger.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.activities.SmsDetailedView;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.services.SaveSmsService;

/**
 * Created by R Ankit on 24-12-2016.
 */

public class SmsReceiver extends BroadcastReceiver {


    private String TAG = SmsReceiver.class.getSimpleName();
    private Bundle bundle;
    private SmsMessage currentSMS;
    private int mNotificationId = 101;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Log.e(TAG, "smsReceiver");

            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        currentSMS = getIncomingMessage(aObject, bundle);

                        String senderNo = currentSMS.getDisplayOriginatingAddress();
                        String message = currentSMS.getDisplayMessageBody();
                        //Log.d(TAG, "senderNum: " + senderNo + " :\n message: " + message);

                        issueNotification(context, senderNo, message);
                        saveSmsInInbox(context,currentSMS);


                    }
                    this.abortBroadcast();
                    // End of loop
                }
            }
        } // bundle null
    }

    private void saveSmsInInbox(Context context, SmsMessage sms) {

        Intent serviceIntent = new Intent(context, SaveSmsService.class);
        serviceIntent.putExtra("sender_no", sms.getDisplayOriginatingAddress());
        serviceIntent.putExtra("message", sms.getDisplayMessageBody());
        serviceIntent.putExtra("date", sms.getTimestampMillis());
        context.startService(serviceIntent);

    }

    private void issueNotification(Context context, String senderNo, String message) {

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(senderNo)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(true)
                        .setContentText(message);

        Intent resultIntent = new Intent(context, SmsDetailedView.class);
        resultIntent.putExtra(Constants.CONTACT_NAME,senderNo);
        resultIntent.putExtra(Constants.FROM_SMS_RECIEVER,true);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }
}
