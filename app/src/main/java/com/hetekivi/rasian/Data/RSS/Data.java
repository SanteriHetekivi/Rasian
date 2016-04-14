package com.hetekivi.rasian.Data.RSS;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Interfaces.Downloadable;
import com.hetekivi.rasian.Interfaces.JSON;
import com.hetekivi.rasian.Interfaces.Storable;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.DownloadTask;
import com.hetekivi.rasian.Tasks.LoadTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hetekivi.rasian.Data.Global.*;

/**
 * Class Data
 * for storing and managing RSS Feeds data items.
 */
public class Data implements Storable, Downloadable, Comparable<Data>, JSON
{
    /**
     * Public static final values for class.
     */
    public static final String      DATE_PATTERN     = "EEE, dd MMM yyyy HH:mm:ss zzz";  // Pattern for parsing dates.
    public static final String      TAG              = "RSS.Data";                       // Tag for class.
    public static final DateTime    DEFAULT_DATE_TIME= new DateTime(1980, 1, 1, 1, 1, 1);// 1.1.1980 klo 01:01:01

    /**
     * Tag names for parsing.
     * Must be lowercase.
     */
    public static final String NAME_TITLE       = "title";
    public static final String NAME_TIME        = "pubdate";
    public static final String NAME_LINK        = "link";
    public static final String NAME_MEDIA       = "media";

    public static boolean IS_MEDIA_NAME(String name)
    {
        return name.contains("media") || name.contains("enclosure");
    }

    /**
     * Function PREFERENCES_START
     * for getting start of the preferences key.
     * @return Start of the preferences key.
     */
    public String PREFERENCES_START()
    {
        return TAG+"_"+Parent+"_"+Time;
    }

    /**
     * Function PREF_TITLE
     * for getting preferences key for variable title.
     * @return Preferences key for variable title.
     */
    public String PREF_TITLE()
    {
        return PREFERENCES_START()+"_Title";
    }

    /**
     * Function PREF_LINK
     * for getting preferences key for variable link.
     * @return Preferences key for variable link.
     */
    public String PREF_LINK()
    {
        return PREFERENCES_START()+"_Link";
    }

    /**
     * Function PREF_MEDIA
     * for getting preferences key for variable media.
     * @return Preferences key for variable media.
     */
    public String PREF_MEDIA()
    {
        return PREFERENCES_START()+"_Media";
    }

    /**
     * Classes member variables.
     */
    public String Title    = null; // Title of the item.
    public String Link     = null; // Main URL address.
    String Media    = null; // URL address to media.
    String Time     = null; // Item's publication time. (pubDate)

    String Parent   = null; // Parent's title.
    DateTime limitTime = null;  // Publication time limiter for downloads.

    /**
     * Function DateTime
     * for getting DateTime object
     * from publication time.
     * @return DateTime object of publication time OR DEFAULT_DATE_TIME if making failed.
     */
    public DateTime DateTime()
    {
        DateTime date = DEFAULT_DATE_TIME;
        if (this.Time != null && !this.Time.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN, Locale.US);
                date = new DateTime(format.parse(this.Time));
            } catch (UnsupportedOperationException | IllegalArgumentException | ParseException exception2) {
                date = DEFAULT_DATE_TIME;
            }
        }
        return date;
    }

    /**
     * Constructor
     * for making bare version.
     * @param parent Parent's title.
     */
    Data(String parent)
    {
        this.Parent = parent;
    }

    /**
     * Constructor
     * for LOADING data from Preferences.
     * @param parent Parent's title.
     * @param time Item's publication time. (pubDate)
     */
    Data(String parent, String time)
    {
        this.Parent = parent;
        this.Time = time;
        this.Load();
    }

    /**
     * Constructor
     * for making full Data object.
     * @param parent Parent's title.
     * @param link Main URL address.
     * @param title Title of the item.
     * @param media URL address to media.
     * @param time Item's publication time. (pubDate)
     */
    Data(String parent, String title, String link, String media, String time)
    {
        this.Parent = parent;
        this.Title  = title;
        this.Link   = link;
        this.Media  = media;
        this.Time   = time;
    }

    /**
     * Function Check
     * for checking does object have all
     * necessary data set.
     * @return Are all necessary data set.
     */
    public boolean Check()
    {
        return Parent != null && Title != null && this.DateTime() != DEFAULT_DATE_TIME;
    }

    /**
     * Function Download
     * for starting and returning DownloadTask.
     * @param limitTime Publication date limit for downloads.
     * @return Started DownloadTask.
     */
    public DownloadTask Download(DateTime limitTime)
    {
        this.limitTime = limitTime;
        return new DownloadTask(this);
    }

    /**
     * Function Download.
     * for adding item's media to download manager.
     * !!!!RUN ON BACKGROUND THREAD!!!!
     * @return Item is added to download manager or it has been downloaded before.
     */
    @Override
    public boolean Download()
    {
        DateTime dateTime = DateTime();
        Boolean success = false;
        Log.i(TAG, "Called download:"+ this.Media);
        if(Check() && this.limitTime != null && dateTime != null && dateTime.isAfter(this.limitTime) &&
                this.Media != null && Global.isExternalStorageWritable())
        {
            try {
                Uri Uri = android.net.Uri.parse(this.Media);
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = downloadManager.query(new DownloadManager.Query());
                boolean IsInDownloadManager = false;
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    if (Title.equals(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)))) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        IsInDownloadManager = (status == DownloadManager.STATUS_PENDING  ||
                                status == DownloadManager.STATUS_RUNNING);
                        break;
                    }
                }
                cursor.close();
                if(!IsInDownloadManager)
                {
                    File file = new File("" + Uri);
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + "/" + this.Parent + "/" + file.getName();
                    file = new File(path);
                    if (!file.exists()) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri);
                        request.setVisibleInDownloadsUi(true);
                        request.allowScanningByMediaScanner();
                        request.setTitle(Title);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS + "/" + Parent, file.getName());
                        long downloadID = downloadManager.enqueue(request);
                        Log.i(TAG, "Download started! id: " + String.valueOf(downloadID));

                    } else {
                        Log.e(TAG, "File " + file.getName() + " exists!");
                        if (Global.hasMessages() && Global.hasResource()) {
                            Error.Long(Resource.String(R.string.File, file.getName(), R.string.exists_END));
                        }
                    }
                }
                else
                {
                    Log.e(TAG, "File is downloading!");
                }
                success = true;
            }
            catch (Exception e)
            {
                success = false;
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        else Log.e(TAG, "Download did not pass test! Data: "+this);
        return success;
    }

    /**
     * Function onDownloadSuccess
     * This gets called when download has been done and was successful.
     */
    @Override
    public void onDownloadSuccess() {
        //TODO: Inform user.
    }

    /**
     * Function onDownloadFailure
     * This gets called when download has been done and there were errors.
     */
    @Override
    public void onDownloadFailure() {
        //TODO: Inform user.
    }


    /**
     * Function Load
     * for loading data from preferences.
     * @return Success of load.
     */
    @Override
    public boolean Load()
    {
        Boolean success = false;
        if(Global.hasPreference() && Parent != null && Time != null)
        {
            Title = Preference.Get(this.PREF_TITLE(), "");
            Link = Preference.Get(this.PREF_LINK(), "");
            Media = Preference.Get(this.PREF_MEDIA(), "");
            success = true;
        }
        return success;
    }

    /**
     * Function onLoadSuccess
     * This gets called when loading has been done and was successful.
     */
    @Override
    public void onLoadSuccess() {
        //TODO: Inform user.
    }

    /**
     * Function onLoadFailure
     * This gets called when loading has been done and there were errors.
     */
    @Override
    public void onLoadFailure() {
        //TODO: Inform user.
    }
    /**
     * Function Save
     * for saving data to preferences.
     * @return Success of save.
     */
    @Override
    public boolean Save()
    {
        Boolean success = false;
        if(Global.hasPreference() && Parent != null && Time != null)
        {
            success = Preference.Set(this.PREF_TITLE(), Title);
            success = Preference.Set(this.PREF_LINK(), Link) && success;
            success = Preference.Set(this.PREF_MEDIA(), Media) && success;
        }
        return success;
    }

    /**
     * Function onSaveSuccess
     * This gets called when saving has been done and was successful.
     */
    @Override
    public void onSaveSuccess() {
        //TODO: Inform user.
    }

    /**
     * Function onSaveFailure
     * This gets called when saving has been done and there were errors.
     */
    @Override
    public void onSaveFailure() {
        //TODO: Inform user.
    }

    /**
     * Function compareTo
     * for comparing this object to another Data object.
     * @param data Other data object.
     * @return Compare result.
     */
    @Override
    public int compareTo(Data data) {
        return this.DateTime().compareTo(data.DateTime());
    }


    /**
     * Function parseRSS
     * for parsing values from RSS name and value.
     * @param name RSS tag name.
     * @param value Value of RSS tag.
     */
    public void parseRSS(String name, String value)
    {
        if (name.contains(NAME_TITLE) && this.Title == null)    this.Title = value;
        else if (name.contains(NAME_TIME) && this.Time == null) this.Time = value;
        else if (name.contains(NAME_LINK) && this.Link == null) this.Link = value;
        else if (IS_MEDIA_NAME(name) && this.Media == null)  this.Media = value;
    }

    /**
     * Function toJSON
     * for making object to JSONObject
     * @return JSONObject that contains objects data.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(NAME_TITLE, this.Title);
            jsonObject.put(NAME_LINK, this.Link);
            jsonObject.put(NAME_TIME, this.Time);
            jsonObject.put(NAME_MEDIA, this.Media);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
            jsonObject = null;
        }
        return jsonObject;
    }

    /**
     * Function onToJSONSuccess
     * This gets called when ToJSONTask has been done and was successful.
     */
    @Override
    public void onToJSONSuccess() {

    }

    /**
     * Function onToJSONFailure
     * This gets called when ToJSONTask has been done and there were failure.
     */
    @Override
    public void onToJSONFailure() {

    }

    /**
     * Function fromJSON
     * for reading data from JSONObject to object.
     * @param jsonObject JSONObject to read from.
     * @return Success of read.
     */
    @Override
    public boolean fromJSON(JSONObject jsonObject) {
        boolean success = false;
        if(jsonObject != null) {
            try {
                this.Title = jsonObject.getString(NAME_TITLE);
                this.Link = jsonObject.getString(NAME_LINK);
                this.Time = jsonObject.getString(NAME_TIME);
                this.Media = jsonObject.getString(NAME_MEDIA);
                success = true;
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                success = false;
            }
        }
        return success;
    }

    /**
     * Function onFromJSONSuccess
     * This gets called when FromJSONTask has been done and was successful.
     */
    @Override
    public void onFromJSONSuccess() {
        new SaveTask(this).execute();
        Log.d(TAG, "FromJSONTask Success!");
    }

    /**
     * Function onFromJSONFailure
     * This gets called when FromJSONTask has been done and there were failure.
     */
    @Override
    public void onFromJSONFailure()
    {
        new LoadTask(this).execute();
        Log.e(TAG, "FromJSONTask Failed!");
    }
}