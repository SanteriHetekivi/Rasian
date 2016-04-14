package com.hetekivi.rasian.Tasks;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.Storable;

/**
 * AsyncTask
 * for loading data from preferences.
 */
public class SaveTask extends RootTask<Void, Void, Boolean>
{

    private Storable delegate = null;

    public SaveTask(Storable obj)
    {
        this.delegate = obj;
    }

    public SaveTask(Storable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public SaveTask(Storable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for saving data to preferences.
     * @param voids Nothing...
     * @return Data has been saved.
     */
    @Override
    protected Boolean doInBackground(Void... voids)
    {
        boolean success = false;
        if(this.delegate != null)
        {
            success = delegate.Save();
        }
        return success;
    }

    /**
     * Post Executor
     * for telling caller what happened.
     * @param success All given classes has been saved.
     */
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(delegate != null)
        {
            if(success) delegate.onSaveSuccess();
            else delegate.onSaveFailure();
        }
        this.callListener(success);
    }
}
