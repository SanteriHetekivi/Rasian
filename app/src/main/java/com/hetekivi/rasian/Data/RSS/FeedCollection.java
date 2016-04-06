package com.hetekivi.rasian.Data.RSS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.hetekivi.rasian.Activities.FeedsActivity;
import com.hetekivi.rasian.Activities.MainActivity;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Interfaces.*;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Receives.UpdateReceiver;
import com.hetekivi.rasian.Tasks.AddTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import com.hetekivi.rasian.Tasks.UpdateTask;
import org.joda.time.DateTime;

import java.util.*;

import static com.hetekivi.rasian.Data.Global.*;

/**
 * Created by Santeri Hetekivi on 26.3.2016.
 */

/**
 * Class RSSCollection
 * for storing and managing Feed feeds.
 */
public class FeedCollection implements Rowable, Storable, Updatable, Addable, Removable
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
        this.Feeds = new LinkedHashMap<String, Feed>();
    }

    /**
     * Public static final keys for preferences.
     */
    public static final String PREFERENCES_START = TAG+"_";
    public static final String PREF_NEXT_UPDATE = PREFERENCES_START + "NextUpdate";
    public static final String PREF_FEEDS       = PREFERENCES_START + "Feeds";
    public static final String PREF_DELAY_HOURS = PREFERENCES_START + "DelayHours";

    /**
     * Classes member variables.
     */
    public DateTime             NextUpdate  = null;
    private Map<String, Feed>   Feeds       = new HashMap<>();
    private int                 delayHours  = 24;

    public List<Data>           Rows        = new LinkedList<>();   // List to store rows.

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
        else if(Global.Check())
        {
            Log.e(TAG, Resource.String(R.string.ErrorRSSCollectionNoKey, url));
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
            Log.e(TAG, Resource.String(R.string.Feed_Start, R.string.IsAlreadyRemoved_End));
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
            this.delayHours = Preference.Get(PREF_DELAY_HOURS, 24);
            if(Preference.Contains(PREF_NEXT_UPDATE))
            {
                this.NextUpdate = new DateTime(Preference.Get(PREF_NEXT_UPDATE, ""));
            }
            else
            {
                this.setAlarm(this.delayHours);
            }

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
        }
        return success;
    }

    /**
     * Function onSaveSuccess
     * This gets called when saving has been done and was successful.
     */
    @Override
    public void onSaveSuccess() {

    }

    /**
     * Function onSaveFailure
     * This gets called when saving has been done and there were errors.
     */
    @Override
    public void onSaveFailure() {

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
        List<Data> rows = new LinkedList<>();
        for (Feed feed : this.Feeds.values())
        {
            success = feed.Update(updateAll, setAll) && success;
            rows.addAll(feed.Rows());
        }
        Collections.sort(rows, Collections.reverseOrder());
        final int limit = (rows.size() < Global.ROW_LIMIT() || Global.ROW_LIMIT() < 0 || setAll)?rows.size():Global.ROW_LIMIT();
        this.Rows = rows.subList(0, limit);
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
        int millis = this.DelayMillis()/60;
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
}
