package com.hetekivi.rasian.Tasks;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

import android.os.AsyncTask;
import com.hetekivi.rasian.Interfaces.Downloadable;
import com.hetekivi.rasian.Interfaces.Listener;

/**
 * AsyncTask
 * for downloading objects.
 */
public class DownloadTask extends AsyncTask<Void, Void, Boolean>
{
    private Downloadable delegate = null;

    public Listener listener = null;

    public Object object = null;

    public DownloadTask(Downloadable obj)
    {
        this.delegate = obj;
    }

    public DownloadTask(Downloadable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public DownloadTask(Downloadable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }


    /**
     * Background runner.
     * for downloading object media.
     * @param voids Nothing...
     * @return Data has been loaded.
     */
    @Override
    protected Boolean doInBackground(Void... voids)
    {
        boolean success = false;
        if(this.delegate != null)
        {
            success = delegate.Download();
        }
        return success;
    }

    /**
     * Post Executor
     * for telling caller what happened.
     * @param success All given downloads has been downloaded.
     */
    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if(delegate != null)
        {
            if(success) delegate.onDownloadSuccess();
            else delegate.onDownloadFailure();
            if(listener != null)
            {
                if(success)
                {
                    if(this.object != null) listener.onSuccess(this.object);
                    listener.onSuccess();
                }
                else
                {
                    if(this.object != null) listener.onFailure(this.object);
                    listener.onFailure();
                }
            }
        }
    }
}
