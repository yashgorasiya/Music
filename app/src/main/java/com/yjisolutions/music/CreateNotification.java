package com.yjisolutions.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.yjisolutions.music.Services.NotificationActionService;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;


    public static void createNotification(Context context,Song song,int playButton,int pos,int size){

        NotificationManagerCompat notificationManagerCompat =NotificationManagerCompat.from(context);
        MediaSession mediaSession = new MediaSession(context,"tag");

        PendingIntent pendingIntentPrevious = null;
        int drw_previous;
        if(pos == 0){
            drw_previous = 0;
        }else{
            Intent intentPrevios =new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious =PendingIntent.getBroadcast(context,
                    0,
                    intentPrevios,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            drw_previous = R.drawable.back_backward;
        }

        PendingIntent pendingIntentPlay;
        Intent intentPlay =new Intent(context, NotificationActionService.class)
                .setAction(ACTION_PLAY);
        pendingIntentPlay = PendingIntent.getBroadcast(context,
                0,
                intentPlay,
                PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent pendingIntentNext = null;
        int drw_next;
        if(pos == size){
            drw_next = 0;
        }else{
            Intent intentNext =new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext =PendingIntent.getBroadcast(context,
                    0,
                    intentNext,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            drw_next = R.drawable.next_skip;
        }

        notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(song.getName())
                .setContentText(song.getDuration())
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(drw_previous,"previous",pendingIntentPrevious)
                .addAction(playButton,"play/pause",pendingIntentPlay)
                .addAction(drw_next,"next",pendingIntentNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0 ,1 ,2)
                .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken())))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        notificationManagerCompat.notify(1,notification);
    }
}
//new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2).setMediaSession(mediaSession.getSessionToken())