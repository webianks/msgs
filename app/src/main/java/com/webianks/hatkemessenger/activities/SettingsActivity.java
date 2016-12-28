package com.webianks.hatkemessenger.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.utils.Helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private final int RESOLVE_CONNECTION_REQUEST_CODE = 111;
    private String TAG = SettingsActivity.class.getSimpleName();
    private DriveApi.DriveContentsResult driveContentsResult;

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    driveContentsResult = result;
                    //first try to get the file if exists already
                    Drive.DriveApi.getAppFolder(getGoogleApiClient()).listChildren(mGoogleApiClient).setResultCallback(metadataCallback);

                }
            };


    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }

                    if (result.getMetadataBuffer().getCount() == 0) {
                        //now create a new file as it doesn't exist
                        createFile();
                        return;
                    }

                    // mResultsAdapter.clear();
                    //  mResultsAdapter.append(result.getMetadataBuffer());
                    showMessage("Successfully listed files. " + result.getMetadataBuffer().getCount());

                }
            };

    private void createFile() {

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("all_sms.json")
                .setMimeType("application/json")
                .build();

        Drive.DriveApi.getAppFolder(getGoogleApiClient())
                .createFile(getGoogleApiClient(), changeSet, driveContentsResult.getDriveContents())
                .setResultCallback(fileCallback);
    }


    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file "+result.getStatus());
                        return;
                    }


                    DriveFile file = result.getDriveFile();
                    file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null)
                            .setResultCallback(contentsOpenedCallback);

                    showMessage("Created a file in App Folder: " + result.getDriveFile().getDriveId());
                }
            };

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        //Log.d(TAG, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().
                replace(R.id.container,
                        new MyPreferenceFragment()).commit();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //Toast.makeText(this, "Connected to drive api", Toast.LENGTH_LONG).show();
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);


    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        startDriveApi();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private SwitchPreference drivePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            final SharedPreferences.Editor my_prefrence = PreferenceManager.
                    getDefaultSharedPreferences(getActivity()).edit();

            drivePref = (SwitchPreference) findPreference(Constants.BACKUP);
            drivePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    //backup dialog

                    //((SettingsActivity) getActivity()).startDriveApi();

                    return true;
                }
            });
        }
    }

    private void startDriveApi() {
        mGoogleApiClient.connect();
    }

    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        // display an error saying file can't be opened
                        return;
                    }
                    // DriveContents object contains pointers
                    // to the actual byte stream
                    DriveContents contents = result.getDriveContents();

                    try {
                        ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                        FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor
                                .getFileDescriptor());
                        // Read to the end of the file.
                        fileInputStream.read(new byte[fileInputStream.available()]);

                        // Append to the file.
                        FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor
                                .getFileDescriptor());
                        Writer writer = new OutputStreamWriter(fileOutputStream);
                        writer.write(Helpers.getSMSJson(SettingsActivity.this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    contents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status result) {
                            // Handle the response status

                            if (result.isSuccess())
                                showMessage("Successfully added sms to the JSON file.");
                        }
                    });

                }
            };


}
