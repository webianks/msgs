package com.webianks.hatkemessenger.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import com.webianks.hatkemessenger.constants.Constants;

/**
 * Created by R Ankit on 28-12-2016.
 */

public class DriveBackupService extends IntentService{

    public DriveBackupService() {
        super("DriveBackup");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sp = getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
        String json_sms = sp.getString(Constants.SMS_JSON,null);

    }


}
