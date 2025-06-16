package com.nextroom.app.web.service.impl;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.nextroom.app.web.service.CloudStorageService;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.nextroom.app.common.constants.Constants.FOLDER_PATH;
import static com.nextroom.app.common.constants.Constants.GCP_BUCKET_NAME;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {

    Storage storage = StorageOptions.getDefaultInstance().getService();

    public String generateSignedUrl(String fileName) {
        String filePath = String.format("%s%s", FOLDER_PATH, fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(GCP_BUCKET_NAME, filePath).build();
        // Generate the signed URL (expires in 15 minutes)
        URL signedUrl = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

        return signedUrl.toString();
    }
}
