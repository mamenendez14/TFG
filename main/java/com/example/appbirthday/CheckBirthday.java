package com.example.appbirthday;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;

public class CheckBirthday extends Worker {

    String nextName;
    int nextDays;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;

    public CheckBirthday(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    //Method launched by work manager
    @NonNull
    @Override
    public Result doWork() {

        Log.d("notify","Lanzando proceso");

        createNotificationChannel();
        createNotification();

        return Result.success();
    }
    //Method to check if today is someone birthday and create a notification if that's the case
    @SuppressLint("Range")
    public void createNotification(){

        Database db = Database.getInstance(getApplicationContext(), Database.DB_NAME, null, 1);
        SQLiteDatabase select = db.getReadableDatabase();

        try {
            db.updateRemainingDays();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Cursor c = select.query("birthday", new String[]{"name", "daysRemaining"}, null, null, null, null, "daysRemaining" );

        if (c != null && !db.checkEmpty()) {
            c.moveToFirst();
            nextName = c.getString(c.getColumnIndex("name"));
            nextDays = c.getInt(c.getColumnIndex("daysRemaining"));

            if (nextDays == 0) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                builder.setSmallIcon(R.drawable.ic_baseline_celebration_24);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setContentTitle("Recordatorio");
                builder.setContentText("Hoy es el cumpleaños de "+nextName+". Felicítalo ya!");
                builder.setColor(Color.BLUE);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setAutoCancel(true);
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
            }
        }
    }
    //Notification parameters if SDK > Android Oreo
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}