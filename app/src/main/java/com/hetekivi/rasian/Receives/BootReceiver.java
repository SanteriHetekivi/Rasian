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
import net.danlew.android.joda.JodaTimeAndroid;

import static com.hetekivi.rasian.Data.Global.Feeds;

/**
 * Receiver BootReceiver
 * for making setup on device boot.
 */
public class BootReceiver extends BroadcastReceiver
{
    /**
     * Constructor
     */
    public BootReceiver() {
    }

    /**
     * Listener for load.
     */
    private Listener loadListener = new Listener() {

        @Override
        public void onSuccess(Object additional) {
            Feeds.resetAlarm();
            new UpdateTask(Feeds, updateListener).execute();
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
        public void onSuccess(Object additional) {

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
        public void onSuccess(Object additional) {

        }

        @Override
        public void onFailure(Object additional) {

        }
    };

    /**
     * Function onReceive
     * runs when this class receives something.
     * Sets up alarm.
     * @param context Application context.
     * @param intent Caller intent.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        JodaTimeAndroid.init(context);
        Global.Preference = new PreferenceManager(context);
        Global.context = context;
        Global.WRITE_EXTERNAL_STORAGE = true;
        new LoadTask(Feeds, this.loadListener).execute();
    }
}
