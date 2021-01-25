package com.example.clowix;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Task;

public class File_Uploader_Service extends Service implements Uploader.CallBackFromUploader  {

    private static final String TAG = "File_Uploader_Service";
    public static String data_uploader_key="user_uploaded_key";
    private String file_name;
    public static Context context;

    public File_Uploader_Service() {

        Log.d(TAG, "File_Uploader_Service: started");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        UploadingDetails uploadingDetails=(UploadingDetails) intent.getSerializableExtra(File_Uploader_Service.data_uploader_key);

        file_name=uploadingDetails.getFile_name();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notification();
        }

               new Thread(new Uploader(uploadingDetails.getUser_profile_locker(),
                       Uri.parse(uploadingDetails.getUpload_uri()),uploadingDetails.getFile_type(),
                       uploadingDetails.getFile_name(),context,this)).start();

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void notification()
    {
        String channel_id="1";
        String channel_name="TaskAlarm";

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {

            NotificationChannel notificationChannel=new NotificationChannel(channel_id,channel_name, NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.BLUE);

            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);


        }

        Notification notification=new NotificationCompat.Builder(this,channel_id)
                .setPriority(NotificationManager.IMPORTANCE_NONE).setOngoing(true).setSmallIcon(R.drawable.clowix_logo)
                .setContentTitle("Uploading..").setContentText(file_name).setCategory(Notification.CATEGORY_SERVICE).build();


        startForeground(1,notification);
    }

    @Override
    public void confirmation(Task<Void> task) {

        Log.d(TAG, "confirmation: called");

            if(task.isSuccessful())
            {
                DialogShower dialogShower=new DialogShower(R.layout.data_uploaded,R.style.translate_animator,context,1000);
                dialogShower.showDialog();
            }
            else
            {
                DialogShower dialogShower=new DialogShower(R.layout.unable_to_upload,R.style.translate_animator,context,1000);
                dialogShower.showDialog();
            }

           stopSelf();
        }
}