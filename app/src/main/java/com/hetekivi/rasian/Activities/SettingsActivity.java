package com.hetekivi.rasian.Activities;

import android.content.Context;
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
import org.joda.time.DateTime;

import static com.hetekivi.rasian.Data.Global.*;

/**
 * Activity Settings
 * for managing settings.
 */
public class SettingsActivity extends AppCompatActivity implements Storable {

    private static final String TAG = "SettingsActivity";
    public static Context context;

    /**
     * Activity's own UI elements.
     */
    private EditText delay;
    private EditText limit;
    private Toolbar toolbar;
    private TextView nextUpdate;
    private Button save;


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
        this.delay = (EditText) findViewById(R.id.ActivitySettingDelay);
        this.limit = (EditText) findViewById(R.id.ActivitySettingLimit);
        this.toolbar = (Toolbar) findViewById(R.id.ActivitySettingsToolbar);
        this.nextUpdate = (TextView) findViewById(R.id.ActivitySettingNextUpdate);
        this.save = (Button) findViewById(R.id.ActivitySettingSave);
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
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClick();
            }
        });

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
                    delay.setBackgroundColor(colorError);
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
            new UpdateTask(Feeds).execute();
            return Preference.Set(Global.ROW_LIMIT_KEY, value);
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
        final String delayText      = String.valueOf(Preference.Get(FeedCollection.PREF_DELAY_HOURS, 24));
        final String limitText      = String.valueOf(Preference.Get(Global.ROW_LIMIT_KEY, 10));
        final String nextUpdateText = this.getNextUpdate();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delay.setText(delayText);
                limit.setText(limitText);
                nextUpdate.setText(nextUpdateText);
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
