package com.hetekivi.rasian.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.hetekivi.rasian.Data.RSS.Data;
import com.hetekivi.rasian.Data.RSS.Feed;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.AddTask;
import com.hetekivi.rasian.Tasks.RemoveTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;

import static com.hetekivi.rasian.Data.Global.*;
import static com.hetekivi.rasian.Data.Global.Error;
import static com.hetekivi.rasian.Data.Global.Resource;


/**
 * Activity Feeds
 * for controlling feeds.
 */
public class FeedsActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static Context context;

    /**
     * Activity's own UI elements.
     */
    private FloatingActionButton ButtonAdd;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private TableLayout table;

    /**
     * Add dialog's UI elements
     */
    private Dialog          DialogAdd;
    private RelativeLayout  DialogDownloadLayout;
    private EditText        DialogAddUrl;
    private CheckBox        DialogAddNew;
    private CheckBox        DialogAddAll;
    private Button          DialogAddButton;

    private static boolean loading = true;

    private LayoutInflater  inflater    = null; // Inflater for whole activity.

    /**
     * Listener for when feeds saving is completed.
     */
    private Listener FeedSaveListener = new Listener() {

        @Override
        public void onSuccess(Object additional) {
            UpdateTable();
        }


        @Override
        public void onFailure(Object additional) {
            Loading(false);
            String message;
            if(additional != null && additional instanceof Feed)
            {
                message = Resource.String(R.string.Saving, R.string.feed, ((Feed) additional).Title(), R.string.failed_END);
            }
            else message = Resource.String(R.string.Saving, R.string.failed_END);
            Error.Long(message);
        }
    };

    /**
     * Listener for when feed is added.
     */
    private Listener FeedAddListener = new Listener() {
        @Override
        public void onSuccess(Object additional)
        {
            String message;
            if(additional != null && additional instanceof Feed)
            {
                message = Resource.String(R.string.Feed, ((Feed) additional).Title(), R.string.added_END);
            }
            else message = Resource.String(R.string.Feed, R.string.added_END);
            Message.Long(message);
            UpdateTable();
        }

        @Override
        public void onFailure(Object additional) {
            Loading(false);
            String message;
            if(additional != null && additional instanceof Feed)
            {
                message = Resource.String(R.string.Feed, ((Feed) additional).Title(), R.string.added_END);
            }
            else message = Resource.String(R.string.Feed, R.string.added_END);
            Error.Long(message);
        }
    };

    /**
     * Listener for when feed is removed.
     */
    private Listener FeedRemoveListener = new Listener() {
        @Override
        public void onSuccess(Object additional)
        {
            String message;
            if(additional != null && additional instanceof Feed)
            {
                message = Resource.String(R.string.Feed, ((Feed) additional).Title(), R.string.removed_END);
            }
            else message = Resource.String(R.string.Feed, R.string.removed_END);
            Message.Long(message);
            UpdateTable();
        }

        @Override
        public void onFailure(Object additional)
        {
            Loading(false);
            String message;
            if(additional != null && additional instanceof Feed)
            {
                message = Resource.String(R.string.Removing, R.string.feed, ((Feed) additional).Title(), R.string.failed_END);
            }
            else message = Resource.String(R.string.Removing, R.string.failed_END);
            Error.Long(message);
        }
    };


    /**
     * Function onCreate
     * gets called when activity is created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        context = this;
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
        Loading(loading);
        UpdateTable();
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
        this.ButtonAdd = (FloatingActionButton) findViewById(R.id.ActivityFeedsAdd);
        this.toolbar = (Toolbar) findViewById(R.id.ActivityFeedsToolbar);
        table = (TableLayout) findViewById(R.id.ActivityFeedsTable);
        progressBar = (ProgressBar) findViewById(R.id.ActivityFeedsLoading);
        this.DialogAdd = new Dialog(this);
        this.DialogAdd.setContentView(R.layout.dialog_add_rss);
        this.DialogAddUrl = (EditText) this.DialogAdd.findViewById(R.id.DialogAddRssUrl);
        this.DialogAddButton = (Button) this.DialogAdd.findViewById(R.id.DialogAddRssAdd);
        this.scrollView = (ScrollView) findViewById(R.id.ActivityFeedsScrollView);
        this.DialogDownloadLayout   = (RelativeLayout) DialogAdd.findViewById(R.id.DialogRSSAddDownloadLayout);

        this.DialogAddAll = (CheckBox) this.DialogAdd.findViewById(R.id.DialogAddRssDownloadAll);
        this.DialogAddNew = (CheckBox) this.DialogAdd.findViewById(R.id.DialogAddRssDownloadNew);
        this.inflater    = (LayoutInflater)  this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Function UIMakeSetValues
     * for setting UI element's values.
     * (titles, images...)
     */
    private void UIMakeSetValues()
    {

    }

    /**
     * Function UIMakeSetActions
     * for setting actions and listeners
     * to UI elements.
     */
    private void UIMakeSetActions()
    {
        this.DialogAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add();
            }
        });
        this.ButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAddDialog();
            }
        });
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Function ShowAddDialog
     * for showing add dialog.
     */
    private void ShowAddDialog()
    {
        this.DialogAddUrl.setText("");
        int downloadVisibility = (WRITE_EXTERNAL_STORAGE())?View.VISIBLE:View.GONE;
        this.DialogDownloadLayout.setVisibility(downloadVisibility);
        this.DialogAdd.show();
    }

    /**
     * Function Add
     * for adding RSS feed from add dialogs values.
     */
    private void Add()
    {
        this.DialogAdd.hide();
        Loading(true);
        String url = this.DialogAddUrl.getText().toString();
        DateTime lastDownload = new DateTime();
        boolean download = false;
        if(this.DialogDownloadLayout.getVisibility() == View.VISIBLE)
        {
            download = this.DialogAddNew.isChecked() || this.DialogAddAll.isChecked();
            if(this.DialogAddAll.isChecked()) lastDownload = Data.DEFAULT_DATE_TIME.minusYears(1);

        }
        Feed feed = new Feed(url, lastDownload, download);
        new AddTask(Feeds, FeedAddListener, feed).execute(feed);
    }

    /**
     * Function UpdateTable
     * for updating feed item table.
     */
    public void UpdateTable()
    {
        this.table.removeAllViews();
        List<Feed> feeds = new LinkedList<>(Feeds.Feeds().values());
        boolean download = WRITE_EXTERNAL_STORAGE();
        for (Feed feed : feeds)
        {
            this.addRow(feed, download);
        }
        Loading(false);
    }


    /**
     * Function addRow
     * for adding row.
     * @param feed Feed object that will be used for values.
     * @param download Boolean for showing download actions.
     */
    private void addRow(final Feed feed, boolean download)
    {
        RelativeLayout  row         = (RelativeLayout)  this.inflater.inflate(R.layout.row_feeds, null);
        TextView        rowTitle    = (TextView)        row.findViewById(R.id.ActivityFeedsRowTitle);
        rowTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(feed.Url()));
                context.startActivity(intent);
            }
        });
        rowTitle.setText(feed.Title());
        Button          rowDelete   = (Button)          row.findViewById(R.id.ActivityFeedsRowDelete);
        rowDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loading(true);
                new RemoveTask(Feeds, FeedRemoveListener, feed).execute(feed);
            }
        });
        CheckBox rowDownload = (CheckBox) row.findViewById(R.id.ActivityFeedsRowDownloadOn);
        if(download)
        {
            rowDownload.setVisibility(View.VISIBLE);
            rowDownload.setChecked(feed.DownloadOn);
            rowDownload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            feed.DownloadOn = b;
                            new SaveTask(feed, FeedSaveListener, feed).execute();
                        }
                    });
                    t.start();
                }
            });
        }
        else rowDownload.setVisibility(View.GONE);
        this.table.addView(row);
    }

    /**
     * Function Loading
     * for setting loading.
     * @param status Status of loading. false = off, true = on.
     */
    private void Loading(boolean status)
    {
        if(this.progressBar != null)
        {
            if(status) this.progressBar.setVisibility(View.VISIBLE);
            else {
                this.progressBar.setVisibility(View.GONE);
            }
        }
        loading = status;
    }
}
