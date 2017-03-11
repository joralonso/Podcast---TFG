package es.usal.podcast.reproductor;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import es.usal.podcast.R;
import es.usal.podcast.modelo.Radio;

/**
 * Created by Jorge on 22/5/16.
 */
public class PlayerRadioNotification {

    private static final String NOTIFICATION_TAG = "PlayerNotification";
    private static final int NOTIFICATION_ID = 3;

    private static RemoteViews mNotificationTemplate;
    private static Radio radio;


    public static void play(Context context){

        Log.d("Notification", "Play");

        mNotificationTemplate.setImageViewResource(R.id.notification_play, R.drawable.ic_pause);

        Intent iPause = new Intent(ReproductorService.PLAY_SERVICE_CMD);
        iPause.putExtra(ReproductorService.CMD_NAME, ReproductorService.CMD_PAUSE);
        PendingIntent pPause = PendingIntent.getBroadcast(context, 1, iPause, PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationTemplate.setOnClickPendingIntent(R.id.notification_play, pPause);
        notify(context, mNotificationTemplate);
    }

    public static void pause(Context context){

        Log.d("Notification", "Pause");

        mNotificationTemplate.setImageViewResource(R.id.notification_play, R.drawable.ic_play);

        Intent iPlay = new Intent(ReproductorService.PAUSE_SERVICE_CMD);
        iPlay.putExtra(ReproductorService.CMD_NAME, ReproductorService.CMD_PLAY);
        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, iPlay, PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationTemplate.setOnClickPendingIntent(R.id.notification_play, pPlay);
        notify(context, mNotificationTemplate);
    }



    public static void notify(final Context context, Radio _radio) {
        radio = _radio;

        mNotificationTemplate = new RemoteViews(context.getPackageName(), R.layout.notification);
        mNotificationTemplate.setTextViewText(R.id.notification_line_one, radio.getTitulo());
        mNotificationTemplate.setTextViewText(R.id.notification_line_two, "Radio Online");
        //mNotificationTemplate.setImageViewResource(R.id.notification_image, R.drawable.example_picture);
        mNotificationTemplate.setImageViewResource(R.id.notification_play, R.drawable.ic_pause);

        Intent iPause = new Intent(ReproductorService.PLAY_SERVICE_CMD);
        iPause.putExtra(ReproductorService.CMD_NAME, ReproductorService.CMD_PAUSE);
        PendingIntent pPause = PendingIntent.getBroadcast(context, 1, iPause, PendingIntent.FLAG_CANCEL_CURRENT);
        mNotificationTemplate.setOnClickPendingIntent(R.id.notification_play, pPause);


        Intent iStop = new Intent(ReproductorService.STOP_SERVICE_CMD);
        iStop.putExtra(ReproductorService.CMD_NAME, ReproductorService.CMD_STOP);
        PendingIntent pStop = PendingIntent.getBroadcast(context, 2, iStop, PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationTemplate.setOnClickPendingIntent(R.id.notification_collapse, pStop);

        PlayerRadioNotification.notify(context, mNotificationTemplate);


    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final RemoteViews mNotificationTemplate) {

        Intent intent = new Intent(context, ReproductorRadioActivity.class);
        radio.setIntent(intent);

        Notification notification = new NotificationCompat.Builder(context)
                .setContent(mNotificationTemplate)
                .setSmallIcon(R.drawable.ic_play)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setDefaults(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setContentIntent( PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(false).build();

        Picasso.with(context).load(radio.getImagenSmall()).into(mNotificationTemplate, R.id.notification_image, NOTIFICATION_ID, notification);


        /*
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
        */
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            //nm.cancel(NOTIFICATION_TAG, 0);

            nm.cancel(NOTIFICATION_ID);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
