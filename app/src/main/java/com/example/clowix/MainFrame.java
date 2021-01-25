package com.example.clowix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainFrame extends AppCompatActivity {

    private static final String TAG = "MainFrame";

    public static final String user_email_id="user_email_id";
    private String user_locker_value,file_name;
    private File_Type data_type;
    private SearchView searchView;
    private int network_status=1;
    private BroadcastReceiver networkChangeReceiver;
    public static String background_status="main";
    private final Handler handler=new Handler();

    enum Status
    {
        Files,Recent,Shared,Photos,User;
    }
    enum File_Type
    {
        Document,Audio,Video,Image,None;
    }
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){


                Intent intent=new Intent(MainFrame.this,MainActivity.class);

                startActivity(intent);

            }
            else {

                Log.d(TAG, "onAuthStateChanged: im stayed");
            }
        }
    };
    private void broadCast_caller()
    {
               networkChangeReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "onReceive: called");

                int status=Network_Status.get_connectivity_status(context);

                if(status==Network_Status.TYPE_MOBILE)
                {

                    Log.d(TAG, "onReceive: mobile type");

                    network_status=1;
                }
                else if(status==Network_Status.TYPE_WIFI)
                {
                    Log.d(TAG, "onReceive: wifi");
                    network_status=1;
                }

                else if(status==Network_Status.TYPE_NOT_CONNECTED)
                {
                    network_status=Network_Status.TYPE_NOT_CONNECTED;
                    DialogShower dialogShower=new DialogShower(R.layout.internet_error,R.style.translate_animator,MainFrame.this);
                    dialogShower.showDialog();
                }

            }
        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        broadCast_caller();

         user_locker_value=getIntent().getStringExtra(MainFrame.user_email_id);

//        user_locker_value="user";

        searchView=findViewById(R.id.search_view);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new File_Tab(searchView,network_status,user_locker_value)).addToBackStack(null).commit();

        TextView status=findViewById(R.id.status);
        ImageView diamond=findViewById(R.id.diamond);
        FloatingActionButton files = findViewById(R.id.files);
        FloatingActionButton recent = findViewById(R.id.recent);
        FloatingActionButton shared = findViewById(R.id.shared);
        FloatingActionButton photos = findViewById(R.id.photos);
        FloatingActionButton user = findViewById(R.id.user_details);
        FloatingActionButton add_data = findViewById(R.id.add_data);


        diamond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog premium_box=new Dialog(MainFrame.this,R.style.translate_animator);
                premium_box.setContentView(R.layout.diamond_primium);
                premium_box.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                premium_box.setCanceledOnTouchOutside(true);
                Window window=premium_box.getWindow();
                window.setGravity(Gravity.CENTER);
                premium_box.show();

             TextView initial_space=premium_box.findViewById(R.id.initial_space);
             FloatingActionButton unlock=premium_box.findViewById(R.id.reset);

               String space= android.text.format.Formatter.formatFileSize(MainFrame.this, SpaceAllocator.initial_space);

               String result="Initial Space "+space;

               initial_space.setText(result);

               unlock.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       Toast.makeText(MainFrame.this,"App is Under Development",Toast.LENGTH_LONG).show();
                   }
               });
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status.setVisibility(View.INVISIBLE);
                diamond.setVisibility(View.INVISIBLE);

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                 status.setVisibility(View.VISIBLE);
                 diamond.setVisibility(View.VISIBLE);

                return false;
            }
        });

        files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status.setText(Status.Files.toString());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new File_Tab(searchView,network_status,user_locker_value)).addToBackStack(null).commit();
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status.setText(Status.Recent.toString());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new Recent(searchView,network_status,user_locker_value)).addToBackStack(null).commit();
            }
        });
        shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status.setText(Status.Shared.toString());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new Shared(searchView,network_status,user_locker_value)).addToBackStack(null).commit();
            }
        });
        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status.setText(Status.Photos.toString());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new Pictures_tab(network_status,user_locker_value)).addToBackStack(null).commit();
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                status.setText(Status.User.toString());
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new User_Tab(network_status,user_locker_value)).addToBackStack(null).commit();
            }
        });
        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainFrame.background_status="tab";

             if(network_status==Network_Status.TYPE_NOT_CONNECTED)
             {
                 DialogShower dialogShower=new DialogShower(R.layout.internet_error,R.style.translate_animator,MainFrame.this);
                 dialogShower.showDialog();
             }
             else
             {
                 int permission= ActivityCompat.checkSelfPermission(MainFrame.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                 if(permission!= PackageManager.PERMISSION_GRANTED)
                 {
                     ActivityCompat.requestPermissions(MainFrame.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                             ,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                 }
                 else
                 {

                     Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                     intent.addCategory(Intent.CATEGORY_OPENABLE);
                     intent.setType("*/*");
                     startActivityForResult(intent,1);
                 }
             }
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(MainFrame.background_status.equals("tab"))
        {

            MainFrame.background_status="main";
        }
        else
        {
          MainFrame.background_status="shutDown";

        }
      
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent,1);


            } else {

                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }
                else
                {
                    Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);

                }
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {

           File_Type file_type=type_finder(get_data(data.getData()));


           if(file_type==File_Type.Image)
           {

               data_type=File_Type.Image;
           }
           else if(file_type==File_Type.Audio)
           {

              data_type=File_Type.Audio;
           }
           else if(file_type==File_Type.Video)
           {
               data_type=File_Type.Video;
           }

           else if(file_type==File_Type.None)
           {

               data_type=File_Type.Document;
           }

           if(data_type!=null)
           {


               UploadingDetails uploadingDetails=new UploadingDetails(user_locker_value,data.getData().toString(),data_type.toString(),file_name);

               Intent intent=new Intent(MainFrame.this,File_Uploader_Service.class);

                intent.putExtra(File_Uploader_Service.data_uploader_key,uploadingDetails);
                File_Uploader_Service.context=MainFrame.this;
                startService(intent);

           }


        }
    }
    

    private String get_data(Uri uri)
    {
        String value=null;
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(uri,null,null,null,null);

        if(cursor!=null)
        {
            while (cursor.moveToNext())
            {

                value=cursor.getString(cursor.getColumnIndex("_display_name"));
                file_name=value;
            }
          cursor.close();


        }

      return value;
    }

   private File_Type type_finder(String value)
   {

       Log.d(TAG, "type_finder: value: " + value);

       if(isImage(value))
       {
           return File_Type.Image;
       }
       else if(isAudio(value))
       {
           return File_Type.Audio;

       }else if(isVideo(value)) {
           return File_Type.Video;

       }

       return File_Type.None;
   }

   private boolean isImage(String value)
   {
       return value.endsWith(".apng") || value.endsWith(".APNG") || value.endsWith(".avif")
               || value.endsWith(".GIF") || value.endsWith(".gif") || value.endsWith(".AVIF")
               || value.endsWith(".jpg") || value.endsWith(".JPG") || value.endsWith(".jpeg")
               || value.endsWith(".JFIF") || value.endsWith(".jfif") || value.endsWith(".JPEG")
               || value.endsWith(".PJPEG") || value.endsWith(".pjpeg") || value.endsWith(".pjp")
               || value.endsWith(".PNG") || value.endsWith(".png") || value.endsWith(".PJP")
               || value.endsWith(".svg") || value.endsWith(".SVG") || value.endsWith(".webp") || value.endsWith(".WEBP");
   }
    private boolean isAudio(String value)
    {

        return value.endsWith(".mp3") || value.endsWith(".MP3");

    }
    private boolean isVideo(String value)
    {
        return value.endsWith(".webm") || value.endsWith(".WEBM") || value.endsWith(".mpg")
                || value.endsWith(".MPG") || value.endsWith(".mp2") || value.endsWith(".MP2")
                || value.endsWith(".MPE") || value.endsWith(".mpeg") || value.endsWith(".MPEG")
                || value.endsWith(".mpe") || value.endsWith(".mpv") || value.endsWith(".MPV")
                || value.endsWith(".mpg") || value.endsWith(".ogg") || value.endsWith(".OGG")
                || value.endsWith(".MPG") || value.endsWith(".m4p") || value.endsWith(".M4P")
                || value.endsWith(".m4v") || value.endsWith(".M4V") || value.endsWith(".avi")
                || value.endsWith(".AVI") || value.endsWith(".wmv") || value.endsWith(".WMV")
                || value.endsWith(".mov") || value.endsWith(".MOV") || value.endsWith(".qt")
                || value.endsWith(".QT") || value.endsWith("flv") || value.endsWith(".FLV")
                || value.endsWith(".swf") || value.endsWith(".SWF") || value.endsWith(".avchd")
                || value.endsWith(".AVCHD") || value.endsWith(".mp4") || value.endsWith(".MP4");
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setMessage("Are You Sure You Want To Sign Out").setNegativeButton("STAY", (dialogInterface, i) -> {
                    Log.d(TAG, "onClick: stay");
                }).setPositiveButton("SIGN OUT", (dialogInterface, i) -> {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.addAuthStateListener(authStateListener);
                    firebaseAuth.signOut();
                });
        builder.setCancelable(false);
        builder.show();

    }
}