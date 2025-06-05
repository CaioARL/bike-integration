package br.edu.ifsp.spo.bike_integration.service.aws;

import software.amazon.awssdk.awscore.AwsResponse;

public interface AwsService {

    default boolean isStatusCode(AwsResponse response, int statusCode) {
        return response != null && response.sdkHttpResponse() != null && statusCode > 0
                && response.sdkHttpResponse().statusCode() == statusCode;
    }

    default boolean isSuccess(AwsResponse response) {
        return response != null && response.sdkHttpResponse() != null && response.sdkHttpResponse().isSuccessful();
    }

}