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
import com.hetekivi.rasian.Data.RSS.RSSCollection;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.UpdateTask;
import org.joda.time.DateTime;

import static com.hetekivi.rasian.Data.Global.*;

public class SettingsActivity extends AppCompatActivity {

    private EditText delay;
    private EditText limit;
    private Toolbar toolbar;
    private TextView nextUpdate;
    private Button save;

    public static Context context;

    private int colorDefault;
    private int colorError;

    private final String KEY_DELAY = RSSCollection.PREFERENCES_START + "delayHours";
    private final String KEY_LIMIT = Global.ROW_LIMIT_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_settings);
        this.colorError = ContextCompat.getColor(this, R.color.colorError);
        this.colorDefault = Color.TRANSPARENT;
        this.UIMake();
    }

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
        this.delay.setText(String.valueOf(Preference.Get(this.KEY_DELAY, 24)));
        this.limit.setText(String.valueOf(Preference.Get(this.KEY_LIMIT, 10)));
        makeNextUpdate();
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
                SaveAll();
            }
        });

    }

    private void makeNextUpdate()
    {
        DateTime dateTime = Feeds.NextUpdate;

        if(dateTime != null)
        {
            String text = getString(R.string.ActivitySettingNextUpdate);
            String date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(dateTime.toDate());
            String time = android.text.format.DateFormat.getTimeFormat(getApplicationContext()).format(dateTime.toDate());
            text += date + " " + time;
            this.nextUpdate.setText(text);
        }
        else
        {
            this.nextUpdate.setText("");
        }

    }

    private void SaveAll()
    {
        boolean success = this.saveDelay();
        success = this.saveRowLimit() && success;
        if(success)
        {
            Message.Long(R.string.ToastSaveSuccessful);
        }
        else
        {
            Error.Long(R.string.ToastSaveFailed);
        }
    }

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

    private boolean saveDelay()
    {
        this.delay.setBackgroundColor(this.colorDefault);
        int value = this.getUnsignedInt(this.delay);
        if(value >= 0)
        {
            String key = RSSCollection.PREFERENCES_START + "delayHours";
            Global.Feeds.setAlarm(value);
            makeNextUpdate();
            return Preference.Set(key, value);
        }
        else
        {
            return false;
        }
    }

    private boolean saveRowLimit()
    {
        this.limit.setBackgroundColor(this.colorDefault);
        int value = this.getUnsignedInt(this.limit);
        if(value >= 0)
        {
            String key = Global.ROW_LIMIT_KEY;
            new UpdateTask(Feeds).execute();
            return Preference.Set(key, value);
        }
        else
        {
            return false;
        }
    }
}
