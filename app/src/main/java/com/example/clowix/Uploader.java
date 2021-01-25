package com.example.clowix;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Uploader implements Runnable {

    private static final String TAG = "Uploader";
    private final String user_profile_locker;
    private final Uri upload_uri;
    private final String file_type,file_name;
    private final Context context;
    private String file_size;
    private long data_byte_size;
    private final CallBackFromUploader callBackFromUploader;
    private final Handler handler=new Handler();

    public Uploader(String user_profile_locker, Uri upload_uri, String file_type, String file_name, Context context,CallBackFromUploader callBackFromUploader) {
        this.user_profile_locker = user_profile_locker;
        this.upload_uri = upload_uri;
        this.file_type = file_type;
        this.file_name = file_name;
        this.context = context;
        this.callBackFromUploader=callBackFromUploader;
    }

    interface CallBackFromUploader
    {
        void confirmation(Task<Void> task);
    }
    @Override
    public void run() {

        upload_cloud();
    }

    private  void upload_cloud()
    {

       handler.post(new Runnable() {
           @Override
           public void run() {

               Log.d(TAG, "run: status: " + MainFrame.background_status);

               Dialog uploading_status=new Dialog(context,R.style.translate_animator);
               uploading_status.setContentView(R.layout.uploading_status_box);
               uploading_status.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
               uploading_status.setCanceledOnTouchOutside(true);
               Window window=uploading_status.getWindow();
               window.setGravity(Gravity.CENTER);
               uploading_status.show();

               TextView file_name1=uploading_status.findViewById(R.id.file_name1);

               file_name1.setText(file_name);

               StorageReference storageReference= FirebaseStorage.getInstance().getReference(user_profile_locker+"/");

               storageReference.child(file_name).putFile(upload_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Log.d(TAG, "onSuccess: uploaded");

                       taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @RequiresApi(api = Build.VERSION_CODES.O)
                           @Override
                           public void onSuccess(Uri uri) {


                               DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                               LocalDateTime localDateTime = LocalDateTime.now();
                               upload_firebase(file_type,uri.toString()
                                       ,file_name,dateTimeFormatter.format(localDateTime),file_size,data_byte_size,uploading_status);

                           }
                       });

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {

                       Log.d(TAG, "onFailure: error: " + e.getMessage());


                   }
               }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                       Log.d(TAG, "onProgress: " + (snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100);

                       data_byte_size=snapshot.getTotalByteCount();
                       file_size= Formatter.formatFileSize(context,snapshot.getTotalByteCount());

                       TextView downloading_percentage=uploading_status.findViewById(R.id.downloading_file1);
                       ProgressBar progressBar=uploading_status.findViewById(R.id.progressBar3);

                       long value=(snapshot.getBytesTransferred()/snapshot.getTotalByteCount())*100;
                       int progress_status=(int)value;

                       String status="Uploading "+progress_status+"%";

                       downloading_percentage.setText(status);

                       progressBar.setProgress(progress_status);

                   }
               });
           }
       });

    }
    private void upload_firebase(String file_type,String uri,String file_name,String date,String file_size,long data_byte_size,Dialog dialog)
    {

       handler.post(new Runnable() {
           @Override
           public void run() {

               DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);

               String random_key_generator=databaseReference.push().getKey();
               assert random_key_generator != null;
               databaseReference.child(random_key_generator).setValue(new Cloud_Data_Information(file_type,uri,file_name,date,
                       file_size,random_key_generator,data_byte_size,file_name, Shared.Shared_Status.NO.toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {

                        dialog.dismiss();

                       if(callBackFromUploader!=null)
                       {
                           callBackFromUploader.confirmation(task);
                       }
                   }
               });


           }
       });

    }

}
