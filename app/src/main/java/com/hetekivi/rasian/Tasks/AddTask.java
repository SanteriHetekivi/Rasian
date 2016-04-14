package com.hetekivi.rasian.Tasks;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

import android.os.AsyncTask;
import com.hetekivi.rasian.Interfaces.Addable;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.Storable;

/**
 * AsyncTask
 * for loading data from preferences.
 */
public class AddTask extends RootTask<Object, Void, Boolean>
{

    private Addable delegate = null;

    public AddTask(Addable obj)
    {
        this.delegate = obj;
    }

    public AddTask(Addable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public AddTask(Addable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for saving data to preferences.
     * @param objectsToAdd Objects to add.
     * @return Data has been loaded.
     */
    @Override
    protected Boolean doInBackground(Object... objectsToAdd)
    {
        boolean success = false;
        if(this.delegate != null && objectsToAdd != null)
        {
            success = true;
            for (Object objectToAdd: objectsToAdd)
            {
                success = delegate.Add(objectToAdd) && success;
            }
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
            if(success) delegate.onAddSuccess();
            else delegate.onAddFailure();
        }
        this.callListener(success);
    }
}
