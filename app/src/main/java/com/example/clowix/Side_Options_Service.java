package com.example.clowix;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Task;



public class Side_Options_Service extends Service implements SideOperator.CallBackFromSideOperator{

    private static final String TAG = "Side_Options_Service";

    public static Context context;
    public static String status_name="status_name",cloud_data_information_key="cloud_data_information",user_locker_key="user_locker_key";
    private SideOperator.SideOperatorStatus sideOperatorStatus;

    public Side_Options_Service() {

        Log.d(TAG, "Side_Options_Service: start");
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.sideOperatorStatus= (SideOperator.SideOperatorStatus)intent.getSerializableExtra(Side_Options_Service.status_name);
        String user_locker=intent.getStringExtra(Side_Options_Service.user_locker_key);
       Cloud_Data_Information cloud_data_information=(Cloud_Data_Information)intent.getSerializableExtra(Side_Options_Service.cloud_data_information_key);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            notification();
        }

                              new Thread(new SideOperator(cloud_data_information,
                              this,sideOperatorStatus,context,user_locker)).start();

        return START_REDELIVER_INTENT;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
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
                .setContentTitle("Task Performing..").setContentText(sideOperatorStatus.toString()).setCategory(Notification.CATEGORY_SERVICE).build();


        startForeground(1,notification);
    }

    @Override
    public void callBackFromSideOperator(Task<Void> task, SideOperator.SideOperatorStatus sideOperatorStatus) {

        if(task==null)
        {
            stopSelf();
        }
        else
        {
            if(sideOperatorStatus.equals(SideOperator.SideOperatorStatus.Delete))
            {
                if(task.isSuccessful())
                {
                    DialogShower dialogShower=new DialogShower(R.layout.datadeletebox,R.style.translate_animator,context,1000);
                    dialogShower.showDialog();
                }
                else
                {
                    DialogShower dialogShower=new DialogShower(R.layout.unable_to_delete,R.style.translate_animator,context,1000);
                    dialogShower.showDialog();
                }

                stopSelf();
            }

            else if(sideOperatorStatus.equals(SideOperator.SideOperatorStatus.Save))
            {

                if(task.isSuccessful())
                {
                    DialogShower dialogShower=new DialogShower(R.layout.data_downloaded,R.style.translate_animator,context,1000);
                    dialogShower.showDialog();
                }
                else
                {
                    DialogShower dialogShower=new DialogShower(R.layout.unable_to_download,R.style.translate_animator,context,1000);
                    dialogShower.showDialog();
                }

                stopSelf();

            }

            else if(sideOperatorStatus.equals(SideOperator.SideOperatorStatus.Rename))
            {
                MainActivity.return_status="rename_tab";
                if(task.isSuccessful())
                {
                    DialogShower dialogShower=new DialogShower(R.layout.rename_success_box,R.style.translate_animator,context,1000);
                    dialogShower.showDialog();
                    Intent intent=new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                }
                else
                {
                    DialogShower dialogShower=new DialogShower(R.layout.unable_to_rename,R.style.translate_animator,context,1000);
                    dialogShower.showDialog();
                    Intent intent=new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                }

                stopSelf();


            }
            else if(sideOperatorStatus.equals(SideOperator.SideOperatorStatus.Share))
            {

                stopSelf();
            }

        }

    }
}