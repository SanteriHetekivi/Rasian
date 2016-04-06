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
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Data.RSS.Data;
import com.hetekivi.rasian.Data.RSS.Feed;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.SaveTask;
import org.joda.time.DateTime;

import java.util.Map;

import static com.hetekivi.rasian.Data.Global.Feeds;
import static com.hetekivi.rasian.Data.Global.Message;

public class FeedsActivity extends AppCompatActivity {

    private FloatingActionButton ButtonAdd;
    private Toolbar toolbar;

    public static Context context;

    private static ProgressBar progressBar;
    private static ScrollView scrollView;
    private static TableLayout table;

    private Dialog DialogAdd;
    private EditText DialogAddUrl;
    private CheckBox DialogAddNew;
    private CheckBox DialogAddAll;
    private Button DialogAddButton;

    private static boolean adding = false;

    private static boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);
        context = this;
        this.UIMake();

    }

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
        this.ButtonAdd = (FloatingActionButton) findViewById(R.id.ActivityFeedsButtonAdd);
        this.toolbar = (Toolbar) findViewById(R.id.ActivityFeedsToolbar);
        table = (TableLayout) findViewById(R.id.ActivityFeedsTable);
        progressBar = (ProgressBar) findViewById(R.id.ActivityFeedsLoading);
        this.DialogAdd = new Dialog(this);
        this.DialogAdd.setContentView(R.layout.dialog_add_rss);
        this.DialogAddUrl = (EditText) this.DialogAdd.findViewById(R.id.DialogRSSAddEditTextUrl);
        this.DialogAddButton = (Button) this.DialogAdd.findViewById(R.id.DialogRSSAddButton);
        scrollView = (ScrollView) findViewById(R.id.ActivityFeedsScrollView);
        this.DialogAddAll = (CheckBox) this.DialogAdd.findViewById(R.id.DialogRSSAddDownloadAll);
        this.DialogAddNew = (CheckBox) this.DialogAdd.findViewById(R.id.DialogRSSAddDownloadNew);
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
    
    private void ShowAddDialog()
    {
        this.DialogAdd.show();
    }
    
    public static void UpdateTable()
    {
        if(table != null)
        {
            /*Map<String, Feed> feeds = Feeds.Feeds();
            table.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (final Map.Entry<String, Feed> entry : feeds.entrySet())
            {
                final Feed feed = entry.getValue();
                RelativeLayout tableRow = (RelativeLayout) inflater.inflate(R.layout.row_feeds, null);
                TextView textview = (TextView) tableRow.findViewById(R.id.RowFeedsTextView);
                textview.setText(feed.Title());
                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(feed.Url()));
                        context.startActivity(intent);
                    }
                });
                Button button = (Button)tableRow.findViewById(R.id.RowFeedsButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Loading(true);
                        Feeds.removeFeed(entry.getKey());
                    }
                });
                final CheckBox download = (CheckBox) tableRow.findViewById(R.id.RowFeedsDownload);
                download.setChecked(feed.Download());
                download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                feed.DownloadOn = b;
                                new SaveTask(feed).execute();
                            }
                        });
                        t.start();
                    }
                });
                table.addView(tableRow);
            }*/
        }
        if(adding)
        {
            Message.Long(R.string.ToastAddSuccessful);
            adding = false;
        }
        Loading(false);
    }

    private void Add()
    {
        String url = this.DialogAddUrl.getText().toString();
        DateTime lastDownload = new DateTime();
        boolean download = DialogAddNew.isChecked() || DialogAddAll.isChecked();
        if(DialogAddAll.isChecked()) lastDownload = Data.DEFAULT_DATE_TIME.minusYears(1);
        this.DialogAddUrl.getText().clear();
        DialogAdd.hide();
        //Feeds.addFeed(url, lastDownload, download);
        adding = true;
        Loading(true);
    }

    public static void Loading(boolean status)
    {
        if(progressBar != null)
        {
            if(status) progressBar.setVisibility(View.VISIBLE);
            else progressBar.setVisibility(View.GONE);
        }
        if(scrollView != null)
        {
            if(status) scrollView.setVisibility(View.GONE);
            else scrollView.setVisibility(View.VISIBLE);
        }
        loading = status;
    }
}
