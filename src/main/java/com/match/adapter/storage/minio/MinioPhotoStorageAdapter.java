package com.match.adapter.storage.minio;

import com.match.domain.port.out.PhotoStoragePort;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

@Component
public class MinioPhotoStorageAdapter implements PhotoStoragePort {

    private final MinioClient client;
    private final String bucket;

    public MinioPhotoStorageAdapter(MinioClient client, @Value("${app.minio.bucket}") String bucket) {
        this.client = client;
        this.bucket = bucket;
    }

    @Override public String upload(String objectKey, byte[] bytes, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                .bucket(bucket).object(objectKey)
                .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                .contentType(contentType).build());
            return objectKey;
        } catch (Exception e) {
            throw new IllegalStateException("MinIO upload failed", e);
        }
    }

    @Override public String presignedGetUrl(String objectKey, int expirySeconds) {
        try {
            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET).bucket(bucket).object(objectKey)
                .expiry(expirySeconds, TimeUnit.SECONDS).build());
        } catch (Exception e) {
            throw new IllegalStateException("MinIO presign failed", e);
        }
    }

    @Override public void delete(String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
        } catch (Exception e) {
            throw new IllegalStateException("MinIO delete failed", e);
        }
    }
}

