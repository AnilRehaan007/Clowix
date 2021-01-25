package com.example.clowix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdaptor extends RecyclerView.Adapter<RecycleViewAdaptor.Holder> implements Filterable {

    private static final String TAG = "RecycleViewAdaptor";
    private List<Cloud_Data_Information> cloud_data_information;
    private List<Cloud_Data_Information> using_for_filter;
    public static List<Cloud_Data_Information> unfilteredList;
    private final Context context;
    private final String user_locker_key;

    public RecycleViewAdaptor(List<Cloud_Data_Information> cloud_data_information, Context context,String user_locker_key) {
        this.cloud_data_information = cloud_data_information;
        this.using_for_filter=cloud_data_information;
        this.context = context;
        this.user_locker_key=user_locker_key;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.files_shower,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.videoView.setVisibility(View.VISIBLE);
        holder.image_shower.setVisibility(View.VISIBLE);


         if(cloud_data_information.get(position).getRename_file_name().equals(cloud_data_information.get(position).getFile_name()))
         {

             holder.data_name.setText(cloud_data_information.get(position).getFile_name());
         }
         else
         {
             holder.data_name.setText(cloud_data_information.get(position).getRename_file_name());
         }

        holder.data_information.setText(cloud_data_information.get(position).getFile_size());

        holder.date_information.setText(cloud_data_information.get(position).getUploading_date());

        if(cloud_data_information.get(position).getTag().equals(MainFrame.File_Type.Image.toString()))
        {
           holder.videoView.setVisibility(View.INVISIBLE);

            Picasso.with(context).load(Uri.parse(cloud_data_information.get(position).getDownload_uri())).error(R.drawable.error_image).into(holder.image_shower);
        }
        else if(cloud_data_information.get(position).getTag().equals(MainFrame.File_Type.Document.toString()))
        {
            holder.videoView.setVisibility(View.INVISIBLE);

            Picasso.with(context).load(R.drawable.document).into(holder.image_shower);
        }
        else if(cloud_data_information.get(position).getTag().equals(MainFrame.File_Type.Audio.toString()))
        {
            holder.videoView.setVisibility(View.INVISIBLE);

            Picasso.with(context).load(R.drawable.music).into(holder.image_shower);
        }
        else if(cloud_data_information.get(position).getTag().equals(MainFrame.File_Type.Video.toString()))
        {
            holder.image_shower.setVisibility(View.INVISIBLE);

            Glide.with(context).load("empty")
                    .thumbnail(Glide.with(context).load(cloud_data_information.get(position).getDownload_uri()))
                    .into(holder.videoView);
        }

        holder.side_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                side_handler(position);

            }
        });

    }

    public final void setData(List<Cloud_Data_Information> cloud_data)
    {

       this.cloud_data_information=cloud_data;
       this.using_for_filter=cloud_data;

       notifyDataSetChanged();

    }
    @Override
    public int getItemCount() {
        return cloud_data_information!=null && cloud_data_information.size()>0 ?cloud_data_information.size():0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Cloud_Data_Information> filter=new ArrayList<>();
                if(charSequence==null || charSequence.length()==0)
                {

                    filter.addAll(unfilteredList);

                }
                else
                {

                    String value=charSequence.toString().toLowerCase().trim();

                    for(Cloud_Data_Information cloud_data_information:using_for_filter)
                    {
                       if(cloud_data_information.getFile_name().toLowerCase().contains(value)
                               || cloud_data_information.getRename_file_name().toLowerCase().contains(value))
                       {

                           filter.add(cloud_data_information);

                       }
                    }


                }

                FilterResults filterResults=new FilterResults();
                filterResults.values=filter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                cloud_data_information.clear();
                cloud_data_information.addAll((List)filterResults.values);
                notifyDataSetChanged();
            }
        };
    }


    static class Holder extends RecyclerView.ViewHolder
    {
        ImageView image_shower,side_click,videoView;
        TextView data_name,data_information,date_information;


        public Holder(@NonNull View itemView) {
            super(itemView);

           videoView=itemView.findViewById(R.id.vedio_view);
           image_shower=itemView.findViewById(R.id.image_shower);
           side_click=itemView.findViewById(R.id.sider_click);
           data_name=itemView.findViewById(R.id.data_name);
           data_information=itemView.findViewById(R.id.data_information);
           date_information=itemView.findViewById(R.id.date_information);
        }

    }

    public void side_handler(int position)
    {
        Dialog download_show_box=new Dialog(context,R.style.translate_animator);
        download_show_box.setContentView(R.layout.side_options);
        download_show_box.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        download_show_box.setCanceledOnTouchOutside(true);
        Window window=download_show_box.getWindow();
        window.setGravity(Gravity.END);
        download_show_box.show();



        download_show_box.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                download_show_box.dismiss();

                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setMessage("Are You Sure You Want To Delete\n\n"
                                +cloud_data_information.get(position).getFile_name()).setNegativeButton("CANCEL", (dialogInterface, i) -> {

                        }).setPositiveButton("DELETE", (dialogInterface, i) -> {


                            Intent intent=new Intent(context,Side_Options_Service.class);
                            Side_Options_Service.context=context;
                            intent.putExtra(Side_Options_Service.status_name,SideOperator.SideOperatorStatus.Delete);
                            intent.putExtra(Side_Options_Service.cloud_data_information_key,cloud_data_information.get(position));
                            intent.putExtra(Side_Options_Service.user_locker_key,user_locker_key);
                            context.startService(intent);


                        });
                builder.setCancelable(false);
                builder.show();
            }
        });

        download_show_box.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download_show_box.dismiss();

                Intent intent=new Intent(context,Side_Options_Service.class);
                Side_Options_Service.context=context;
                intent.putExtra(Side_Options_Service.status_name,SideOperator.SideOperatorStatus.Share);
                intent.putExtra(Side_Options_Service.cloud_data_information_key,cloud_data_information.get(position));
                intent.putExtra(Side_Options_Service.user_locker_key,user_locker_key);
                context.startService(intent);

            }
        });

        download_show_box.findViewById(R.id.downloading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download_show_box.dismiss();
                Intent intent=new Intent(context,Side_Options_Service.class);
                Side_Options_Service.context=context;
                intent.putExtra(Side_Options_Service.status_name,SideOperator.SideOperatorStatus.Save);
                intent.putExtra(Side_Options_Service.cloud_data_information_key,cloud_data_information.get(position));
                intent.putExtra(Side_Options_Service.user_locker_key,user_locker_key);
                context.startService(intent);
            }
        });

        download_show_box.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download_show_box.dismiss();

                Intent intent=new Intent(context,Side_Options_Service.class);
                Side_Options_Service.context=context;
                intent.putExtra(Side_Options_Service.status_name,SideOperator.SideOperatorStatus.Rename);
                intent.putExtra(Side_Options_Service.cloud_data_information_key,cloud_data_information.get(position));
                intent.putExtra(Side_Options_Service.user_locker_key,user_locker_key);
                context.startService(intent);

            }
        });

    }
}
