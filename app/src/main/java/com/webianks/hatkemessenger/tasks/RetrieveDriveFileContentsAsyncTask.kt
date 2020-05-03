package com.webianks.hatkemessenger.tasks

import android.content.Context
import android.util.Log
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveId
import com.webianks.hatkemessenger.activities.SettingsActivity
import com.webianks.hatkemessenger.constants.Constants
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class RetrieveDriveFileContentsAsyncTask(private val context: Context) :
        ApiClientAsyncTask<DriveId?, Boolean?, String?>(context) {


    private val TAG = RetrieveDriveFileContentsAsyncTask::class.java.simpleName


     override fun doInBackgroundConnected(vararg params: DriveId?): String? {
        var contents: String? = null
        val file = params[0]?.asDriveFile()
        val driveContentsResult = file?.open(googleApiClient, DriveFile.MODE_READ_ONLY, null)?.await()
         if (driveContentsResult != null) {
             if (!driveContentsResult.status.isSuccess) {
                 return null
             }
         }
        val driveContents = driveContentsResult?.driveContents
        val reader = BufferedReader(
                InputStreamReader(driveContents?.inputStream))
        val builder = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
            contents = builder.toString()
        } catch (e: IOException) {
            Log.e(TAG, "IOException while reading from the stream", e)
        }
        driveContents?.discard(googleApiClient)
        return contents
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result == null) {
            (context as SettingsActivity).showMessage("Error while reading from the file")
            return
        }
        (context as SettingsActivity).showMessage("Restored your messages. ")
        restoreJSON(result)
    }

    private fun restoreJSON(json: String) {
        val sp = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(Constants.SMS_JSON, json)
        editor.apply()
    }

}