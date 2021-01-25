package com.example.clowix;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static String return_status="empty";
    public static final String demo="demo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 if(MainActivity.return_status.equals("user_tab") || MainActivity.return_status.equals("rename_tab"))
 {
     MainActivity.return_status="empty";
     Intent intent=new Intent(MainActivity.this,MainFrame.class);
     intent.putExtra(MainFrame.user_email_id, Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().
             getCurrentUser()).getEmail()).replaceAll("\\.",""));
     startActivity(intent);
 }
 else
 {
     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Introduction()).addToBackStack(null).commit();


     Timer timer=new Timer();
     timer.schedule(new TimerTask() {
         @Override
         public void run() {

             if(FirebaseAuth.getInstance().getCurrentUser()==null)
             {
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Login_SignUp()).addToBackStack(null).commit();
             }
             else
             {
                 Intent intent=new Intent(MainActivity.this,MainFrame.class);
                 intent.putExtra(MainFrame.user_email_id, Objects.requireNonNull(FirebaseAuth.getInstance().
                         getCurrentUser().getEmail()).replaceAll("\\.",""));
                 startActivity(intent);
             }
             timer.cancel();
         }
     },1500);
 }

    }

    @Override
    public void onBackPressed() {



        if(Login.data_fill_confirmation())
        {
           alertDialog();


        }
        else if(SignUp.data_fill_confirmation())
        {

         alertDialog();
        }
        else
        {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

    }

    private  void alertDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setMessage("Do You Want To Leave This Page").setNegativeButton("LEAVE", (dialogInterface, i) -> {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);

                }).setPositiveButton("STAY", (dialogInterface, i) -> {

                    Log.d(TAG, "onBackPressed: stay pressed");
                });
        builder.setCancelable(false);
        builder.show();

    }

}