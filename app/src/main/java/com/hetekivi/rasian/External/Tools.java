package com.hetekivi.rasian.External;

import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
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

    public static Intent PathToOpenFileIntent(String path)
    {
        Intent intent = null;
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        if(uri != null)
        {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String extension = path.substring(path.lastIndexOf("."));
            String type = mime.getMimeTypeFromExtension(extension);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
        }
        return intent;
    }



}
