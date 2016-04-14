package com.hetekivi.rasian.Tasks;

import android.content.Context;
import android.util.Log;
import com.hetekivi.rasian.Interfaces.JSON;
import com.hetekivi.rasian.Interfaces.Listener;
import org.json.JSONObject;

import java.io.*;

import static com.hetekivi.rasian.Data.Global.OptionsFile;

/**
 * Created by Santeri Hetekivi on 12.4.2016.
 */


/**
 * AsyncTask FromJSONTask
 * for loading data from JSON file.
 */
public class FromJSONTask extends RootTask<FileDescriptor, Void, Boolean>
{
    public static final String TAG = "FromJSONTask"; // Tag for class.

    private JSON delegate = null;

    public FromJSONTask(JSON obj)
    {
        this.delegate = obj;
    }

    public FromJSONTask(JSON obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public FromJSONTask(JSON obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for loading data from JSON file.
     * @param files First value is used as a file to read from.
     * @return Data has been loaded.
     */
    @Override
    protected Boolean doInBackground(FileDescriptor... files)
    {
        Boolean success = false;
        FileDescriptor file = (files.length > 0)?files[0]:null;
        if(this.delegate != null && file != null)
        {

            try {
                InputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                String JSON = new String(buffer, "UTF-8");
                Log.d(TAG, JSON);
                JSONObject jsonObject = new JSONObject(JSON);
                success = delegate.fromJSON(jsonObject);
            } catch (Exception e)
            {
                success = false;
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        return success;
    }



    /**
     * Post Executor
     * for telling caller what happened.
     * @param success JSON file has been made.
     */
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(delegate != null)
        {
            if(success) delegate.onFromJSONSuccess();
            else delegate.onFromJSONFailure();
        }
        this.callListener(success);
    }
}
