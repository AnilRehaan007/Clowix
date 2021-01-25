package com.example.clowix;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class SideOperator implements Runnable {

    private static final String TAG = "SideOperator";
    private final Cloud_Data_Information cloud_data_information;
    private final CallBackFromSideOperator callBackFromSideOperator;
    private final SideOperatorStatus sideOperatorStatus;
    private final Context context;
    private final Handler handler=new Handler();
    private final String user_profile_locker;

    public SideOperator(Cloud_Data_Information cloud_data_information,
                        CallBackFromSideOperator callBackFromSideOperator, SideOperatorStatus sideOperatorStatus,Context context,String user_profile_locker) {
        this.cloud_data_information = cloud_data_information;
        this.callBackFromSideOperator = callBackFromSideOperator;
        this.sideOperatorStatus = sideOperatorStatus;
        this.context=context;
        this.user_profile_locker=user_profile_locker;
    }

    enum SideOperatorStatus
    {
        Delete,Share,Save,Rename,Cancel;
    }

    interface CallBackFromSideOperator
    {
        void callBackFromSideOperator(Task<Void> task,SideOperatorStatus sideOperatorStatus);
    }

    @Override
    public void run() {

        switch (sideOperatorStatus)
        {

            case Delete:

                delete_item(cloud_data_information);

                break;

            case Share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if(cloud_data_information.getFile_name().equals(cloud_data_information.getRename_file_name()))
                {
                    sendIntent.putExtra(Intent.EXTRA_TEXT,cloud_data_information.getFile_name() + " link: \n\n"+cloud_data_information.getDownload_uri());
                }
                else
                {
                    sendIntent.putExtra(Intent.EXTRA_TEXT,cloud_data_information.getRename_file_name() + " link: \n\n"+cloud_data_information.getDownload_uri());
                }

                sendIntent.setType("text/plain");
                status_updater(cloud_data_information);
                context.startActivity(sendIntent);
                break;

            case Save:
              save_data(cloud_data_information);
                break;

            case Rename:

                rename_file(cloud_data_information);
                break;
        }


    }

    private void rename_file(Cloud_Data_Information cloud_data_information)
    {
        if(callBackFromSideOperator!=null)
        {

            callBackFromSideOperator.callBackFromSideOperator(null,SideOperatorStatus.Cancel);

        }
        handler.post(new Runnable() {
            @Override
            public void run() {

                Dialog rename_dialog=new Dialog(context,R.style.translate_animator);
                rename_dialog.setContentView(R.layout.rename_dialog);
                rename_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                rename_dialog.setCanceledOnTouchOutside(true);
                Window window=rename_dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                rename_dialog.show();

                EditText text=rename_dialog.findViewById(R.id.initial_space);

                if(cloud_data_information.getFile_name().equals(cloud_data_information.getRename_file_name()))
                {
                    text.setText(cloud_data_information.getFile_name());
                }
                else
                {
                    text.setText(cloud_data_information.getRename_file_name());
                }

                String selected_file_extension = MimeTypeMap.getFileExtensionFromUrl(cloud_data_information.getFile_name());

                rename_dialog.findViewById(R.id.rename_click).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String fileExt = MimeTypeMap.getFileExtensionFromUrl(text.getText().toString());

                        String updated_name;

                        if(fileExt.length()==0)
                        {
                         updated_name=text.getText().toString();
                         updated_name+="."+selected_file_extension;
                        }
                        else
                        {
                            updated_name=text.getText().toString();
                        }

                      rename_file(cloud_data_information,updated_name,rename_dialog);

                    }
                });


            }
        });


    }

    private void status_updater(Cloud_Data_Information cloud_data_information)
    {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);

        Map<String,Object> update_file=new HashMap<>();

        update_file.put(Cloud_Data_Information.Field_name.shared_status.toString(),Shared.Shared_Status.YES.toString());

        databaseReference.child(cloud_data_information.getRandom_key_generator()).updateChildren(update_file).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(callBackFromSideOperator!=null)
                {

                    callBackFromSideOperator.callBackFromSideOperator(task,SideOperatorStatus.Share);

                }


            }
        });



    }

private void rename_file(Cloud_Data_Information cloud_data_information,String update_name,Dialog dialog)
{

    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);

    Map<String,Object> update_file=new HashMap<>();

    update_file.put(Cloud_Data_Information.Field_name.rename_file_name.toString(),update_name);

    databaseReference.child(cloud_data_information.getRandom_key_generator()).updateChildren(update_file).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

             dialog.dismiss();

            if(callBackFromSideOperator!=null)
            {

                callBackFromSideOperator.callBackFromSideOperator(task,SideOperatorStatus.Rename);

            }


        }
    });



}

    private void delete_item(Cloud_Data_Information cloud_data_information)
    {

        StorageReference storageReference= FirebaseStorage.getInstance().getReference(user_profile_locker+"/");



        storageReference.child(cloud_data_information.getFile_name()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);
                databaseReference.child(cloud_data_information.getRandom_key_generator()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(callBackFromSideOperator!=null)
                        {
                            callBackFromSideOperator.callBackFromSideOperator(task,SideOperatorStatus.Delete);

                        }

                    }
                });
            }
        });

       }

       private void save_data(Cloud_Data_Information cloud_data_information)
       {

              handler.post(new Runnable() {
                  @Override
                  public void run() {

                      Dialog downloading_status=new Dialog(context,R.style.translate_animator);
                      downloading_status.setContentView(R.layout.downloading_status_box);
                      downloading_status.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                      downloading_status.setCanceledOnTouchOutside(true);
                      Window window=downloading_status.getWindow();
                      window.setGravity(Gravity.CENTER);
                      downloading_status.show();

                      TextView file_name=downloading_status.findViewById(R.id.file_name1);

                      if(cloud_data_information.getRename_file_name().equals(cloud_data_information.getFile_name()))
                      {
                          file_name.setText(cloud_data_information.getFile_name());

                      }
                      else
                      {
                          file_name.setText(cloud_data_information.getRename_file_name());
                      }

                      File destination;

                      if(cloud_data_information.getRename_file_name().length()==0)
                      {
                          destination=new File(Environment.getExternalStorageDirectory(),cloud_data_information.getFile_name());
                      }
                      else
                      {
                          destination=new File(Environment.getExternalStorageDirectory(),cloud_data_information.getRename_file_name());
                      }

                      StorageReference storageReference= FirebaseStorage.getInstance().getReference(user_profile_locker+"/");

                      storageReference.child(cloud_data_information.getFile_name()).getFile(destination).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {



                              final_process(downloading_status,task);

                          }
                      }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                          @Override
                          public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {

                              TextView downloading_percentage=downloading_status.findViewById(R.id.downloading_file1);
                              ProgressBar progressBar=downloading_status.findViewById(R.id.progressBar3);

                              long value=(snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100;
                              int progress_status=(int)value;

                              String status="Downloading "+progress_status+"%";

                              downloading_percentage.setText(status);

                              progressBar.setProgress(progress_status);
                          }
                      });


                  }
              });
       }

     private void final_process(Dialog dialog,Task<FileDownloadTask.TaskSnapshot> task)
     {
         handler.post(new Runnable() {
             @Override
             public void run() {

                 DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);

               databaseReference.child("demo").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {


                                   dialog.dismiss();


                              if(callBackFromSideOperator!=null)
                              {

                                  Task<Void> task1=new Task<Void>() {
                                      @Override
                                      public boolean isComplete() {
                                          return false;
                                      }

                                      @Override
                                      public boolean isSuccessful() {
                                          return task.isSuccessful();
                                      }

                                      @Override
                                      public boolean isCanceled() {
                                          return false;
                                      }

                                      @Nullable
                                      @Override
                                      public Void getResult() {
                                          return null;
                                      }

                                      @Nullable
                                      @Override
                                      public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
                                          return null;
                                      }

                                      @Nullable
                                      @Override
                                      public Exception getException() {
                                          return task.getException();
                                      }

                                      @NonNull
                                      @Override
                                      public Task<Void> addOnSuccessListener(@NonNull OnSuccessListener<? super Void> onSuccessListener) {
                                          return null;
                                      }

                                      @NonNull
                                      @Override
                                      public Task<Void> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                                          return null;
                                      }

                                      @NonNull
                                      @Override
                                      public Task<Void> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
                                          return null;
                                      }

                                      @NonNull
                                      @Override
                                      public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                                          return null;
                                      }

                                      @NonNull
                                      @Override
                                      public Task<Void> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                                          return null;
                                      }

                                      @NonNull
                                      @Override
                                      public Task<Void> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                                          return null;
                                      }
                                  };

                                  callBackFromSideOperator.callBackFromSideOperator(task1,SideOperatorStatus.Save);

                              }
                   }
               });


             }
         });




     }
    }
