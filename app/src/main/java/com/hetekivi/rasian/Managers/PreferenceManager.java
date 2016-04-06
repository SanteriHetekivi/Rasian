package com.hetekivi.rasian.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Santeri Hetekivi on 31.3.2016.
 */

/**
 * Class PreferenceManager
 * for managing saving and loading.
 */
public class PreferenceManager
{
    /**
     * Public static final values for class.
     */
    private static final String TAG = "PreferenceManager";

    /**
     * Constructor.
     * @param context Applications context.
     */
    public PreferenceManager(Context context)
    {
        this.preferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        if (this.preferences != null) this.editor = this.preferences.edit();
    }

    private final String PREFERENCE_FILE_KEY = "com.hetekivi.rasian.preferences";   // Key for preferences file.

    /**
     * Classes member variables.
     */
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;

    /**
     * Function Check
     * for checking if all is set right.
     * @return Result of check.
     */
    public boolean Check()
    {
        return (this.preferences != null && this.editor != null);
    }

    /**
     * Function Check
     * for checking if all is set right
     * and if preferences has given key.
     * @param key Key to check.
     * @return Result of check.
     */
    private boolean Check(String key)
    {
        return this.Check() && this.preferences.contains(key);
    }

    /**
     * Function Contains
     * for checking if all is set right
     * and if preferences has given key.
     * @param key Key to check.
     * @return Result of check.
     */
    public boolean Contains(String key)
    {
        return this.Check(key);
    }

    /**
     * Function Set
     * for setting given string to given key.
     * @param key Key for value.
     * @param value String value to set.
     * @return Success of set.
     */
    public boolean Set(String key, String value)
    {
        boolean success = false;
        if(this.Check())
        {
           success = this.editor.putString(key, value).commit();
        }
        return success;
    }

    /**
     * Function Set
     * for setting given int to given key.
     * @param key Key for value.
     * @param value Int value to set.
     * @return Success of set.
     */
    public boolean Set(String key, int value)
    {
        boolean success = false;
        if(this.Check())
        {
           success = this.editor.putInt(key, value).commit();
        }
        return success;
    }

    /**
     * Function Set
     * for setting given Boolean to given key.
     * @param key Key for value.
     * @param value Boolean value to set.
     * @return Success of set.
     */
    public boolean Set(String key, Boolean value)
    {
        boolean success = false;
        if(this.Check())
        {
            success = this.editor.putBoolean(key, value).commit();
        }
        return success;
    }

    /**
     * Function Set
     * for setting given Set<String> to given key.
     * @param key Key for value.
     * @param value Set<String> value to set.
     * @return Success of set.
     */
    public boolean Set(String key, Set<String> value)
    {
        boolean success = false;
        if(this.Check())
        {
           success = this.editor.putStringSet(key, value).commit();
        }
        return success;
    }

    /**
     * Function Set
     * for setting given float to given key.
     * @param key Key for value.
     * @param value float value to set.
     * @return Success of set.
     */
    public boolean Set(String key, float value)
    {
        boolean success = false;
        if(this.Check())
        {
           success = this.editor.putFloat(key, value).commit();
        }
        return success;
    }

    /**
     * Function Set
     * for setting given float to given key.
     * @param key Key for value.
     * @param value long value to set.
     * @return Success of set.
     */
    public boolean Set(String key, long value)
    {
        boolean success = false;
        if(this.Check())
        {
           success = this.editor.putLong(key, value).commit();
        }
        return success;
    }

    /**
     * Function Get
     * for getting int with given key.
     * @param key Key for value.
     * @param def Default value if keys is not set.
     * @return Value for key or default if key is not set.
     */
    public int Get(String key, int def)
    {
        int value = def;
        if(this.Check(key)) value = this.preferences.getInt(key, def);
        return value;
    }

    /**
     * Function Get
     * for getting boolean with given key.
     * @param key Key for value.
     * @param def Default value if keys is not set.
     * @return Value for key or default if key is not set.
     */
    public boolean Get(String key, boolean def)
    {
        boolean value = def;
        if(this.Check(key)) value = this.preferences.getBoolean(key, def);
        return value;
    }

    /**
     * Function Get
     * for getting Set<String> with given key.
     * @param key Key for value.
     * @param def Default value if keys is not set.
     * @return Value for key or default if key is not set.
     */
    public Set<String> Get(String key, Set<String> def)
    {
        Set<String> value = def;
        if(this.Check(key)) value = this.preferences.getStringSet(key, def);
        return value;
    }

    /**
     * Function Get
     * for getting String with given key.
     * @param key Key for value.
     * @param def Default value if keys is not set.
     * @return Value for key or default if key is not set.
     */
    public String Get(String key, String def)
    {
        String value = def;
        if(this.Check(key)) value = this.preferences.getString(key, def);
        return value;
    }

    /**
     * Function Get
     * for getting float with given key.
     * @param key Key for value.
     * @param def Default value if keys is not set.
     * @return Value for key or default if key is not set.
     */
    public float Get(String key, float def)
    {
        float value = def;
        if(this.Check(key)) value = this.preferences.getFloat(key, def);
        return value;
    }

    /**
     * Function Get
     * for getting long with given key.
     * @param key Key for value.
     * @param def Default value if keys is not set.
     * @return Value for key or default if key is not set.
     */
    public long Get(String key, long def)
    {
        long value = def;
        if(this.Check(key)) value = this.preferences.getLong(key, def);
        return value;
    }

    /**
     * Function Clear
     * for clearing whole preferences.
     * @return Success of clear.
     */
    public boolean Clear()
    {
        boolean success = false;
        if(this.Check())
        {
            success = this.preferences.edit().clear().commit();
        }
        return success;
    }
}

