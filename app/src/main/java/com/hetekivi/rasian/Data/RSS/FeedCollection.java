package com.hetekivi.rasian.Data.RSS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import com.hetekivi.rasian.Activities.FeedsActivity;
import com.hetekivi.rasian.Activities.MainActivity;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Interfaces.*;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Receives.UpdateReceiver;
import com.hetekivi.rasian.Tasks.*;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

import static com.hetekivi.rasian.Data.Global.*;
import static com.hetekivi.rasian.External.Tools.DownloadDirectory;

/**
 * Created by Santeri Hetekivi on 26.3.2016.
 */

/**
 * Class RSSCollection
 * for storing and managing Feed feeds.
 */
public class FeedCollection implements Rowable, Storable, Updatable, Addable, Removable, JSON
{
    /**
     * Public static final values for class.
     */
    private static final String TAG = "RSSCollection";

    /**
     * Constructor
     */
    public FeedCollection()
    {
        this.Feeds = new LinkedHashMap<>();
    }

    public static final int    DEFAULT_DELAY_HOURS  = 24;
    public static final int    DEFAULT_ROW_LIMIT    = 10;
    public static final String DEFAULT_DOWNLOAD_DIR = DownloadDirectory();

    /**
     * Public static final keys for preferences.
     */
    public static final String PREFERENCES_START    = TAG+"_";
    public static final String PREF_NEXT_UPDATE     = PREFERENCES_START + "NextUpdate";
    public static final String PREF_FEEDS           = PREFERENCES_START + "Feeds";
    public static final String PREF_DELAY_HOURS     = PREFERENCES_START + "DelayHours";
    public static final String PREF_DOWNLOAD_DIR    = PREFERENCES_START + "DownloadDir";
    public static final String PREF_ROW_LIMIT       = PREFERENCES_START + "RowLimit";

    /**
     * Tag names for parsing.
     * Must be lowercase.
     */
    public static final String NAME_FEED            = "feed";
    public static final String NAME_NEXT_UPDATE     = "next_update";
    public static final String NAME_DELAY_HOURS     = "delay_hours";
    public static final String NAME_DOWNLOAD_DIR    = "download_dir";
    public static final String NAME_ROW_LIMIT       = "row_limit";

    /**
     * Classes member variables.
     */
    public DateTime             NextUpdate  = null;
    private Map<String, Feed>   Feeds       = new HashMap<>();
    private int                 delayHours  = DEFAULT_DELAY_HOURS;
    public File                 downloadDir = new File(DEFAULT_DOWNLOAD_DIR);
    public List<Data>           Rows        = new LinkedList<>();   // List to store rows.
    public  int                 RowLimit    = DEFAULT_ROW_LIMIT;

    /**
     * Function Feeds
     * for getting all feeds.
     * @return All added feeds.
     */
    public Map<String,Feed> Feeds()
    {
        return this.Feeds;
    }

    /**
     * Function Feed
     * for getting feed that has given url.
     * @param url Url that will be used for selecting fees.
     * @return Feed that has given url.
     */
    private Feed Feed(String url)
    {
        Feed feed = null;
        if(this.Feeds().containsKey(url))
        {
            feed = this.Feeds().get(url);
        }
        return feed;
    }

    /**
     * Function Feed
     * for setting given feed to this collection.
     * @param feed Feed to set.
     */
    public boolean Feed(Feed feed)
    {
        if(feed.Url() != null && !feed.Url().isEmpty())
        {
            this.Feeds.put(feed.Url(), feed);
            this.Rows = this.Rows();
            return true;
        }
        return false;
    }

    /**
     * Function removeFeed
     * for removing feed by it's url.
     * @param url Feed's url.
     */
    private boolean removeFeed(String url)
    {
        if(this.Feeds().containsKey(url))
        {
            this.Feeds.remove(url);
            return true;
        }
        else if(Check())
        {
            Log.e(TAG, "Removing feed with url: "+url+" failed!");
            return false;
        }
        return false;
    }

    /**
     * Function DelayHours
     * for getting alarm's repeat delay in hours.
     * @return Alarm's repeat delay in hours.
     */
    public int DelayHours()
    {
        return this.delayHours;
    }

    /**
     * Function DelayMillis
     * for getting alarm's repeat delay in milliseconds.
     * @return Alarm's repeat delay in milliseconds.
     */
    public int DelayMillis()
    {
        return this.delayHours * 1000 * 60 * 60;
    }

    /**
     * Function Rows
     * for making and returning Data rows.
     * @return List of rows as Data objects.
     */
    @Override
    public List<Data> Rows()
    {
        List<Data> rows = new LinkedList<>();
        for (Feed feed : this.Feeds.values())
        {
            rows.addAll(feed.Rows());
        }
        Collections.sort(rows, Collections.reverseOrder());
        final int limit = (rows.size() < Global.ROW_LIMIT() || Global.ROW_LIMIT() < 0 )?rows.size():Global.ROW_LIMIT();
        this.Rows = rows.subList(0, limit);
        return rows;
    }

    /**
     * Function onRowsGotten
     * is called when rows have been gotten.
     * @param rows All rows.
     */
    @Override
    public void onRowsGotten(List<Data> rows) {
        this.Rows = rows;
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
        if(Global.hasPreference())
        {
            success = true;
            Set<String> urls = Preference.Get(PREF_FEEDS, new HashSet<String>());
            boolean rssSuccess;
            for (String url : urls) {
                Feed rss = new Feed(url);
                rssSuccess = rss.Load();
                if(rssSuccess)
                {
                    rssSuccess = rss.Update(false, false);
                    if(rssSuccess) this.Feed(rss);
                }
                success = rssSuccess && success;
            }
            this.delayHours = Preference.Get(PREF_DELAY_HOURS, DEFAULT_DELAY_HOURS);
            if(Preference.Contains(PREF_NEXT_UPDATE))
            {
                this.NextUpdate = new DateTime(Preference.Get(PREF_NEXT_UPDATE, ""));
            }
            else this.setAlarm(this.delayHours);
            this.downloadDir = new File(Preference.Get(PREF_DOWNLOAD_DIR, DEFAULT_DOWNLOAD_DIR));
            this.RowLimit = Preference.Get(PREF_ROW_LIMIT, DEFAULT_ROW_LIMIT);

            if(this.NextUpdate.isBefore(new DateTime()))
            {
                this.resetAlarm();
            }
        }
        return success;
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
        if(Global.hasPreference())
        {
            success = true;
            Set<String> urls = new LinkedHashSet<>();
            List<Feed> feeds = new LinkedList<>(this.Feeds.values());
            for (Feed feed : feeds)
            {
                urls.add(feed.Url());
                success = feed.Save() && success;
            }
            success = Preference.Set(PREF_FEEDS, urls) && success;
            if(!Preference.Contains(PREF_DELAY_HOURS) || !Preference.Contains(PREF_NEXT_UPDATE)) setAlarm(delayHours);
            success = Preference.Set(PREF_DELAY_HOURS, delayHours) && success;
            if(this.NextUpdate != null) success = Preference.Set(PREF_NEXT_UPDATE, NextUpdate.toString()) && success;
            success = Preference.Set(PREF_DOWNLOAD_DIR, this.downloadDir.getPath()) && success;
            success = Preference.Set(PREF_ROW_LIMIT, this.RowLimit) && success;

        }
        return success;
    }

    /**
     * Function onSaveSuccess
     * This gets called when saving has been done and was successful.
     */
    @Override
    public void onSaveSuccess() {
        Log.d(TAG, "SaveTask Success!");
    }

    /**
     * Function onSaveFailure
     * This gets called when saving has been done and there were errors.
     */
    @Override
    public void onSaveFailure() {
        Log.e(TAG, "SaveTask Failed!");
    }


    /**
     * Function Update
     * for updating objects data.
     * @param updateAll Does update go thought all.
     * @param setAll Sets all and overwrites limits.
     * @return Success of update.
     */
    @Override
    public boolean Update(boolean updateAll, boolean setAll)
    {
        Boolean success = true;
        for (Feed feed : this.Feeds.values())
        {
            success = feed.Update(updateAll, setAll) && success;
        }
        List<Data> rows = this.Rows();
        if(setAll) this.Rows = rows;
        return success;
    }

    /**
     * Function onUpdateSuccessful
     * This gets called when update has been done and was successful.
     */
    @Override
    public void onUpdateSuccessful() {
        new SaveTask(this).execute();
    }

    /**
     * Function onUpdateFailed
     * This gets called when update has been done and there were errors.
     */
    @Override
    public void onUpdateFailed() {
    }

    /**
     * Function setAlarm
     * for setting Alarm Manager to repeat
     * by given hours.
     * @param _delayHours Repeat hours.
     */
    public void setAlarm(int _delayHours)
    {
        this.delayHours = _delayHours;
        int millis = this.DelayMillis();
        DateTime nextDate = new DateTime().plusMillis(millis);
        this.SetAlarm(nextDate, millis);
    }

    /**
     * Function SetAlarm
     * for setting Alarm Manager repeating by
     * given millis and starting with given DateTime.
     * @param nextDate Next Alarm.
     * @param repeatMillis  Repeat space.
     */
    private void SetAlarm(DateTime nextDate, long repeatMillis)
    {
        if(context != null && nextDate != null)
        {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(alarmMgr != null)
            {
                NextUpdate = null;
                Intent intent = new Intent(context, UpdateReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                alarmMgr.cancel(alarmIntent);
                if(repeatMillis > 0)
                {
                    long nextMillis = nextDate.getMillis();
                    alarmMgr.setRepeating(AlarmManager.RTC, nextMillis, repeatMillis, alarmIntent);
                    NextUpdate = nextDate;
                }

            }
        }
    }

    /**
     * Function resetAlarm
     * for resetting alarm that has been gotten out of sync.
     */
    public void resetAlarm()
    {
        DateTime nextDate = NextUpdate;
        int repeatMillis = this.DelayMillis();
        DateTime now = new DateTime();
        if(nextDate != null)
        {
            while(nextDate.isBefore(now))
            {
                nextDate = nextDate.plusMillis(repeatMillis);
            }
        }
        else
        {
            nextDate = now.plusMillis(repeatMillis);
        }
        this.SetAlarm(nextDate, repeatMillis);
    }

    /**
     * Function Add
     * for adding Feed object to collection.
     * @param objectToAdd Feed object that will be added.
     * @return Success of add.
     */
    @Override
    public boolean Add(Object objectToAdd)
    {
        boolean success = false;
        if(objectToAdd != null && objectToAdd instanceof Feed)
        {
            Feed feed = (Feed) objectToAdd;
            if(feed.Update(true, false))
            {
                success = this.Feed(feed);
            }
            if(success)
            {
                this.Rows();
            }
        }
        return success;
    }

    /**
     * Function onAddSuccess
     * This gets called when adding has been done and was successful.
     */
    @Override
    public void onAddSuccess() {
        new SaveTask(this).execute();
    }

    /**
     * Function onAddFailure
     * This gets called when adding has been done and there were errors.
     */
    @Override
    public void onAddFailure() {

    }

    /**
     * Function Remove
     * for removing data.
     * @param objectToRemove Object that will be removed.
     * @return Success of remove.
     */
    @Override
    public boolean Remove(Object objectToRemove)
    {
        boolean success = false;
        if(objectToRemove != null && objectToRemove instanceof Feed)
        {
            Feed feed = (Feed) objectToRemove;
            if(this.removeFeed(feed.Url()))
            {
                this.Rows();
                if(this.Save())
                {
                    success = true;
                }
            }
        }
        return success;
    }

    /**
     * Function onRemoveSuccess
     * This gets called when removing has been done and was successful.
     */
    @Override
    public void onRemoveSuccess() {

    }

    /**
     * Function onRemoveFailure
     * This gets called when removing has been done and there were errors.
     */
    @Override
    public void onRemoveFailure() {

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
            jsonObject.put(NAME_NEXT_UPDATE, this.NextUpdate.toString());
            jsonObject.put(NAME_DELAY_HOURS, this.delayHours);
            jsonObject.put(NAME_ROW_LIMIT, this.RowLimit);

            jsonObject.put(NAME_DOWNLOAD_DIR, this.downloadDir.getPath());
            if(this.Feeds != null)
            {
                JSONArray items = new JSONArray();
                JSONObject item;
                List<Feed> feeds = new LinkedList<>(this.Feeds.values());
                for (Feed feed : feeds)
                {
                    item = feed.toJSON();
                    if(item != null) items.put(item);
                }
                jsonObject.putOpt(NAME_FEED, items);
            }
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
        Log.d(TAG, "ToJSONTask Success!");
    }

    /**
     * Function onToJSONFailure
     * This gets called when ToJSONTask has been done and there were failure.
     */
    @Override
    public void onToJSONFailure() {
        Log.e(TAG, "ToJSONTask Failed!");
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
                success = true;


                if(jsonObject.has(NAME_DELAY_HOURS))    this.delayHours     = jsonObject.getInt(NAME_DELAY_HOURS);
                if(jsonObject.has(NAME_NEXT_UPDATE))
                {
                                                        this.NextUpdate     = new DateTime(jsonObject.getString(NAME_NEXT_UPDATE));
                                                        this.resetAlarm();
                }
                if(jsonObject.has(NAME_DOWNLOAD_DIR))   this.downloadDir    = new File(jsonObject.getString(NAME_DOWNLOAD_DIR));
                if(jsonObject.has(NAME_ROW_LIMIT))      this.RowLimit       = jsonObject.getInt(NAME_ROW_LIMIT);
                if(jsonObject.has(NAME_FEED))
                {
                    JSONArray items = jsonObject.getJSONArray(NAME_FEED);
                    if (items != null) {
                        this.Feeds = new LinkedHashMap<>();
                        boolean feedSuccess;
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            if (item != null) {
                                Feed feed = new Feed();
                                feedSuccess = feed.fromJSON(item);
                                if (feedSuccess) {
                                    feedSuccess = this.Feed(feed);
                                }
                                success = feedSuccess && success;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                success = false;
            }
        }
        if(success) this.Rows();
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
    public void onFromJSONFailure() {
        new LoadTask(this).execute();
        Log.e(TAG, "FromJSONTask Failed!");
    }
}
