package com.hetekivi.rasian.Data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.hetekivi.rasian.Data.RSS.FeedCollection;
import com.hetekivi.rasian.Managers.PreferenceManager;
import com.hetekivi.rasian.Managers.ResourceManager;
import com.hetekivi.rasian.Managers.ToastManager;
import com.hetekivi.rasian.Tasks.LoadTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

/**
 * Created by Santeri Hetekivi on 26.3.2016.
 */

/**
 * Class Global
 * for managing and
 * storing static global
 * variables.
 */
public class Global
{
    /**
     * Public static final values for class.
     */
    private static final String TAG = "Global";

    /**
     * Initializer Init
     * for setting all values by given context.
     * @param _context Context to give all non Activity classes.
     * @return Success of initializing.
     */
    public static boolean Init(Context _context)
    {
        boolean success = false;
        if(_context != null) {
            context = _context;
            Preference = new PreferenceManager(context);
            Message = new ToastManager(context, ToastManager.Type.MESSAGE);
            Error = new ToastManager(context, ToastManager.Type.ERROR);
            Resource = new ResourceManager(context.getResources(), context.getPackageName());
            success = true;
            JodaTimeAndroid.init(context);
        }
        else Log.e(TAG, "Context given to Init is null!");
        return success;
    }


    public static File DownloadDir()
    {
        if(Feeds != null && Feeds.downloadDir != null && Feeds.downloadDir.exists())
        {
            return Feeds.downloadDir;
        }
        else return new File(FeedCollection.DEFAULT_DOWNLOAD_DIR);
    }

    public static String DownloadDirString()
    {
        if(Feeds != null && Feeds.downloadDir != null && Feeds.downloadDir.exists())
        {
            return Feeds.downloadDir.getPath();
        }
        else return FeedCollection.DEFAULT_DOWNLOAD_DIR;
    }

    /**
     * Function ROW_LIMIT
     * for getting row limit.
     * @return Limit for rows.
     */
    public static int ROW_LIMIT()
    {
        if(Feeds != null) return Feeds.RowLimit;
        else return FeedCollection.DEFAULT_ROW_LIMIT;
    }

    /**
     * Function WRITE_EXTERNAL_STORAGE
     * for checking if writing to external storage is allowed.
     * @return Result of the test.
     */
    public static boolean WRITE_EXTERNAL_STORAGE()
    {
        boolean result = false;
        if(hasContext() && WRITE_EXTERNAL_STORAGE)
        {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            result = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            if(!result) Log.e(TAG, "No permission for writing to external storage!");

        }
        else Log.e(TAG, "No context or WRITE_EXTERNAL_STORAGE false! WRITE_EXTERNAL_STORAGE: "+WRITE_EXTERNAL_STORAGE);
        return result;
    }

    /**
     * Global classes.
     */
    public static Context context = null;                       // Context to use for non Activity classes.
    public static PreferenceManager Preference = null;          // Preferences manager for saving and loading.
    public static ToastManager Message = null;                  // Message manager for sending messages.
    public static ToastManager Error = null;                    // Error manager for sending error messages.
    public static FeedCollection Feeds = new FeedCollection();    // Collection for managing RSS feeds.
    public static ResourceManager Resource = null;

    /**
     * Global Strings
     */
    public static final String OPTIONS_FILE_NAME = "options.json";

    /**
     * Public static flags.
     */
    public static boolean WRITE_EXTERNAL_STORAGE = false;   // Is writing to external storage allowed.

    /**
     * Function Check
     * for checking if all classes are set.
     * @return Are all classes set.
     */
    public static boolean Check()
    {
        return hasMessages() && hasContext() && hasPreference() && hasResource();
    }

    /**
     * Function hasContext
     * for checking does class
     * have application context set.
     * @return Checks result.
     */
    public static boolean hasContext()
    {
        boolean success = (context != null);
        if(!success) Log.e(TAG, "Global doesn't have context!");
        return success;
    }

    /**
     * Function hasPreference
     * for checking does class
     * have PreferenceManager set.
     * @return Checks result.
     */
    public static boolean hasPreference()
    {
        boolean success = (Preference != null && Preference.Check());
        if(!success) Log.e(TAG, "Global doesn't have preference!");
        return success;
    }
    /**
     * Function hasMessages
     * for checking does class
     * have ToastManagers set.
     * @return Checks result.
     */
    public static boolean hasMessages()
    {
        boolean success = (Message != null && Error != null);
        if(!success) Log.e(TAG, "Global doesn't have toast messages!");
        return success;
    }

    /**
     * Function hasResource
     * for checking does class
     * have ResourceManager set.
     * @return Checks result.
     */
    public static boolean hasResource()
    {
        boolean success = (Resource != null);
        if(!success) Log.e(TAG, "Global doesn't have Resource!");
        return success;
    }

    /**
     * Function isExternalStorageWritable
     * for checking is the devices external storage writable.
     * @return Checks result.
     */
    public static boolean isExternalStorageWritable() {
        boolean result = false;
        if(WRITE_EXTERNAL_STORAGE())
        {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                result = true;
            }
            else Log.e(TAG, "Media is not mounted!");
        }
        return result;
    }

    /**
     * Function isDirectoryWritable
     * for checking if given public directory is writable.
     * @param title Title of public directory.
     * @return Checks result.
     */
    public static boolean isDirectoryWritable(String title) {
        boolean result = false;
        if(isExternalStorageWritable())
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)+"/"+title);
            result = file.mkdirs();
            if(!result) Log.e(TAG, "Making directories failed! Path: "+file.getPath());
        }
        return result;
    }

    /**
     * Function Load
     * for loading all data.
     */
    public static void Load()
    {
        if(hasPreference())
        {
            if(Feeds != null) new LoadTask(Feeds).execute();
        }
        else Log.e(TAG, "Trying to load without Preferences!");
    }

    /**
     * Function Save for saving all data.
     */
    public static void Save()
    {
        if(hasPreference())
        {
            Preference.Clear();
            if(Feeds != null) new SaveTask(Feeds).execute();
        }
        else Log.e(TAG, "Trying to save without Preferences!");
    }

    /**
     * Function NFCSupported
     * checks if given Context can use NTC and Android Beam
     * @param con Context to use for check.
     * @return Result for check.
     */
    public static boolean NFCSupported(Context con)
    {
        boolean success = false;
        if(con != null)
        {
            success = con.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC) &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
        }
        return success;
    }

    public static File DataDir()
    {
        File dir = new File(Environment.getExternalStorageDirectory(), "Rasian/");
        dir.mkdirs();
        return dir;
    }

    public static File OptionsFile()
    {
        return new File(DataDir(), OPTIONS_FILE_NAME);
    }
}
