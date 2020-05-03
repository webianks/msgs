package com.webianks.hatkemessenger.tasks

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.drive.Drive
import java.util.concurrent.CountDownLatch

// Copyright 2013 Google Inc. All Rights Reserved.
/**
 * An AsyncTask that maintains a connected client.
 */
abstract class ApiClientAsyncTask<Params, Progress, Result> internal constructor(context: Context?) : AsyncTask<Params, Progress, Result?>() {
    /**
     * Gets the GoogleApliClient owned by this async task.
     */
    val googleApiClient: GoogleApiClient

    @SafeVarargs
    override fun doInBackground(vararg params: Params): Result? {
        Log.d("TAG", "in background")
        val latch = CountDownLatch(1)
        googleApiClient.registerConnectionCallbacks(object : ConnectionCallbacks {
            override fun onConnectionSuspended(cause: Int) {}
            override fun onConnected(arg0: Bundle?) {
                latch.countDown()
            }
        })
        googleApiClient.registerConnectionFailedListener { latch.countDown() }
        googleApiClient.connect()
        try {
            latch.await()
        } catch (e: InterruptedException) {
            return null
        }
        return if (!googleApiClient.isConnected) {
            null
        } else try {
            doInBackgroundConnected(*params)
        } finally {
            googleApiClient.disconnect()
        }
    }

    /**
     * Override this method to perform a computation on a background thread, while the client is
     * connected.
     */
    protected abstract fun doInBackgroundConnected(vararg params: Params): Result

    init {
        val builder = GoogleApiClient.Builder(context!!)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
        googleApiClient = builder.build()
    }
}