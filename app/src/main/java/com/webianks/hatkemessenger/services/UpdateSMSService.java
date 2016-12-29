package com.webianks.hatkemessenger.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.webianks.hatkemessenger.constants.SmsContract;

/**
 * Created by R Ankit on 29-12-2016.
 */

public class UpdateSMSService extends IntentService {

    private static String TAG = UpdateSMSService.class.getSimpleName();

    public UpdateSMSService() {
        super("UpdateSMSReceiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.d("webi","ID SELECTED "+intent.getLongExtra("id",-123));
     /*
        String[] selectionArgs = new String[]{intent.getStringExtra("contact")};
        getContentResolver().update(SmsContract.ALL_SMS_URI, values, SmsContract.SMS_SELECTION, selectionArgs);*/

        markSmsRead(intent.getLongExtra("id",-123));
       // deleteSMS(intent.getLongExtra("id",-123));

    }

    public void markSmsRead(long messageId) {

        ContentValues cv = new ContentValues();
        cv.put("read", "1");

        long changed = getContentResolver().update(Uri.parse("content://sms/" + messageId),cv, null, null);
        Log.e("webi", "Message changed: "+changed);

    }

    /*public void deleteSMS(long messageId) {

        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = getContentResolver().query(uriSms, new String[]{"_id"}, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);

                    if (id == messageId) {
                        getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);

                        Log.e("webi", "Message is Deleted successfully");
                    }

                } while (c.moveToNext());
            }

            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            Log.e("webi", e.toString());
        }

    }

*/

}
