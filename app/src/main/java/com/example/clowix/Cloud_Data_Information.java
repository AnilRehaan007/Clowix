package com.example.clowix;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Cloud_Data_Information implements Serializable {

   private  String tag,download_uri,file_name,uploading_date,file_size,random_key_generator,rename_file_name,shared_status;
   private long file_byte_count;


   enum Field_name
   {
       tag,download_uri,file_name,uploading_date,file_size,random_key_generator,file_byte_count,rename_file_name,shared_status,
   }
    public Cloud_Data_Information(String tag, String download_uri, String file_name, String uploading_date,
                                  String file_size, String random_key_generator, long file_byte_count,String rename_file_name,String shared_status) {
        this.tag = tag;
        this.download_uri = download_uri;
        this.file_name = file_name;
        this.uploading_date = uploading_date;
        this.file_size = file_size;
        this.random_key_generator = random_key_generator;
        this.file_byte_count = file_byte_count;
        this.rename_file_name=rename_file_name;
        this.shared_status=shared_status;
    }

    public Cloud_Data_Information() {
    }

    public String getTag() {
        return tag;
    }

    public String getDownload_uri() {
        return download_uri;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getUploading_date() {
        return uploading_date;
    }

    public String getFile_size() {
        return file_size;
    }

    public long getFile_byte_count() {
        return file_byte_count;
    }

    public String getRandom_key_generator() {
        return random_key_generator;
    }

    public String getShared_status() {
        return shared_status;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Cloud_Data_Information)
        {

            Cloud_Data_Information info=(Cloud_Data_Information) obj;

            return this.getRandom_key_generator().equals(info.getRandom_key_generator());
        }
        return false;
    }

    public String getRename_file_name() {
        return rename_file_name;
    }
}
