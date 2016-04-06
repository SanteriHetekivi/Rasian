package com.hetekivi.rasian.Data.RSS;

import android.util.Log;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Interfaces.*;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.DownloadTask;
import org.joda.time.DateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.hetekivi.rasian.Data.Global.*;
import static com.hetekivi.rasian.External.Tools.toURL;

/**
 * Created by Santeri Hetekivi on 26.3.2016.
 */

/**
 * Class Feed
 * for storing and managing RSS feed.
 */
public class Feed implements Storable, Downloadable, Updatable, Rowable
{
    /**
     * Public static final values for class.
     */
    public static final String TAG      = "RSS.Feed";   // Tag for class.

    /**
     * Constructor
     * for setting values for make from url.
     * @param url Url address of the feed.
     * @param downloadedDate DateTime for last allowed download.
     * @param download Toggle for allowing downloads.
     */
    public Feed(String url, DateTime downloadedDate, boolean download)
    {
        this.Url            = url;
        this.DownloadDate   = downloadedDate;
        this.DownloadOn = download;
    }

    /**
     * Constructor
     * for loading from parameters.
     * @param url Url address of the feed.
     */
    Feed(String url)
    {
        this.Url = url;
    }

    /**
     * Public static final values for RSS parsing.
     */

    /**
     * Tag names for parsing.
     * Must be lowercase.
     */
    public static final String NAME_ITEM        = "item";
    public static final String NAME_TITLE       = "title";
    public static final String NAME_TIME        = "pubdate";
    public static final String NAME_LINK        = "link";
    public static final List<String> NAMES_MEDIA= new ArrayList<>(Arrays.asList("media:content", "enclosure"));

    /**
     * Attribute names for parsing.
     */
    public static final String MEDIA_ATTRIBUTE  = "url"; // Name of media attribute.

    /**
     * Settings for parsing.
     */
    public static final String ENCODING = "UTF-8"; // Encoding for XML.

    /**
     * Function PREFERENCES_START
     * for getting start of the preferences key.
     * @return Start of the preferences key.
     */
    public String PREFERENCES_START()
    {
        return TAG+"_"+Url;
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
     * Function PREF_DOWNLOAD_DATE
     * for getting preferences key for variable download date.
     * @return Preferences key for variable download date.
     */
    public String PREF_DOWNLOAD_DATE()
    {
        return PREFERENCES_START()+"_DownloadDate";
    }

    /**
     * Function PREF_DOWNLOAD
     * for getting preferences key for variable download.
     * @return Preferences key for variable download.
     */
    public String PREF_DOWNLOAD()
    {
        return PREFERENCES_START()+"_Download";
    }

    /**
     * Function PREF_DATA
     * for getting preferences key for variable data.
     * @return Preferences key for variable data.
     */
    public String PREF_DATA()
    {
        return PREFERENCES_START()+"_Data";
    }


    /**
     * Classes member variables.
     */
    private String              Title           = null;                     // Feed's title.
    private String              Url             = null;                     // Feed's url address.
    private DateTime            DownloadDate    = null;                     // Limiter for downloads.
    public boolean             DownloadOn      = false;                    // DownloadOn toggle.
    private Map<String, Data>   Data            = new LinkedHashMap<>();    // Feeds items. (Keys is pubDate)

    /**
     * Function Title
     * for getting feed's title.
     * @return Feed's title.
     */
    public String Title()
    {
        return this.Title;
    }

    /**
     * Function Url
     * for getting feed's url address.
     * @return Feeds url address.
     */
    public String Url()
    {
        return this.Url;
    }

    /**
     * Function Title
     * for setting feed's tile.
     * @param title Feed's title to set.
     * @return Success of operation.
     */
    private boolean Title(String title)
    {
        if(title != null && !title.isEmpty())
        {
            this.Title = title;
            return true;
        }
        return false;
    }

    /**
     * Function setData
     * for adding and setting data to feed.
     * @param item Data to add.
     * @return Success of operation.
     */
    private boolean setData(Data item)
    {
        if(item != null && item.Check())
        {
            this.Data.put(item.Time, item);
            return true;
        }
        return false;
    }


    /**
     * Function AllowDownloads
     * for checking if this feed allows downloads and they are possible.
     * @return Does this feed allow downloads and are they possible.
     */
    public boolean AllowDownloads()
    {
        return this.DownloadOn && Global.isExternalStorageWritable();
    }

    /**
     * Function AllowDownloads
     * for checking if this feed allows downloads on given item and they are possible.
     * @param item Data item that we want to download.
     * @return Does this feed allow downloads on given item and are they possible.
     */
    public boolean AllowDownloads(Data item)
    {
        return this.AllowDownloads() && item != null && item.DateTime().isAfter(this.DownloadDate);
    }

    /**
     * Function Rows
     * for getting object data as a list.
     * @return Data values as a list.
     */
    @Override
    public List<Data> Rows()
    {
        if(this.Data == null || this.Data.isEmpty())
        {
            return new LinkedList<>();
        }
        else
        {
            return new LinkedList<>(this.Data.values());
        }
    }

    /**
     * Function onRowsGotten
     * is called when rows have been gotten.
     * @param rows All rows.
     */
    @Override
    public void onRowsGotten(List<Data> rows) {

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
        if(Global.hasPreference() && this.Url != null)
        {
            this.Title(Preference.Get(this.PREF_TITLE(), (String)null));
            this.DownloadDate = new DateTime(Preference.Get(this.PREF_DOWNLOAD_DATE(), ""));
            this.DownloadOn = Preference.Get(this.PREF_DOWNLOAD(), false);
            List<String> _rssData = new ArrayList<>();
            _rssData.addAll(Preference.Get(this.PREF_DATA(), new HashSet<String>()));
            for(int i = 0; i < _rssData.size(); ++i)
            {
                String date = _rssData.get(i);
                if(date != null)
                {
                    Data d = new Data(this.Title(), date);
                    d.Load();
                    this.setData(d);
                }
            }
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
     * @return Was saving successful.
     */
    @Override
    public boolean Save()
    {
        Boolean success = false;
        if(Global.hasPreference())
        {
            Preference.Set(this.PREF_TITLE(), this.Title);
            Preference.Set(this.PREF_DOWNLOAD_DATE(), this.DownloadDate.toString());
            Preference.Set(this.PREF_DOWNLOAD(), this.DownloadOn);
            Set<String> dataTimes = new HashSet<>();
            if(this.Data != null)
            {
                List<Data> data = new LinkedList<>(this.Data.values());
                for (Data d : data)
                {
                    dataTimes.add(d.Time);
                    success = d.Save() && success;
                }
            }
            Preference.Set(this.PREF_DATA(), dataTimes);
            success = true;
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
     * Function Download
     * for starting to download given item.
     * @param item Item to download.
     * @return Started task or null if failed.
     */
    public DownloadTask Download(Data item)
    {
        DownloadTask task = null;
        if(this.AllowDownloads(item))
        {
            item.limitTime = this.DownloadDate;
            task = item.Download(this.DownloadDate);
        }
        return task;
    }


    /**
     * Function Download
     * for downloading all data.
     * @return Success of downloads.
     */
    @Override
    public boolean Download() {

        boolean success = false;
        if(this.Data != null && this.AllowDownloads())
        {
            success = true;
            for (Map.Entry<String, Data> entry: this.Data.entrySet())
            {
                Data item = entry.getValue();
                if(this.AllowDownloads(item))
                {
                    item.limitTime = this.DownloadDate;
                    success = item.Download() && success;
                }
            }
        }
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
     * Function Update
     * for updating Rss feed's data.
     * @param updateAll Does update go thought all.
     * @param setAll Sets all and overwrites limits.
     * @return Success of update.
     */
    @Override
    public boolean Update(boolean updateAll, boolean setAll)
    {
        boolean success = false;
        URL url = toURL(this.Url);
        if(url != null)
        {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                InputStream inputStream = url.openConnection().getInputStream();
                xpp.setInput(inputStream, ENCODING);
                boolean insideItem = false;
                Data item = null;
                int eventType = xpp.getEventType();
                String name, value;
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if (eventType == XmlPullParser.START_TAG) {
                        name = xpp.getName().toLowerCase();
                        try {
                            value = xpp.nextText();
                        } catch (Exception e) {
                            value = null;
                        }
                        if (name.equalsIgnoreCase(NAME_ITEM)) {
                            insideItem = true;
                            item = new Data(this.Title());
                        }
                        else if (value != null)
                        {
                            if (insideItem)
                            {
                                if (name.equals(NAME_TITLE)) item.Title = value;
                                else if (name.equals(NAME_TIME)) {
                                    item.Time = value;
                                    if (!updateAll && item.DateTime().isBefore(this.DownloadDate)) break;
                                } else if (name.equals(NAME_LINK)) item.Link = value;
                                else if (NAMES_MEDIA.contains(name)) {
                                    item.Media = xpp.getAttributeValue(null, MEDIA_ATTRIBUTE);
                                }
                            } else if (!success && name.equalsIgnoreCase(NAME_TITLE)) {
                                success = this.Title(value);
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase(NAME_ITEM)) {
                        insideItem = false;
                        if (item != null) this.setData(item);
                        item = null;
                    }
                    eventType = xpp.next(); //move to next element
                }
                if (item != null)
                {
                    this.setData(item);
                }
            }
            catch (Exception e)
            {
                success = false;
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        return success;
    }


    /**
     * Function onUpdateSuccessful
     * This gets called when update has been done and was successful.
     * When update is successful adds itself to feeds.
     */
    @Override
    public void onUpdateSuccessful() {
        if(Feeds != null)
        {
            Feeds.Feed(this);
        }
    }

    /**
     * Function onUpdateFailed
     * This gets called when update has been done and there were errors.
     * When update failed inform user.
     */
    @Override
    public void onUpdateFailed() {
        if(Check())
        {
            Error.Long(Resource.String(R.string.Feed_Start, this.Title(), R.string.UpdateFailed_End));
        }
    }
}
