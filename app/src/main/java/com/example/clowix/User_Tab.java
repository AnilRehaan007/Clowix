package com.example.clowix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class User_Tab extends Fragment {

    private static final String TAG = "User_Tab";
    private final String user_profile_locker;
    private double total_space_covered;
    private final int network_status;


    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){


               Intent intent=new Intent(getContext(),MainActivity.class);

               startActivity(intent);

            }
            else {

                Log.d(TAG, "onAuthStateChanged: im stayed");
            }
        }
    };
    public User_Tab(int network_status,String user_profile_locker) {
      this.network_status=network_status;
      this.user_profile_locker=user_profile_locker;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user__tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout user_alert=view.findViewById(R.id.user_alert);

        user_alert.setVisibility(View.INVISIBLE);

        if(network_status==Network_Status.TYPE_NOT_CONNECTED)
        {
            DialogShower dialogShower=new DialogShower(R.layout.internet_error,R.style.translate_animator,getContext());
            dialogShower.showDialog();

            user_alert.setVisibility(View.VISIBLE);

        }
        else
        {
            user_alert.setVisibility(View.INVISIBLE);

            Dialog dialog=new Dialog(getContext(),R.style.translate_animator);
            dialog.setContentView(R.layout.user_detail);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            Window window=dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            dialog.show();


            dialog.findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
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
            });

            dialog.findViewById(R.id.minimize).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MainActivity.return_status="user_tab";
                    Intent intent=new Intent(getContext(),MainActivity.class);

                    startActivity(intent);

                }
            });
            
            read_time_access(dialog);
        }

    }

    private  void read_time_access(Dialog dialog)
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                Cloud_Data_Information cloud_data_information=snapshot.getValue(Cloud_Data_Information.class);

                assert cloud_data_information != null;

               total_space_covered+=cloud_data_information.getFile_byte_count();

                ProgressBar progressBar=dialog.findViewById(R.id.progressBar);


                double value=(total_space_covered/SpaceAllocator.initial_space)*100;

                int progress_status=(int)value;

                 progressBar.setProgress(progress_status);

                 long free_space= (long) (SpaceAllocator.initial_space-total_space_covered);

                 TextView set_usage=dialog.findViewById(R.id.set_usage_status);
                 TextView set_email=dialog.findViewById(R.id.set_email);
                 String output="%s free of %s";
                 set_usage.setText(String.format(output,Formatter.formatFileSize(getContext(),
                         free_space),Formatter.formatFileSize(getContext(),SpaceAllocator.initial_space)));
                 set_email.setText(user_profile_locker);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: ");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }
}