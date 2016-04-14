package com.hetekivi.rasian.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Data.RSS.Data;
import com.hetekivi.rasian.Data.RSS.Feed;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.*;
import org.joda.time.DateTime;

import java.util.List;
import static com.hetekivi.rasian.Data.Global.*;

/**
 * Activity Main
 * Main page of the app.
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public static Context context;

    /**
     * Activity's own UI elements.
     */
    private FloatingActionButton    ButtonAdd;
    private Toolbar                 toolbar;
    private TableLayout             table;
    private ProgressBar             progressBar;
    private SwipeRefreshLayout      swipeRefreshLayout;
    private ScrollView              scrollView;


    /**
     * Add dialog's UI elements
     */
    private Dialog      DialogAdd;
    private EditText    DialogAddUrl;
    private CheckBox    DialogAddNew;
    private CheckBox    DialogAddAll;
    private Button      DialogAddButton;

    private static boolean  firstTime   = true;     // Is first time running.
    private static boolean  loading     = false;    // Is loading.

    private LayoutInflater  inflater    = null; // Inflater for whole activity.

    /**
     * Listener for when load is completed.
     */
    private Listener FeedsLoadListener = new Listener() {
        @Override
        public void onSuccess(Object additional) {
            new UpdateTask(Feeds, FeedsUpdateListener).execute();
        }

        @Override
        public void onFailure(Object additional) {
            Loading(false);
            String message;
            if(additional != null && additional instanceof Feed)
            {
                message = Resource.String(R.string.Loading, R.string.feed, ((Feed) additional).Title(), R.string.failed_END);
            }
            else message = Resource.String(R.string.Loading, R.string.failed_END);
            Error.Long(message);
        }
    };

    /**
     * Listener for when update is completed.
     */
    private Listener FeedsUpdateListener = new Listener() {

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
                message = Resource.String(R.string.Updating, R.string.feed, ((Feed) additional).Title(), R.string.failed_END);
            }
            else message = Resource.String(R.string.Updating, R.string.failed_END);
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
                message = Resource.String(R.string.Adding, R.string.feed, ((Feed) additional).Title(), R.string.failed_END);
            }
            else message = Resource.String(R.string.Adding, R.string.failed_END);
            Error.Long(message);
        }
    };

    /**
     * Function onCreate
     * gets called when activity is created.
     * @param savedInstanceState Bundle containing saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Global.Init(context);
        this.UIMake();
        Loading(loading);
        if(firstTime)
        {
           this.firstTime();
        }
        else
        {
            Loading(true);
            UpdateTable();
        }
    }

    /**
     * Function firstTime
     * gets run on first time when app starts.
     */
    private void firstTime()
    {
        Loading(true);
        new LoadTask(Feeds, FeedsLoadListener).execute();
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        firstTime = false;
    }

    /**
     * Function onRequestPermissionsResult
     * gets called when app gets result for permission request.
     * @param requestCode Code for request.
     * @param permissions Permissions that request asked.
     * @param grantResults Results for request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            {
                Global.WRITE_EXTERNAL_STORAGE = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    /**
     * Function onResume
     * gets called when activity activates.
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
        this.ButtonAdd = (FloatingActionButton) findViewById(R.id.ActivityMainAdd);
        this.toolbar = (Toolbar) findViewById(R.id.ActivityMainToolbar);
        this.table = (TableLayout) findViewById(R.id.ActivityMainTable);
        this.progressBar = (ProgressBar) findViewById(R.id.ActivityMainLoading);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ActivityMainSwipeRefreshLayout);
        this.DialogAdd = new Dialog(this);
        this.DialogAdd.setContentView(R.layout.dialog_add_rss);
        this.DialogAddUrl = (EditText) DialogAdd.findViewById(R.id.DialogAddRssUrl);
        this.DialogAddButton = (Button) DialogAdd.findViewById(R.id.DialogAddRssAdd);
        this.DialogAddAll = (CheckBox) DialogAdd.findViewById(R.id.DialogAddRssDownloadAll);
        this.DialogAddNew = (CheckBox) DialogAdd.findViewById(R.id.DialogAddRssDownloadNew);
        this.scrollView = (ScrollView) findViewById(R.id.ActivityMainScrollView);
        this.inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        swipeRefreshLayout.setOnRefreshListener(this);
        setSupportActionBar(this.toolbar);
        try
        {
            getSupportActionBar().setIcon(R.drawable.toast);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }

    }

    /**
     * Function onCreateOptionsMenu
     * what gets called when options menu is created.
     * @param menu Menu that got created.
     * @return Success of the function.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Function onOptionsItemSelected
     * is listener for options menu item selection.
     * @param item Item that got selected.
     * @return Success of the function.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ActivityMainMenuSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                //new ToJSONTask(Feeds, context).execute(OPTIONS_FILE_NAME);
                return true;
            case R.id.ActivityMainMenuFeeds:
                Intent feedsIntent = new Intent(this, FeedsActivity.class);
                startActivity(feedsIntent);
                return true;
            case R.id.ActivityMainMenuShowAll:
                this.ShowAll();
                return true;
            case R.id.ActivityMainMenuSaveLoad:
                Intent saveLoadIntent = new Intent(this, SaveLoadActivity.class);
                startActivity(saveLoadIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Function ShowAddDialog
     * for showing add dialog.
     */
    private void ShowAddDialog()
    {
        this.DialogAddUrl.setText("");
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
        boolean download = this.DialogAddNew.isChecked() || this.DialogAddAll.isChecked();
        if(this.DialogAddAll.isChecked()) lastDownload = Data.DEFAULT_DATE_TIME.minusYears(1);
        Feed feed = new Feed(url, lastDownload, download);
        new AddTask(Feeds, FeedAddListener, feed).execute(feed);
    }

    /**
     * Function Update
     * that updates all info.
     */
    private void Update()
    {
        Loading(true);
        new UpdateTask(Feeds, FeedsUpdateListener).execute(false, false);
    }

    /**
     * Function ShowAll
     * that gets all fields.
     */
    private void ShowAll()
    {
        Loading(true);
        new UpdateTask(Feeds, FeedsUpdateListener).execute(false, true);
    }


    /**
     * Function UpdateTable
     * for updating feed item table.
     */
    private void UpdateTable()
    {
        this.table.removeAllViews();
        List<Data> rows = Feeds.Rows;
        for (final Data data : rows)
        {
            this.addRow(data);
        }
        Loading(false);
    }


    /**
     * Function addRow
     * for adding row.
     * @param data Data object that will be used for values.
     * @return Result of the function.
     */
    private void addRow(final Data data)
    {

        RelativeLayout  row         = (RelativeLayout)  this.inflater.inflate(R.layout.row_main, null);
        TextView        rowTitle    = (TextView)        row.findViewById(R.id.ActivityMainRowTitle);
        Button          rowDownload = (Button)          row.findViewById(R.id.ActivityMainRowDownload);
        rowTitle.setText(data.Title);
        rowTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.Link));
                    context.startActivity(intent);
            }
            }
        );
        rowDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.Download(Data.DEFAULT_DATE_TIME.minusMonths(1)).execute();
                }
            }
        );
        table.addView(row);
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
        if(this.swipeRefreshLayout != null)
        {
            this.swipeRefreshLayout.setRefreshing(status);
        }
        loading = status;
   }

    /**
     * Function on Refresh
     * this gets called when swipeRefreshLayout is refreshed.
     * Updating table values.
     */
    @Override
    public void onRefresh() {
        this.Update();
    }
}
