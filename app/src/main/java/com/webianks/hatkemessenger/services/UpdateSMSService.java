package com.webianks.hatkemessenger.services;

import android.app.IntentService;
import android.content.ContentValues;
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


        Log.d("webi",intent.getStringExtra("id"));
     /*
        String[] selectionArgs = new String[]{intent.getStringExtra("contact")};
        getContentResolver().update(SmsContract.ALL_SMS_URI, values, SmsContract.SMS_SELECTION, selectionArgs);*/
        markSmsRead(intent.getStringExtra("id"));

    }

    public void markSmsRead(String id) {

        ContentValues cv = new ContentValues();
        cv.put("message", "I have modified the message.");

        //long affected = getContentResolver().update(Uri.parse("content://sms/" + id), cv, null, null);

        //String[] selectionArgs = new String[]{id};
        //long affected = getContentResolver().update(SmsContract.ALL_SMS_URI, cv, SmsContract.SMS_SELECTION_ID, selectionArgs);

        String[] selectionArgs = {id};

        Cursor found = getContentResolver().query(SmsContract.ALL_SMS_URI,
                new String[]{SmsContract.COLUMN_ID},
                SmsContract.SMS_SELECTION_ID,
                selectionArgs,
                null);

        if (found.moveToFirst()){

            Log.d("webi"," Affected: "+found.getString(found.getColumnIndex(SmsContract.COLUMN_ID)));

            long affected = getContentResolver().
                    delete(SmsContract.ALL_SMS_URI, SmsContract.SMS_SELECTION_ID, selectionArgs);

            Log.d("webi"," Affected: "+affected);


        }


    }

}
