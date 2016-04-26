package com.hetekivi.rasian.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.NFC;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.FromJSONTask;
import com.hetekivi.rasian.Tasks.GetRowsTask;
import com.hetekivi.rasian.Tasks.ToJSONTask;

import java.io.File;
import java.io.FileDescriptor;

import static com.hetekivi.rasian.Data.Global.*;

public class SaveLoadActivity extends AppCompatActivity implements NFC {

    private static final String TAG = "SaveLoadActivity";

    private static Context context = null;
    private Intent requestFileIntent = null;
    private final int FILE_INTENT = 0;


    /**
     * Activity's own UI elements.
     */
    private FloatingActionButton    ButtonSave          = null;
    private FloatingActionButton    ButtonLoad          = null;
    private FloatingActionButton    ButtonSend          = null;
    private TextView                MiddleButtonText    = null;
    private TextView                LeftButtonText    = null;
    private Toolbar Toolbar = null;

    /**
     * Listeners
     */

    /**
     * Listener for when data has been saved to JSON file.
     */
    private Listener ToJSONSaveListener = new Listener() {
        @Override
        public void onSuccess(Object additional) {
            Message.Long(Resource.String(R.string.Saved_END));
        }

        @Override
        public void onFailure(Object additional) {
            Error.Long(Resource.String(R.string.Saving, R.string.failed_END));
        }
    };

    /**
     * Listener for sending JSON.
     */
    private Listener ToJSONSendListener = new Listener() {
        @Override
        public void onSuccess(Object additional) {
            Send();
        }

        @Override
        public void onFailure(Object additional) {

        }
    };

    /**
     * Listener for loading JSON.
     */
    private Listener FromJSONLoadListener = new Listener() {
        @Override
        public void onSuccess(Object additional) {
            Message.Long(Resource.String(R.string.Loaded_END));
            new GetRowsTask(Feeds).execute();
        }

        @Override
        public void onFailure(Object additional) {
            Error.Long(Resource.String(R.string.Loading, R.string.failed_END));
        }
    };

    /**
     * Function onCreate
     * gets called when activity is created.
     * @param savedInstanceState Bundle containing saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_load);
        context = this;
        requestFileIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("application/octet-stream");
        UIMake();
    }
    /**
     * Function onResume
     * gets called when activity activates.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        this.UIReset();
    }

    /**
     * Function UIMake
     * for making and linking
     * UI elements.
     */
    private void UIMake()
    {
        this.UIMakeSelect();
        this.UIMakeSetValues();
        this.UIMakeSetActions();
    }

    /**
     * Function UIReset
     * for resetting UI
     * element values.
     */
    private void UIReset()
    {
        this.UIMakeSetValues();
    }

    /**
     * Function UIMakeSelect
     * for linking UI elements to
     * class methods.
     */
    private void UIMakeSelect()
    {
        this.ButtonSave         = (FloatingActionButton) findViewById(R.id.ActivitySaveLoadSave);
        this.ButtonLoad         = (FloatingActionButton) findViewById(R.id.ActivitySaveLoadLoad);
        this.ButtonSend         = (FloatingActionButton) findViewById(R.id.ActivitySaveLoadSend);
        this.Toolbar            = (Toolbar) findViewById(R.id.ActivitySaveLoadToolbar);
        this.LeftButtonText     = (TextView) findViewById(R.id.ActivitySaveLoadLeftButton);
        this.MiddleButtonText   = (TextView) findViewById(R.id.ActivitySaveLoadMiddleButton);
    }

    /**
     * Function UIMakeSetValues
     * for setting UI element's values.
     * (titles, images...)
     */
    private void UIMakeSetValues()
    {
        int nfcVisibility = (NFCSupported(this))?View.VISIBLE:View.GONE;
        this.ButtonSend.setVisibility(nfcVisibility);
        this.MiddleButtonText.setVisibility(nfcVisibility);
        int downloadVisibility = (WRITE_EXTERNAL_STORAGE())?View.VISIBLE:View.GONE;
        this.ButtonSave.setVisibility(downloadVisibility);
        this.LeftButtonText.setVisibility(downloadVisibility);
        this.ButtonSend.setVisibility(downloadVisibility);
        this.MiddleButtonText.setVisibility(downloadVisibility);

    }

    /**
     * Function UIMakeSetActions
     * for setting actions and listeners
     * to UI elements.
     */
    private void UIMakeSetActions()
    {
        this.ButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartSaving();
            }
        });
        this.ButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NFCSupported(context)) {StartSending();}
            }
        });
        this.ButtonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartLoading();
            }
        });
        setSupportActionBar(this.Toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    /**
     * Function StartSending
     * for sending data with NFC.
     */
    private void StartSending()
    {
        if(NFCSupported(this)) {
            new ToJSONTask(Feeds, ToJSONSendListener).execute(OptionsFile());
        }
    }

    /**
     * Function StartSaving
     * for starting to save data to JSON file.
     */
    private void StartSaving()
    {
        new ToJSONTask(Feeds, ToJSONSaveListener).execute(OptionsFile());
    }

    /**
     * StartLoading
     * for stating to load data from JSON file.
     */
    private void StartLoading()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Resource.String(R.string.OverwriteWarning)).setPositiveButton(Resource.String(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivityForResult(requestFileIntent, FILE_INTENT);
            }
        }).setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }

    /**
     * Function Send
     * for sending data file with NFC.
     * @return Success of the send.
     */
    @Override
    public boolean Send() {
        boolean success = false;
        if(NFCSupported(this))
        {
            Log.d(TAG, "Starting to send!");
            NfcAdapter NFCAdapter = NfcAdapter.getDefaultAdapter(this);
            if(!NFCAdapter.isEnabled()){
                Toast.makeText(this, "Please enable NFC.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }
            else if(!NFCAdapter.isNdefPushEnabled()) {
                Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            }
            else {

                Log.d(TAG, "Getting file!");
                File fileToTransfer = OptionsFile();
                if(fileToTransfer.exists())
                {
                    fileToTransfer.setReadable(true, false);
                    NFCAdapter.setBeamPushUris(new Uri[]{Uri.fromFile(fileToTransfer)}, this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        NFCAdapter.invokeBeam(this);
                    }
                    success = true;
                    Log.d(TAG, "Sending file! "+fileToTransfer.getPath());
                }else Log.e(TAG, "File does not exist!");
            }
        }
        return success;
    }

    /**
     * Function onActivityResult
     * as listener for when activity returns with result.
     * @param requestCode Code for request.
     * @param resultCode Code for result.
     * @param data Data for activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Uri returnUri = data.getData();
            if(returnUri != null)
            {
                String filename = new File(returnUri.getPath()).getName();
                if(filename.equals(OPTIONS_FILE_NAME))
                {
                    try
                    {
                        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(returnUri, "r");
                        if(parcelFileDescriptor != null)
                        {
                            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                            new FromJSONTask(Feeds, FromJSONLoadListener).execute(fileDescriptor);
                        }
                        else
                        {
                            Log.e(TAG, "Making ParcelFileDescriptor failed!");
                            Error.Long(Resource.String(R.string.GettingFileFailed));
                        }

                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.getLocalizedMessage());
                        Error.Long(Resource.String(R.string.GettingFileFailed));
                    }
                }
                else
                {
                    Log.e(TAG, "File name "+filename+" was not "+OPTIONS_FILE_NAME);
                    Error.Long(Resource.String(R.string.File, R.string.must_be, OPTIONS_FILE_NAME));
                }
            }
            else
            {
                Log.e(TAG, "Return uri was null");
                Error.Long(Resource.String(R.string.GettingFileFailed));
            }
        }
        else
        {
            Log.e(TAG, "Result was not success! CODE: "+resultCode);
            Error.Long(Resource.String(R.string.GettingFileFailed));
        }
    }

    /**
     * Function Receive
     * for receiving data.
     * @return Success of receive.
     */
    @Override
    public boolean Receive() {
        return false;
    }
}
