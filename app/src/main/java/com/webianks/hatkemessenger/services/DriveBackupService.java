package com.webianks.hatkemessenger.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webianks.hatkemessenger.Sms;
import com.webianks.hatkemessenger.constants.Constants;

import java.lang.reflect.Type;
import java.util.List;

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
