package net.redboxgames.redlyrics;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static net.redboxgames.redlyrics.NotificationClassAndroidO.BroadcastTypes.METADATA_CHANGED;

public class NotificationClassAndroidO extends BroadcastReceiver {
    static final class BroadcastTypes
    {
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.
        long timeSentInMs = intent.getLongExtra("timeSent", 0L);

        Intent intentt = new Intent(context, NotificationClassAndroidO.class);
        intentt.setAction("com.spotify.music.metadatachanged");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2000, intentt, PendingIntent.FLAG_UPDATE_CURRENT);

            String action = intent.getAction();
            String trackId = intent.getStringExtra("id");
            String artistName = intent.getStringExtra("artist");
            String albumName = intent.getStringExtra("album");
            String trackName = intent.getStringExtra("track");
            int trackLengthInSec = intent.getIntExtra("length", 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                            .setContentTitle(trackName + " by " + artistName)
                            .setContentText("Tap to Get Lyrics")
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());

    }
}
