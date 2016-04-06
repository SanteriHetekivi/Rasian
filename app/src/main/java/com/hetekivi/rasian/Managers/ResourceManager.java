package com.hetekivi.rasian.Managers;

import android.content.res.Resources;
import android.util.Log;
import com.hetekivi.rasian.R;

/**
 * Created by Santeri Hetekivi on 3.4.2016.
 */

/**
 * Class StringManager
 * for managing strings.
 */
public class ResourceManager
{
    /**
     * Public static final values for class.
     */
    private static final String TAG = "ResourceManager";

    /**
     * Constructor
     * @param _resources Applications resources.
     * @param packageName Applications packageName.
     */
    public ResourceManager(Resources _resources, String packageName)
    {
        if(_resources != null && packageName != null)
        {
            this.resources = _resources;
            this.packageName = packageName;
        }
    }

    /**
     * Start strings for resources.
     */
    public static final String PACKAGE_ID = "com.hetekivi.rasian:id/";
    public static final String PACKAGE_STRING = "com.hetekivi.rasian:string/";


    /**
     * Type names.
     */
    public static final String TYPE_STRING = "string";

    /**
     * Classes member variables.
     */
    private Resources resources = null;
    private String packageName = null;


    /**
     * Function String
     * for getting string from string containing id.
     * @param idString String containing id.
     * @return String that has given id or empty string if not found.
     */
    public String String(String idString)
    {
        String str = "";
        if(Check())
        {
            int id = resources.getIdentifier(idString, TYPE_STRING, this.packageName);
            if(id != 0)
            {
                str = this.resources.getString(id);
            }
            else
            {
                Log.e(TAG, this.String(R.string.ErrorGlobalStringID));
            }
        }
        return str;
    }

    /**
     * Function String
     * for getting string from id.
     * @param id Id of string.
     * @return String that has given id or empty string if not found.
     */
    public String String(int id)
    {
        String str = "";
        if(Check())
        {
            String name = resources.getResourceName(id);
            if (name != null && name.startsWith(PACKAGE_STRING)) {
                str = resources.getString(id);
            }
            else
            {
                Log.e(TAG, this.String(R.string.ErrorGlobalStringID));
            }
        }
        return str;
    }

    /**
     * Function String
     * for making string from two ids.
     * @param id1 First string's id.
     * @param id2 Second string's id.
     * @return String that combines two strings.
     */
    public String String(int id1, int id2)
    {
        return this.String(id1)+this.String(id2);
    }

    /**
     * Function String
     * for making string from two ids and a string.
     * @param id1 First string's id.
     * @param between Second string.
     * @param id2 Third string's id.
     * @return String that combines three strings.
     */
    public String String(int id1, String between, int id2)
    {
        return this.String(id1)+between+this.String(id2);
    }
    /**
     * Function String
     * for making string from two ids.
     * @param id1 First string's id.
     * @param after Second string.
     * @return String that combines two strings.
     */
    public String String(int id1, String after)
    {
        return this.String(id1)+after;
    }

    /**
     * Function Check
     * for checking if all is set right.
     * @return Result of the check.
     */
    public boolean Check()
    {
        return this.resources != null && this.packageName != null;
    }

}
