package com.hetekivi.rasian.Tasks;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

import android.os.AsyncTask;
import com.hetekivi.rasian.Interfaces.Addable;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.Removable;

/**
 * AsyncTask
 * for loading data from preferences.
 */
public class RemoveTask extends RootTask<Object, Void, Boolean>
{

    private Removable delegate = null;

    public RemoveTask(Removable obj)
    {
        this.delegate = obj;
    }

    public RemoveTask(Removable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public RemoveTask(Removable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for saving data to preferences.
     * @param objectsToRemove Objects that will be removed.
     * @return Data has been loaded.
     */
    @Override
    protected Boolean doInBackground(Object... objectsToRemove)
    {
        boolean success = false;
        if(this.delegate != null && objectsToRemove != null)
        {
            success = true;
            for (Object objectToRemove: objectsToRemove)
            {
                success = delegate.Remove(objectToRemove) && success;
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
            if(success) delegate.onRemoveSuccess();
            else delegate.onRemoveFailure();
        }
        this.callListener(success);
    }
}
