package com.webianks.hatkemessenger.constants;

import android.net.Uri;

/**
 * Created by R Ankit on 25-12-2016.
 */

public class SmsContract {

    public static final Uri ALL_SMS_URI = Uri.parse("content://sms/");
    public static final String SMS_SELECTION = "address = ? ";

}