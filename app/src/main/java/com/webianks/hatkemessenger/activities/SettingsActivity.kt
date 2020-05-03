package com.webianks.hatkemessenger.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveApi.DriveContentsResult
import com.google.android.gms.drive.DriveApi.MetadataBufferResult
import com.google.android.gms.drive.DriveFolder.DriveFileResult
import com.google.android.gms.drive.MetadataChangeSet
import com.webianks.hatkemessenger.R
import com.webianks.hatkemessenger.activities.SettingsActivity
import com.webianks.hatkemessenger.constants.Constants
import com.webianks.hatkemessenger.tasks.EditContentsAsyncTask
import com.webianks.hatkemessenger.tasks.RetrieveDriveFileContentsAsyncTask

class SettingsActivity : AppCompatActivity(), ConnectionCallbacks, OnConnectionFailedListener {
    /*@Override
    protected void onStart() {
        super.onStart();
        startDriveApi();
    }
*/ var googleApiClient: GoogleApiClient? = null
        private set
    private val RESOLVE_CONNECTION_REQUEST_CODE = 111
    private var driveContentsResult: DriveContentsResult? = null
    private var progressDialog: ProgressDialog? = null
    private var type = 0
    private val driveContentsCallback: ResultCallback<DriveContentsResult> = object : ResultCallback<DriveContentsResult> {
        override fun onResult(result: DriveContentsResult) {
            if (!result.status.isSuccess) {
                showMessage("Error while trying to create new file contents")
                return
            }
            driveContentsResult = result
            Drive.DriveApi.getAppFolder(googleApiClient)!!.listChildren(googleApiClient).setResultCallback(metadataCallback)
        }
    }
    private val metadataCallback = ResultCallback<MetadataBufferResult> { result ->
        if (!result.status.isSuccess) {
            showMessage("Problem while retrieving files")
            return@ResultCallback
        }
        if (result.metadataBuffer.count == 0) {
            //now create a new file as it doesn't exist
            if (type != Constants.TYPE_RESTORE) createFile() else showMessage("No  backup found")
            return@ResultCallback
        }
        val driveId = result.metadataBuffer[0].driveId
        if (type == Constants.TYPE_RESTORE) {
            //for retrieving the content of the file
            RetrieveDriveFileContentsAsyncTask(this@SettingsActivity).execute(driveId)
        } else {
            //for editing the content of the file
            val file = driveId.asDriveFile()
            EditContentsAsyncTask(this@SettingsActivity).execute(file)
        }
    }

    private fun createFile() {
        val changeSet = MetadataChangeSet.Builder()
                .setTitle("all_sms.json")
                .setMimeType("application/json")
                .build()
        Drive.DriveApi.getAppFolder(googleApiClient)
                ?.createFile(googleApiClient, changeSet, driveContentsResult!!.driveContents)
                ?.setResultCallback(fileCallback)
    }

    private val fileCallback = ResultCallback<DriveFileResult> { result ->
        if (!result.status.isSuccess) {
            showMessage("Error while trying to create the file " + result.status)
            return@ResultCallback
        }
        val file = result.driveFile
        EditContentsAsyncTask(this@SettingsActivity).execute(file)

        //showMessage("Created a file in App Folder: ");
    }

    fun showMessage(message: String?) {
        if (progressDialog != null) progressDialog!!.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        //Log.d(TAG, message);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fragmentManager.beginTransaction().replace(R.id.container,
                MyPreferenceFragment()).commit()
        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESOLVE_CONNECTION_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                googleApiClient!!.connect()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onConnected(bundle: Bundle?) {
        doAfterConnectedStuff()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE)
            } catch (e: SendIntentException) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.errorCode, this, 0).show()
        }
    }

    class MyPreferenceFragment : PreferenceFragment() {
        private var drivePref: Preference? = null
        private var restorePref: Preference? = null
        private val TAG = MyPreferenceFragment::class.java.simpleName
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            drivePref = findPreference(Constants.BACKUP)
            drivePref?.onPreferenceClickListener = OnPreferenceClickListener {
                createBackup()
                true
            }
            restorePref = findPreference(Constants.RESTORE)
            restorePref?.onPreferenceClickListener = OnPreferenceClickListener { //backup dialog
                restoreBackup()
                true
            }
        }

        private fun createBackup() {
            (activity as SettingsActivity).startDriveApi(Constants.TYPE_BACKUP)
        }

        private fun restoreBackup() {
            (activity as SettingsActivity).startDriveApi(Constants.TYPE_RESTORE)
        }
    }

    private fun startDriveApi(type: Int) {
        this.type = type
        if (!googleApiClient!!.isConnected) googleApiClient!!.connect() else doAfterConnectedStuff()
    }

    private fun doAfterConnectedStuff() {
        if (progressDialog == null) progressDialog = ProgressDialog(this)
        if (type == Constants.TYPE_BACKUP) progressDialog!!.setTitle(getString(R.string.google_drive_backup)) else progressDialog!!.setTitle(getString(R.string.google_drive_restore))
        progressDialog!!.setMessage(getString(R.string.please_wait))
        progressDialog!!.isIndeterminate = true
        progressDialog!!.show()
        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(driveContentsCallback)
    }
}