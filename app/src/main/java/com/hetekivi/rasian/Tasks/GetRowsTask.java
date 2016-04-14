package com.hetekivi.rasian.Tasks;

import android.os.AsyncTask;
import com.hetekivi.rasian.Data.RSS.Data;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Interfaces.Rowable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * AsyncTask
 * for getting rows.
 */
public class GetRowsTask extends RootTask<Void, Void, List<Data>>
{
    private Rowable delegate = null;

    public GetRowsTask(Rowable obj)
    {
        this.delegate = obj;
    }

    public GetRowsTask(Rowable obj, Listener _listener)
    {
        this.delegate = obj;
        this.listener = _listener;
    }

    public GetRowsTask(Rowable obj, Listener _listener, Object _object)
    {
        this.delegate = obj;
        this.listener = _listener;
        this.object = _object;
    }

    /**
     * Background runner.
     * for getting rows.
     * @param voids Nothing...
     * @return Rows.
     */
    @Override
    protected List<Data> doInBackground(Void... voids)
    {
        List<Data> rows = new LinkedList<>();
        if(this.delegate != null)
        {
            rows.addAll(delegate.Rows());
        }
        return rows;
    }

    /**
     * Post Executor
     * for telling caller what happened.
     * @param rows All rows from object.
     */
    @Override
    protected void onPostExecute(List<Data> rows) {
        super.onPostExecute(rows);
        if(delegate != null)
        {
            delegate.onRowsGotten(rows);
        }
        this.callListener(true);
    }
}