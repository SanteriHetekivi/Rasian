package com.hetekivi.rasian.Tasks;

import android.content.Context;
import android.util.Log;
import com.hetekivi.rasian.Interfaces.JSON;
import com.hetekivi.rasian.Interfaces.Listener;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static com.hetekivi.rasian.Data.Global.OptionsFile;

/**
 * Created by Santeri Hetekivi on 12.4.2016.
 */


/**
 * AsyncTask ToJSONTask
 * for saving data to JSON file.
 */
public class ToJSONTask extends RootTask<File, Void, Boolean>
{
    public static final String TAG = "ToJSONTask"; // Tag for class.

    private JSON delegate = null;

    private Context context = null;

    public ToJSONTask(JSON obj)
    {
        this.delegate = obj;
    }

    public ToJSONTask(JSON obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public ToJSONTask(JSON obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for saving data to JSON file.
     * @param files First value is used as input file.
     * @return Data has been saved.
     */
    @Override
    protected Boolean doInBackground(File... files)
    {
        Boolean success = false;
        File file = (files.length > 0)?files[0]:null;
        Log.d(TAG, "Starting to make JSON!");

        if(this.delegate != null && file != null)
        {
            JSONObject jsonObject = delegate.toJSON();
            if(jsonObject != null) {
                String JSON = jsonObject.toString();
                Log.d(TAG, JSON);
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(file);
                    outputStream.write(JSON.getBytes());
                    Log.d(TAG, "Writing JSON to file "+file.getPath()+"!");

                    outputStream.close();
                    success = true;
                } catch (Exception e) {
                    success = false;
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }else Log.e(TAG, "jsonObject was null!");
        }
        else Log.e(TAG, "Filename not set!");
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
            if(success) delegate.onToJSONSuccess();
            else delegate.onToJSONFailure();
        }
        this.callListener(success);
    }
}
