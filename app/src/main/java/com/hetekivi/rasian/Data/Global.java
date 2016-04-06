package com.hetekivi.rasian.Data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.hetekivi.rasian.Data.RSS.RSSCollection;
import com.hetekivi.rasian.Managers.PreferenceManager;
import com.hetekivi.rasian.Managers.ResourceManager;
import com.hetekivi.rasian.Managers.ToastManager;
import com.hetekivi.rasian.Tasks.LoadTask;
import com.hetekivi.rasian.Tasks.SaveTask;

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
        }
        return success;
    }

    /**
     * Public static final keys for preferences.
     */
    public static final String ROW_LIMIT_KEY = "ROW_LIMIT";

    /**
     * Function ROW_LIMIT
     * for getting row limit.
     * @return Limit for rows.
     */
    public static int ROW_LIMIT()
    {
        int value = 10;
        if(Preference != null)
        {
            value = Preference.Get(ROW_LIMIT_KEY, value);
        }
        return value;
    }

    /**
     * Function WRITE_EXTERNAL_STORAGE
     * for checking if writing to external storage is allowed.
     * @return Result of the test.
     */
    private static boolean WRITE_EXTERNAL_STORAGE()
    {
        boolean result = false;
        if(hasContext() && WRITE_EXTERNAL_STORAGE)
        {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            result = (permissionCheck == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    /**
     * Global classes.
     */
    public static Context context = null;                       // Context to use for non Activity classes.
    public static PreferenceManager Preference = null;          // Preferences manager for saving and loading.
    public static ToastManager Message = null;                  // Message manager for sending messages.
    public static ToastManager Error = null;                    // Error manager for sending error messages.
    public static RSSCollection Feeds = new RSSCollection();    // Collection for managing RSS feeds.
    public static ResourceManager Resource = null;

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
        return Message != null && Error != null;
    }

    /**
     * Function hasResource
     * for checking does class
     * have ResourceManager set.
     * @return Checks result.
     */
    public static boolean hasResource()
    {
        return Resource != null;
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
        }
        return result;
    }

    /**
     * Function Load
     * for loading all data.
     */
    public static void Load()
    {
        if(Preference != null)
        {
            if(Feeds != null) new LoadTask(Feeds).execute();
        }
    }

    /**
     * Function Save for saving all data.
     */
    public static void Save()
    {
        if(Preference != null)
        {
            Preference.Clear();
            Preference.Set(ROW_LIMIT_KEY, ROW_LIMIT());
            if(Feeds != null) new SaveTask(Feeds).execute();
        }

    }
}
