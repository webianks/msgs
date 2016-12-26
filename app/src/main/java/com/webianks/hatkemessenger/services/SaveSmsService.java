package com.webianks.hatkemessenger.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

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

        ContentValues values = new ContentValues();
        values.put("address", senderNo);
        values.put("body", message);
        getContentResolver().insert(Uri.parse("content://sms"), values);

    }
}
