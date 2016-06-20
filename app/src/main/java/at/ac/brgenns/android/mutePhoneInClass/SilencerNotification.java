package at.ac.brgenns.android.mutePhoneInClass;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling silencer
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class SilencerNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Silencer";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p/>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p/>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of silencer notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context,
                              final String exampleString, final int number) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
//        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);

        final String ticker = exampleString;
        final String title = res.getString(
                R.string.silencer_notification_title, exampleString);
        final String text = res.getString(
                R.string.silencer_notification_text, exampleString);

        Intent intent = new Intent(context, MutePhoneService.class);
        PendingIntent pendingIntent1 = PendingIntent.getService(context, 1, intent, 0);
        PendingIntent pendingIntent2 = PendingIntent.getService(context, 2, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0, 0})
                .setOnlyAlertOnce(true)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_volume_off_black_24dp)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_LOW)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
//                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText("Dummy summary text"))

                .addAction(R.drawable.ic_volume_off_black_24dp,
                        res.getString(R.string.action_unmute), pendingIntent1)
                .addAction(R.drawable.ic_add_black_24dp, res.getString(R.string.action_settings),
                        pendingIntent2)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(false);

        notify(context, builder.build());
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
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
