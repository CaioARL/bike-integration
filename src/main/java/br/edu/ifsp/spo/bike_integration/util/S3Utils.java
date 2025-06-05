package br.edu.ifsp.spo.bike_integration.util;

import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public interface S3Utils {

    /**
     * Create S3Key for the given file name and user ID.
     *
     * @param entity the entity name (e.g., "usuario", "evento", "problema")
     * @param id     the ID of the entity (e.g., user ID, event ID, problem ID)
     * @param file   the MultipartFile to be uploaded
     * @return the S3Key as a String
     */
    static String createS3Key(String entity, String id, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String[] splitFileName = fileName.split("\\.");
        String extension = splitFileName[splitFileName.length - 1];
        return entity + "/" + id + "/image" + "." + extension;
    }

    static PutObjectRequest createRestPutObjectRequest(String bucketName, String s3Key) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("image/jpeg")
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
    }

}
