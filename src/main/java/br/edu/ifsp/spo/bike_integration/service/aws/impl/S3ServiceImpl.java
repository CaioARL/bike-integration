package br.edu.ifsp.spo.bike_integration.service.aws.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.ifsp.spo.bike_integration.exception.BikeIntegrationCustomException;
import br.edu.ifsp.spo.bike_integration.service.aws.S3Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    private final S3Client client;

    public S3ServiceImpl(final S3Client client) {
        this.client = client;
    }

    @Override
    public <T> T get(GetObjectRequest request, ResponseTransformer<GetObjectResponse, T> transfomer)
            throws BikeIntegrationCustomException {
        try {
            return this.client.getObject(request, transfomer);
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

    @Override
    public ResponseInputStream<GetObjectResponse> get(GetObjectRequest request) throws BikeIntegrationCustomException {
        try {
            return this.client.getObject(request);
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

    @Override
    public PutObjectResponse put(PutObjectRequest request, RequestBody requestBody)
            throws BikeIntegrationCustomException {
        try {
            return this.client.putObject(request, requestBody);
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

    @Override
    public HeadObjectResponse getHead(HeadObjectRequest request) throws BikeIntegrationCustomException {
        try {
            return this.client.headObject(request);
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

    @Override
    public String getUrl(String key) throws BikeIntegrationCustomException {
        try {
            return this.client
                    .utilities()
                    .getUrl(builder -> builder.bucket(bucket)
                            .key(key))
                    .toExternalForm();
        } catch (Exception e) {
            throw new BikeIntegrationCustomException(e);
        }
    }

}