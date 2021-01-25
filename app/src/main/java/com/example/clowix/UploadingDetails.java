package com.example.clowix;

import java.io.Serializable;

public class UploadingDetails implements Serializable {
    private final String user_profile_locker;
    private final String upload_uri;
    private final String file_type;
    private final String file_name;

    public UploadingDetails(String user_profile_locker, String upload_uri, String file_type, String file_name) {
        this.user_profile_locker = user_profile_locker;
        this.upload_uri = upload_uri;
        this.file_type = file_type;
        this.file_name = file_name;
    }

    public String getUser_profile_locker() {
        return user_profile_locker;
    }

    public String getUpload_uri() {
        return upload_uri;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_name() {
        return file_name;
    }
}
