package com.thebluealliance.androidclient.gcm.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.gcm.GCMMessageHandler;
import com.thebluealliance.androidclient.listeners.NotificationDismissedListener;
import com.thebluealliance.androidclient.models.StoredNotification;

/**
 * Created by Nathan on 7/24/2014.
 */
public abstract class BaseNotification {

    String messageData;
    String messageType;
    Gson gson;
    private String logTag;
    protected boolean display;
    StoredNotification stored;

    public BaseNotification(String messageType, String messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
        this.gson = JSONManager.getGson();
        this.logTag = null;
        this.display = true;
        this.stored = null;
    }

    public boolean shouldShow(){
        return display;
    }

    public abstract Notification buildNotification(Context context);

    public String getNotificationType() {
        return messageType;
    }

    public abstract void parseMessageData() throws JsonParseException;

    public abstract void updateDataLocally(Context c);

    public abstract int getNotificationId();

    public StoredNotification getStoredNotification(){
        return stored;
    }

    protected String getLogTag() {
        if (logTag == null) {
            logTag = Constants.LOG_TAG + "/" + messageType;
        }
        return logTag;
    }

    /**
     * Creates a builder with the important defaults set.
     */
    public NotificationCompat.Builder getBaseBuilder(Context context) {
        PendingIntent onDismiss = PendingIntent.getBroadcast(context, 0, NotificationDismissedListener.newIntent(context), 0);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setGroup(GCMMessageHandler.GROUP_KEY)
                .setColor(context.getResources().getColor(R.color.accent_dark))
                .setDeleteIntent(onDismiss)
                .setAutoCancel(true)
                .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.tba_blue_background)));
    }

    /**
     * Creates a builder with the important defaults and an Activity content intent.
     */
    public NotificationCompat.Builder getBaseBuilder(Context context, Intent activityIntent) {
        NotificationCompat.Builder builder = getBaseBuilder(context);
        PendingIntent onTap = PendingIntent.getActivity(context, getNotificationId(), activityIntent, 0);

        builder.setContentIntent(onTap);
        return builder;
    }

    protected static Bitmap getLargeIconFormattedForPlatform(Context context, @DrawableRes int drawable) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), drawable);
        // Show just the white image on 4.x
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return largeIcon;
        }
        // Add a colored circle on 5.x+
        int padding = Utilities.getPixelsFromDp(context, 8);
        int circleDiameter = Math.max(largeIcon.getWidth(), largeIcon.getHeight()) + (padding * 2);
        Bitmap finalBitmap = Bitmap.createBitmap(circleDiameter, circleDiameter, largeIcon.getConfig());
        Canvas c = new Canvas(finalBitmap);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(context.getResources().getColor(R.color.primary));
        backgroundPaint.setAntiAlias(true);
        c.drawCircle(circleDiameter / 2, circleDiameter / 2, circleDiameter / 2, backgroundPaint);
        c.drawBitmap(largeIcon, padding, padding, null);
        return finalBitmap;
    }

}
