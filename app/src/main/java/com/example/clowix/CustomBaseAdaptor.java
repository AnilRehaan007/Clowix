package com.example.clowix;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomBaseAdaptor extends BaseAdapter {

    private  List<Cloud_Data_Information> cloud_data_information;
    private final Context context;
    private final LayoutInflater layoutInflater;

    public CustomBaseAdaptor(List<Cloud_Data_Information> cloud_data_information, Context context) {
        this.cloud_data_information = cloud_data_information;
        this.context = context;
        this.layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cloud_data_information!=null && cloud_data_information.size()>0 ? cloud_data_information.size():0;
    }

    public final void setData(List<Cloud_Data_Information> cloud_data)
    {

        this.cloud_data_information=cloud_data;

        notifyDataSetChanged();

    }

    @Override
    public Object getItem(int i) {
        return cloud_data_information.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if(view==null)
        {
            view=layoutInflater.inflate(R.layout.grid_view_image_holder,viewGroup,false);
        }

        ImageView imageView=view.findViewById(R.id.grid_view_image);

        Picasso.with(context).load(Uri.parse(cloud_data_information.get(i).getDownload_uri())).error(R.drawable.error_image).into(imageView);

        return imageView;
    }
}
