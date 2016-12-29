package com.webianks.hatkemessenger.tasks;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.webianks.hatkemessenger.activities.SettingsActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by R Ankit on 28-12-2016.
 */

public class RetrieveDriveFileContentsAsyncTask
        extends ApiClientAsyncTask<DriveId, Boolean, String> {

    private String TAG = RetrieveDriveFileContentsAsyncTask.class.getSimpleName();
    private Context context;

    public RetrieveDriveFileContentsAsyncTask(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected String doInBackgroundConnected(DriveId... params) {
        String contents = null;
        DriveFile file = params[0].asDriveFile();
        DriveApi.DriveContentsResult driveContentsResult =
                file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            return null;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(driveContents.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            contents = builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading from the stream", e);
        }

        driveContents.discard(getGoogleApiClient());
        return contents;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null) {
            ((SettingsActivity)context).showMessage("Error while reading from the file");
            return;
        }
        //showMessage("File contents: " + result);
        //Log.d(TAG,result);
    }
}

