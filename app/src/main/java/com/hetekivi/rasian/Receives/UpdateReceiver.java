package com.hetekivi.rasian.Receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Interfaces.Listener;
import com.hetekivi.rasian.Managers.PreferenceManager;
import com.hetekivi.rasian.Tasks.LoadTask;
import com.hetekivi.rasian.Tasks.SaveTask;
import com.hetekivi.rasian.Tasks.UpdateTask;
import org.joda.time.DateTime;

import static com.hetekivi.rasian.Data.Global.Feeds;

/**
 * Receiver UpdateReceiver
 * for running a update.
 */
public class UpdateReceiver extends BroadcastReceiver
{
    /**
     * Constructor
     */
    public UpdateReceiver() {
    }

    private DateTime receiveDateTime; // Time when objects receives.

    /**
     * Listener for load.
     */
    private Listener loadListener = new Listener() {
        @Override
        public void onSuccess() {
            new UpdateTask(Feeds, updateListener).execute(false, false);
        }

        @Override
        public void onSuccess(Object additional) {

        }

        @Override
        public void onFailure() {

        }

        @Override
        public void onFailure(Object additional) {

        }
    };

    /**
     * Listener for update.
     */
    private Listener updateListener = new Listener() {
        @Override
        public void onSuccess() {
            Feeds.NextUpdate = receiveDateTime.plusMillis(Feeds.DelayMillis());
            new SaveTask(Feeds, saveListener).execute();
        }

        @Override
        public void onSuccess(Object additional) {

        }

        @Override
        public void onFailure() {

        }

        @Override
        public void onFailure(Object additional) {

        }
    };

    /**
     * Listener for save.
     */
    private Listener saveListener = new Listener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onSuccess(Object additional) {

        }

        @Override
        public void onFailure() {

        }

        @Override
        public void onFailure(Object additional) {

        }
    };

    /**
     * Function onReceive
     * runs when this class receives something.
     * Updates objects and downloads anything there is to download.
     * Updates next update date.
     * @param context Application context.
     * @param intent Caller intent.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.receiveDateTime = new DateTime();
        Global.Preference = new PreferenceManager(context);
        Global.context = context;
        new LoadTask(Feeds, this.loadListener).execute();
    }
}
