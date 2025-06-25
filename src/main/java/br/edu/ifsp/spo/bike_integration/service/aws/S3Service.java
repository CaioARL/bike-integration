package br.edu.ifsp.spo.bike_integration.service.aws;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface S3Service extends AwsService {

    Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    <T> T get(GetObjectRequest request, ResponseTransformer<GetObjectResponse, T> transfomer)
            throws BikeIntegrationCustomException;

    ResponseInputStream<GetObjectResponse> get(GetObjectRequest request) throws BikeIntegrationCustomException;

    PutObjectResponse put(PutObjectRequest request, RequestBody requestBody) throws BikeIntegrationCustomException;

    HeadObjectResponse getHead(HeadObjectRequest request) throws BikeIntegrationCustomException;

    String getUrl(String key);

    String getKeyFromUrl(String url);

    DeleteObjectResponse delete(String key) throws BikeIntegrationCustomException;

    default HeadObjectResponse getHeadSafe(HeadObjectRequest request) {
        try {
            return this.getHead(request);
        } catch (Exception e) {
            LOGGER.debug("Error on getHeadSafe: ", e);
            return null;
        }
    }

    default byte[] getAsByteArray(GetObjectRequest request) throws BikeIntegrationCustomException {
        try {
            ResponseBytes<?> responseBytes = this.get(request, ResponseTransformer.toBytes());
            return (responseBytes != null) ? responseBytes.asByteArray() : null;
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

    default PutObjectResponse put(PutObjectRequest request, InputStream inputStream)
            throws BikeIntegrationCustomException {
        return this.put(request, inputStream, 0L);
    }

    default PutObjectResponse put(PutObjectRequest request, InputStream inputStream, long contentLength)
            throws BikeIntegrationCustomException {
        try {
            return this.put(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

    default PutObjectResponse put(PutObjectRequest request, byte[] bytes) throws BikeIntegrationCustomException {
        try {
            return this.put(request, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

}