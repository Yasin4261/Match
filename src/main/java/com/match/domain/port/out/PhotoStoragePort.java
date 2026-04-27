package com.match.domain.port.out;

public interface PhotoStoragePort {
    /** Returns the stored object key. */
    String upload(String objectKey, byte[] bytes, String contentType);
    /** Returns a short-lived URL clients can use to GET the photo. */
    String presignedGetUrl(String objectKey, int expirySeconds);
    void delete(String objectKey);
}

