package com.hetekivi.rasian.Tasks;

import android.os.AsyncTask;
import com.hetekivi.rasian.Interfaces.Listener;

/**
 * Created by Santeri Hetekivi on 8.4.2016.
 */
public abstract class RootTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    public Listener listener = null;
    public Object object = null;

    void callListener(boolean success)
    {
        if(listener != null)
        {
            if(success) listener.onSuccess(this.object);
            else    listener.onFailure(this.object);
        }
    }
}
