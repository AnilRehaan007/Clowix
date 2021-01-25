package com.example.clowix;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Pictures_tab extends Fragment {

    private static final String TAG = "Pictures_tab";
    private List<Cloud_Data_Information> data;
    private CustomBaseAdaptor customBaseAdaptor;
    private final String user_profile_locker;
    private RelativeLayout relativeLayout;
    private final int network_status;

    public Pictures_tab(int network_status,String user_profile_locker) {
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

        return inflater.inflate(R.layout.fragment_pictures_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        relativeLayout=view.findViewById(R.id.user_alert);

        GridView gridView=view.findViewById(R.id.my_view);

        relativeLayout.setVisibility(View.VISIBLE);

        data=new ArrayList<>();

        customBaseAdaptor=new CustomBaseAdaptor(new ArrayList<>(),getContext());

        gridView.setAdapter(customBaseAdaptor);

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        Dialog photo_tab_dialog=new Dialog(getContext(),R.style.translate_animator);
        photo_tab_dialog.setContentView(R.layout.photo_shower_box);
        photo_tab_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        photo_tab_dialog.setCanceledOnTouchOutside(true);
        Window window=photo_tab_dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        photo_tab_dialog.show();

        ImageView photo_shower=photo_tab_dialog.findViewById(R.id.show_image);

        Picasso.with(getContext()).load(Uri.parse(data.get(i).getDownload_uri())).into(photo_shower);

    }
    });

        if(network_status==Network_Status.TYPE_NOT_CONNECTED)
        {
            DialogShower dialogShower=new DialogShower(R.layout.internet_error,R.style.translate_animator,getContext());
            dialogShower.showDialog();
        }
        else
        {
            read_time_access();
        }

    }

    private  void read_time_access()
    {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(user_profile_locker);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                Cloud_Data_Information cloud_data_information=snapshot.getValue(Cloud_Data_Information.class);

                assert cloud_data_information != null;

                if(cloud_data_information.getTag().equals(MainFrame.File_Type.Image.toString()))
                {

                    relativeLayout.setVisibility(View.INVISIBLE);

                    data.add(cloud_data_information);

                }

                customBaseAdaptor.setData(data);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: ");

                Cloud_Data_Information cloud_data_information=snapshot.getValue(Cloud_Data_Information.class);

                data.remove(cloud_data_information);

                customBaseAdaptor.setData(data);
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