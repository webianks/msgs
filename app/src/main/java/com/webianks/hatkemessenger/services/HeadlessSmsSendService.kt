package com.webianks.hatkemessenger.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by R Ankit on 24-12-2016.
 */
class HeadlessSmsSendService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}