package com.webianks.hatkemessenger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.constants.Constants;
import com.webianks.hatkemessenger.tasks.EditContentsAsyncTask;
import com.webianks.hatkemessenger.tasks.RetrieveDriveFileContentsAsyncTask;

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private final int RESOLVE_CONNECTION_REQUEST_CODE = 111;
    private DriveApi.DriveContentsResult driveContentsResult;
    private ProgressDialog progressDialog;
    private int type;

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    driveContentsResult = result;

                    Drive.DriveApi.getAppFolder(getGoogleApiClient()).listChildren(mGoogleApiClient).
                            setResultCallback(metadataCallback);

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

                        if (type != Constants.TYPE_RESTORE)
                            createFile();
                        else
                           showMessage("No  backup found");

                        return;
                    }

                    DriveId driveId = result.getMetadataBuffer().get(0).getDriveId();

                    if (type == Constants.TYPE_RESTORE){
                        //for retrieving the content of the file
                        new RetrieveDriveFileContentsAsyncTask(SettingsActivity.this).execute(driveId);
                    }
                    else{
                        //for editing the content of the file
                        DriveFile file = driveId.asDriveFile();
                        new EditContentsAsyncTask(SettingsActivity.this).execute(file);
                    }

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
                        showMessage("Error while trying to create the file " + result.getStatus());
                        return;
                    }


                    DriveFile file = result.getDriveFile();
                    new EditContentsAsyncTask(SettingsActivity.this).execute(file);

                    //showMessage("Created a file in App Folder: ");

                }
            };

    public void showMessage(String message) {

        if (progressDialog!=null)
            progressDialog.dismiss();

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
       doAfterConnectedStuff();
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

    /*@Override
    protected void onStart() {
        super.onStart();
        startDriveApi();
    }
*/
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private Preference drivePref;
        private Preference restorePref;
        private String TAG = MyPreferenceFragment.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            drivePref =  findPreference(Constants.BACKUP);
            drivePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    createBackup();
                    return true;

                }
            });

            restorePref = findPreference(Constants.RESTORE);
            restorePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    //backup dialog
                    restoreBackup();
                    return true;

                }
            });
        }

        private void createBackup() {
            ((SettingsActivity) getActivity()).startDriveApi(Constants.TYPE_BACKUP);
        }

        private void restoreBackup() {
            ((SettingsActivity) getActivity()).startDriveApi(Constants.TYPE_RESTORE);
        }

    }

    private void startDriveApi(int type) {
        this.type = type;

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        else
            doAfterConnectedStuff();
    }

    private void doAfterConnectedStuff() {

        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);

        if (type == Constants.TYPE_BACKUP)
            progressDialog.setTitle(getString(R.string.google_drive_backup));
        else
            progressDialog.setTitle(getString(R.string.google_drive_restore));

        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);

    }


}
