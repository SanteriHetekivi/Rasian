package com.hetekivi.rasian.Data.RSS;

import android.util.Log;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Interfaces.*;
import com.hetekivi.rasian.R;
import com.hetekivi.rasian.Tasks.DownloadTask;
import com.hetekivi.rasian.Tasks.LoadTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import com.hetekivi.rasian.Tasks.UpdateTask;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.hetekivi.rasian.Data.Global.*;
import static com.hetekivi.rasian.Data.RSS.Data.IS_MEDIA_NAME;
import static com.hetekivi.rasian.Data.RSS.Data.NAME_TITLE;
import static com.hetekivi.rasian.External.Tools.toURL;
import static org.xmlpull.v1.XmlPullParser.END_TAG;

/**
 * Created by Santeri Hetekivi on 26.3.2016.
 */

/**
 * Class Feed
 * for storing and managing RSS feed.
 */
public class Feed implements Storable, Downloadable, Updatable, Rowable, JSON
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
     * Constructor
     */
    Feed()
    {

    }


    /**
     * Public static final values for RSS parsing.
     */

    /**
     * Tag names for parsing.
     * Must be lowercase.
     */
    public static final String NAME_ITEM            = "item";
    public static final String NAME_TITLE           = "title";
    public static final String NAME_URL             = "url";
    public static final String NAME_DOWNLOAD_DATE   = "download_date";
    public static final String NAME_DOWNLOAD_ON     = "download_on";

    /**
     * Attribute names for parsing.
     */
    public static final String MEDIA_ATTRIBUTE  = "url"; // Name of media attribute.

    /**
     * Settings for parsing.
     */
    public static final String ENCODING = "UTF-8"; // Encoding for XML.


    /**
     * Listener for Data object's download task.
     */
    private Listener DataDownloadListener = new Listener() {
        @Override
        public void onSuccess(Object additional) {
            if(additional != null && additional instanceof DateTime)
            {
                if(DownloadDate((DateTime) additional)) saveDownloadDate();
            }
        }

        @Override
        public void onFailure(Object additional) {

        }
    };

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
    public boolean              DownloadOn      = false;                    // DownloadOn toggle.
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

    private boolean DownloadDate(DateTime dateTime)
    {
        if(dateTime != null && (this.DownloadDate == null || dateTime.isAfter(this.DownloadDate)))
        {
            this.DownloadDate = dateTime;
            return true;
        }
        else
        {
            String message = "Given dateTime ";
            if(dateTime == null) Log.e(TAG, message+"is null!");
            else Log.e(TAG, message+dateTime.toString()+" is not after DownloadDate "+this.DownloadDate.toString()+"!");
            return false;
        }
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
        boolean success = this.DownloadOn && Global.isExternalStorageWritable();
        if(!success)
        {
            Log.e(TAG, "Download not allowed! DownloadOn: "+this.DownloadOn );
        }
        return success;
    }

    /**
     * Function AllowDownloads
     * for checking if this feed allows downloads on given item and they are possible.
     * @param item Data item that we want to download.
     * @return Does this feed allow downloads on given item and are they possible.
     */
    public boolean AllowDownloads(Data item)
    {
        boolean success = this.AllowDownloads() && item != null && item.DateTime().isAfter(this.DownloadDate);
        if(!success)
        {
            String message = "Download not allowed!";
            if (item == null) Log.e(TAG, message + " Item is null!");
            else if (!item.DateTime().isAfter(this.DownloadDate)) {
                Log.e(TAG, message + " Items DateTime " + item.DateTime() +
                        " is not after DownloadDate " + this.DownloadDate.toString());
            }
            else Log.e(TAG, message);
        }
        return success;
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
     * Functions for loading members.
     */
    public boolean loadTitle()
    {
        return this.Title(Preference.Get(this.PREF_TITLE(), (String)null));
    }
    public boolean loadDownloadDate()
    {
        String downloadDate = Preference.Get(this.PREF_DOWNLOAD_DATE(), "");
        boolean success = !downloadDate.isEmpty();
        if(success) this.DownloadDate = new DateTime(downloadDate);
        return success;
    }
    public boolean loadDownloadOn()
    {
        this.DownloadOn = Preference.Get(this.PREF_DOWNLOAD(), false);
        return true;
    }
    public boolean loadData()
    {
        boolean success = true;
        List<String> _rssData = new ArrayList<>();
        _rssData.addAll(Preference.Get(this.PREF_DATA(), new HashSet<String>()));
        for(int i = 0; i < _rssData.size(); ++i)
        {
            String date = _rssData.get(i);
            if(date != null)
            {
                Data d = new Data(this.Title(), date);
                success = d.Load() && success;
                this.setData(d);
            }
        }
        return success;
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
            success = this.loadTitle();
            success = this.loadDownloadDate() && success;
            success = this.loadDownloadOn() && success;
            success = this.loadData() && success;
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
     * Functions for saving members.
     */
    public boolean saveTitle()
    {
        return Preference.Set(this.PREF_TITLE(), this.Title);
    }
    public boolean saveDownloadDate()
    {
       return Preference.Set(this.PREF_DOWNLOAD_DATE(), this.DownloadDate.toString());
    }
    public boolean saveDownloadOn()
    {
        return Preference.Set(this.PREF_DOWNLOAD(), this.DownloadOn);
    }
    public boolean saveData()
    {
        boolean success = true;
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
        if(success)
        {
            success = Preference.Set(this.PREF_DATA(), dataTimes);
        }
        return success;
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
            success = this.saveTitle();
            success = this.saveDownloadDate() && success;
            success = this.saveDownloadOn() && success;
            success = this.saveData() && success;
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
            if(task != null)
            {
                task.object = item.DateTime();
                task.listener = this.DataDownloadListener;
            }
            else Log.e(TAG, "Download task for item "+item.Title+" is null!");
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
                boolean insideItem = false;
                Data item = null;
                String name = null;
                String value = null;
                List<Data> items = new LinkedList<>();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                InputStream inputStream = url.openConnection().getInputStream();
                xpp.setInput(inputStream, ENCODING);
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(xpp.getName() != null) {
                        name = xpp.getName().toLowerCase();
                        if (eventType == XmlPullParser.START_TAG) {
                            if (name.contains(NAME_ITEM)) {
                                insideItem = true;
                                item = new Data(this.Title());
                            } else if ((value = getXMLValue(name, xpp)) != null) {
                                //Log.d(TAG, name + ": " + value);
                                if (insideItem) {
                                    item.parseRSS(name, value);
                                    if (!updateAll && item.Time != null && !item.DateTime().isAfter(this.DownloadDate)) {
                                        success = true;
                                        item = null;
                                        break;
                                    }
                                } else {
                                    if (name.contains(NAME_TITLE) && (!success || this.Title == null)) {
                                        success = this.Title(value);
                                    }
                                }
                            }
                        } else if (eventType == END_TAG && name.contains(NAME_ITEM)) {
                            if (item != null) items.add(item);
                            item = null;
                            insideItem = false;
                        }
                    }
                    eventType = xpp.next(); //move to next element
                }
                if (item != null) items.add(item);
                if(success && !items.isEmpty())
                {
                    boolean addSuccess;
                    for (Data dataItem: items)
                    {
                        addSuccess = this.setData(dataItem);
                        if(addSuccess)
                        {
                            if(dataItem.DateTime().isAfter(this.DownloadDate))
                            {
                                if (this.DownloadOn) {
                                    DownloadTask task = this.Download(dataItem);
                                    if (task != null) task.execute();
                                    else Log.e(TAG, "Download task for item " + dataItem.Title + " is null!");
                                } else {
                                    DownloadDate(dataItem.DateTime());
                                }
                            }
                        }
                        else Log.e(TAG, "Adding item "+dataItem.Title+" failed!");
                    }
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

    private String getXMLValue(String name, XmlPullParser xpp)
    {
        String value = null;
        if(name != null && IS_MEDIA_NAME(name))
        {
            try{
                value = xpp.getAttributeValue(null, MEDIA_ATTRIBUTE);
            }
            catch (Exception e) {
                Log.e(TAG, name+": "+e.getLocalizedMessage());
            }
        }
        else
        {
            try {
                value = xpp.nextText();
            } catch (Exception e) {
                Log.e(TAG, name+": "+e.getLocalizedMessage());
            }
        }
        return value;
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
            jsonObject.put(NAME_URL, this.Url);
            jsonObject.put(NAME_DOWNLOAD_DATE, this.DownloadDate.toString());
            jsonObject.put(NAME_DOWNLOAD_ON, this.DownloadOn);
            if(this.Data != null)
            {
                JSONArray items = new JSONArray();
                JSONObject item;
                List<Data> data = new LinkedList<>(this.Data.values());
                for (Data d : data)
                {
                    item = d.toJSON();
                    if(item != null) items.put(item);
                }
                jsonObject.putOpt(NAME_ITEM, items);
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
                success = true;
                this.DownloadDate = new DateTime(jsonObject.getString(NAME_DOWNLOAD_DATE));
                this.Title = jsonObject.getString(NAME_TITLE);
                this.Url = jsonObject.getString(NAME_URL);
                this.DownloadOn = jsonObject.getBoolean(NAME_DOWNLOAD_ON);
                JSONArray items = jsonObject.getJSONArray(NAME_ITEM);
                if (items != null) {
                    this.Data = new LinkedHashMap<>();
                    boolean dataSuccess;
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        if (item != null) {
                            Data data = new Data(this.Title);
                            dataSuccess = data.fromJSON(item);
                            if (dataSuccess) {
                                dataSuccess = this.setData(data);
                            }
                            success = dataSuccess && success;
                        }
                    }
                }
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
