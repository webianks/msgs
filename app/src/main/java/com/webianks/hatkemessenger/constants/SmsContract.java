package com.webianks.hatkemessenger.constants;

import android.net.Uri;

/**
 * Created by R Ankit on 25-12-2016.
 */

public class SmsContract {

    public static final Uri ALL_SMS_URI = Uri.parse("content://sms/inbox");
    public static final String SMS_SELECTION = "address = ? ";
    public static final String SMS_SELECTION_ID = "_id = ? ";
    public static final String COLUMN_ID = "_id";
    public static final String SMS_SELECTION_SEARCH = "address LIKE ? OR body LIKE ?";
    public static final String SORT_DESC = "date DESC";

}
