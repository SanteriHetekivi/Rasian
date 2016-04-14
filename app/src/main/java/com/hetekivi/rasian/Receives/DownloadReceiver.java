package com.hetekivi.rasian.Receives;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.hetekivi.rasian.Activities.MainActivity;
import com.hetekivi.rasian.Data.Global;
import com.hetekivi.rasian.Managers.ResourceManager;
import com.hetekivi.rasian.R;

import java.io.File;

import static com.hetekivi.rasian.External.Tools.PathToOpenFileIntent;

public class DownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "DownloadReceiver";

    public DownloadReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "Download Received!");
        ResourceManager resourceManager = new ResourceManager(context.getResources(), context.getPackageName());
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
        {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            String title = "";
            PendingIntent pendingIntent = null;
            if(downloadManager != null)
            {
                Long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if(id != 0)
                {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(id);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                        if(uri != null) {
                            Intent openFileIntent = PathToOpenFileIntent(uri);
                            if(openFileIntent != null)pendingIntent = PendingIntent.getActivity(context, 0, openFileIntent, 0);
                            else Log.e(TAG, "Making openFileIntent failed!");
                        }else Log.e(TAG, "File uri is null!");
                    }else Log.e(TAG, "Cursor could not move to first!");
                    cursor.close();
                }else Log.e(TAG, "EXTRA_DOWNLOAD_ID not found!");
            }else Log.e(TAG, "No download manager!");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(resourceManager.String(R.string.File, title, R.string.downloaded_END))
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentText("");
            if(pendingIntent != null) mBuilder.setContentIntent(pendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
