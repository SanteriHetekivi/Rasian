package com.hetekivi.rasian.External;

import android.net.Uri;

import java.net.URL;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * Class Tools
 * for storing different static tools.
 */
public class Tools {
    /**
     * Function toURL
     * for converting given string to URL.
     * @param str String to convert.
     * @return URL of the string or null if failed.
     */
    public static URL toURL(String str)
    {
        URL url = null;
        try
        {
            url = new URL(str);
        }
        catch (Exception e)
        {
            url = null;
        }
        return url;
    }

    /**
     * Function toUri
     * for converting given string to Uri.
     * @param str String to convert.
     * @return Uri of the string or null if failed.
     */
    public static Uri toUri(String str)
    {
        Uri uri = null;
        try
        {
            uri = Uri.parse(str);
        }
        catch (Exception e)
        {
            uri = null;
        }
        return uri;
    }
}
