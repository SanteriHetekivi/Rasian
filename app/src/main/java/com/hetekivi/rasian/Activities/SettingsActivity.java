package com.hetekivi.rasian.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Data.RSS.FeedCollection;
import com.hetekivi.rasian.Interfaces.Storable;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.LoadTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import com.hetekivi.rasian.Tasks.UpdateTask;
import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import org.joda.time.DateTime;

import java.io.File;

import static com.hetekivi.rasian.Data.Global.*;

/**
 * Activity Settings
 * for managing settings.
 */
public class SettingsActivity extends AppCompatActivity implements Storable {

    private static final String TAG = "SettingsActivity";
    public static Context context;
    public static final int REQUEST_DIRECTORY = 1;

    /**
     * Activity's own UI elements.
     */
    private EditText delay;
    private EditText limit;
    private Button selectDownloadDir;
    private TextView downloadDir;
    private Toolbar toolbar;
    private TextView nextUpdate;
    private Button save;

    private TableRow downloadDirTitleRow;
    private TableRow downloadDirRow;

    /**
     * Background colors for fields.
     */
    private int colorDefault;
    private int colorError;


    /**
     * Function onCreate
     * gets called when activity is created.
     * @param savedInstanceState Bundle containing saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_settings);
        this.colorError = ContextCompat.getColor(this, R.color.colorError);
        this.colorDefault = Color.TRANSPARENT;
        this.UIMake();
    }

    /**
     * Function firstTime
     * gets run on first time when app starts.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Message.UpdateContext(context);
        int downloadVisibility = (WRITE_EXTERNAL_STORAGE())?View.VISIBLE:View.GONE;
        this.downloadDirTitleRow.setVisibility(downloadVisibility);
        this.downloadDirRow.setVisibility(downloadVisibility);
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
        this.delay                  = (EditText) findViewById(R.id.ActivitySettingDelay);
        this.limit                  = (EditText) findViewById(R.id.ActivitySettingLimit);
        this.toolbar                = (Toolbar) findViewById(R.id.ActivitySettingsToolbar);
        this.nextUpdate             = (TextView) findViewById(R.id.ActivitySettingNextUpdate);
        this.save                   = (Button) findViewById(R.id.ActivitySettingSave);
        this.downloadDir            = (TextView) findViewById(R.id.ActivitySettingDownloadDir);
        this.selectDownloadDir      = (Button) findViewById(R.id.ActivitySettingsDownloadDirButton);
        this.downloadDirTitleRow    = (TableRow) findViewById(R.id.ActivitySettingDownloadDirTitleRow);
        this.downloadDirRow         = (TableRow) findViewById(R.id.ActivitySettingDownloadDirRow);
    }

    /**
     * Function UIMakeSetValues
     * for setting UI element's values.
     * (titles, images...)
     */
    private void UIMakeSetValues()
    {
        new LoadTask(this).execute();
    }

    /**
     * Function UIMakeSetActions
     * for setting actions and listeners
     * to UI elements.
     */
    private void UIMakeSetActions()
    {
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.selectDownloadDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDirectory();
            }
        });
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClick();
            }
        });

    }

    /**
     * Function selectDirectory
     * for starting activity for selecting directory.
     */
    private void selectDirectory()
    {
        final Intent chooserIntent = new Intent(this, DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName(Resource.String(R.string.DownloadDirectory))
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();
        chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
        startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
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

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                setDirectory(data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            }
        }
    }

    /**
     * Function setDirectory
     * for setting given string as download directory if it is path to directory.
     * @param directory Path to directory.
     */
    private void setDirectory(String directory) {
        if(directory != null)
        {
            File dir = new File(directory);
            if(dir.exists() && dir.isDirectory())
            {
                this.downloadDir.setText(directory);
            }
        }
    }

    /**
     * Function onSaveClick
     * that gets called when save button was pressed.
     */
    private void onSaveClick()
    {
        new SaveTask(this).execute();
    }

    /**
     * Function getNextUpdate
     * for getting text for nextUpdate field.
     * @return Formatted text for next update.
     */
    private String getNextUpdate()
    {
        DateTime dateTime = Feeds.NextUpdate;
        String text = "";
        if(dateTime != null)
        {
            text = getString(R.string.NextUpdate_START);
            String date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(dateTime.toDate());
            String time = android.text.format.DateFormat.getTimeFormat(getApplicationContext()).format(dateTime.toDate());
            text += date + " " + time;
        }
        return text;
    }

    /**
     * Function SaveAll
     * for saving all values.
     * @return Success of save.
     */
    private boolean SaveAll()
    {
        boolean success = this.saveDelay();
        success = this.saveRowLimit() && success;
        success = this.saveDownloadDir() && success;
        return success;
    }

    /**
     * Function getUnsignedInt
     * for getting unsigned int from EditText.
     * @param editText Field to get int from.
     * @return Fields value as int or -1 if failed.
     */
    private int getUnsignedInt(EditText editText)
    {
        int value;
        String str = editText.getText().toString();
        if(str.isEmpty())
        {
            value = 0;
            editText.setText("0");
        }
        else
        {
            try
            {
                value = Integer.parseInt(editText.getText().toString());
            }catch (NumberFormatException ex)
            {
                value = -1;
            }
        }
        return value;
    }

    /**
     * Function saveDelay
     * for saving update delay.
     * @return Success of save.
     */
    private boolean saveDelay()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delay.setBackgroundColor(colorDefault);
            }
        });
        String value = this.downloadDir.getText().toString();
        if(!value.isEmpty())
        {
            File dir = new File(value);
            if(dir.exists() && dir.isDirectory())
            {
                Feeds.downloadDir = dir;
                return Preference.Set(FeedCollection.PREF_DOWNLOAD_DIR, value);
            }

        }
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    delay.setBackgroundColor(colorError);
                }
            });
        return false;
    }

    /**
     * Function for saving Download Directory
     * @return
     */
    private boolean saveDownloadDir()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delay.setBackgroundColor(colorDefault);
            }
        });
        int value = this.getUnsignedInt(this.delay);
        if(value >= 0)
        {
            Global.Feeds.setAlarm(value);
            return Preference.Set(FeedCollection.PREF_DELAY_HOURS, value);
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadDir.setBackgroundColor(colorError);
                }
            });
            return false;
        }
    }

    /**
     * Function saveRowLimit
     * for saving row limit.
     * @return Success of save.
     */
    private boolean saveRowLimit()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                limit.setBackgroundColor(colorDefault);
            }
        });
        int value = this.getUnsignedInt(this.limit);
        if(value >= 0)
        {
            Global.Feeds.RowLimit = value;
            new UpdateTask(Feeds).execute();
            return Preference.Set(FeedCollection.PREF_ROW_LIMIT, value);
        }
        else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    limit.setBackgroundColor(colorError);
                }
            });
            return false;
        }
    }

    /**
     * Function Load
     * for loading data to fields.
     * @return Success of load.
     */
    @Override
    public boolean Load() {
        final String delayText          = String.valueOf(Preference.Get(FeedCollection.PREF_DELAY_HOURS,
                FeedCollection.DEFAULT_DELAY_HOURS));
        final String limitText          = String.valueOf(Preference.Get(FeedCollection.PREF_ROW_LIMIT,
                FeedCollection.DEFAULT_ROW_LIMIT));
        final String downloadDirText    = Preference.Get(FeedCollection.PREF_DOWNLOAD_DIR,
                FeedCollection.DEFAULT_DOWNLOAD_DIR);

        final String nextUpdateText = this.getNextUpdate();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delay.setText(delayText);
                limit.setText(limitText);
                nextUpdate.setText(nextUpdateText);
                downloadDir.setText(downloadDirText);
            }
        });
        return true;
    }

    /**
     * Function onLoadSuccess
     * This gets called when loading has been done and was successful.
     */
    @Override
    public void onLoadSuccess() {

    }

    /**
     * Function onLoadFailure
     * This gets called when loading has been done and there were errors.
     */
    @Override
    public void onLoadFailure() {
        Error.Long(Resource.String(R.string.Loading, R.string.failed_END));
    }

    /**
     * Function Save
     * for saving data from fields.
     * @return Success of save.
     */
    @Override
    public boolean Save() {
        return this.SaveAll();
    }

    /**
     * Function onSaveSuccess
     * This gets called when saving has been done and was successful.
     * Informing user and loading fields.
     */
    @Override
    public void onSaveSuccess() {
        Message.Long(R.string.Saved_END);
        new LoadTask(this).execute();
    }

    /**
     * Function onSaveFailure
     * This gets called when saving has been done and there were errors.
     */
    @Override
    public void onSaveFailure() {
        Error.Long(Resource.String(R.string.Saving, R.string.failed_END));
    }
}
