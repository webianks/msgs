package com.webianks.hatkemessenger.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.webianks.hatkemessenger.constants.SmsContract;

/**
 * Created by R Ankit on 26-12-2016.
 */

public class SaveSmsService extends IntentService {

    public SaveSmsService() {
        super("SaveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String senderNo = intent.getStringExtra("sender_no");
        String message = intent.getStringExtra("message");
        long time = intent.getLongExtra("date",0);

        ContentValues values = new ContentValues();
        values.put("address", senderNo);
        values.put("body", message);
        values.put("date_sent",time);
        getContentResolver().insert(SmsContract.ALL_SMS_URI, values);

        Intent i = new Intent("android.intent.action.MAIN").putExtra("new_sms", true);
        this.sendBroadcast(i);

    }
}
