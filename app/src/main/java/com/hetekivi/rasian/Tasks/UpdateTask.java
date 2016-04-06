package com.hetekivi.rasian.Tasks;

import android.os.AsyncTask;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.Storable;
import com.hetekivi.rasian.Interfaces.Updatable;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * for updating data.
 */
public class UpdateTask extends AsyncTask<Boolean, Void, Boolean>
{

    private Updatable delegate = null;

    public Listener listener = null;


    public Object object = null;

    public UpdateTask(Updatable obj)
    {
        this.delegate = obj;
    }

    public UpdateTask(Updatable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public UpdateTask(Updatable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for updating data.
     * @param options Options for execute.
     * @return Data has been loaded.
     */
    @Override
    protected Boolean doInBackground(Boolean... options)
    {
        boolean success = false;
        boolean updateAll = (options.length > 0)?options[0]:false;
        boolean setAll = (options.length > 1)?options[1]:false;
        if(this.delegate != null)
        {
            success = delegate.Update(updateAll, setAll);
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
            if(success) delegate.onUpdateSuccessful();
            else delegate.onUpdateFailed();
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