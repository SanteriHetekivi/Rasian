package com.hetekivi.rasian.Tasks;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

import android.os.AsyncTask;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.Storable;

/**
 * AsyncTask
 * for loading data from preferences.
 */
public class LoadTask extends RootTask<Void, Void, Boolean>
{
    private Storable delegate = null;

    public LoadTask(Storable obj)
    {
        this.delegate = obj;
    }

    public LoadTask(Storable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public LoadTask(Storable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for loading data from preferences.
     * @param voids Nothing...
     * @return Data has been loaded.
     */
    @Override
    protected Boolean doInBackground(Void... voids)
    {
        boolean success = false;
        if(this.delegate != null)
        {
            success = delegate.Load();
        }
        return success;
    }

    /**
     * Post Executor
     * for telling caller what happened.
     * @param success All given classes has been loaded.
     */
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(delegate != null)
        {
            if(success) delegate.onLoadSuccess();
            else delegate.onLoadFailure();
        }
        this.callListener(success);
    }
}
