package br.edu.ifsp.spo.bike_integration.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.endpoint}")
    private String endpoint;

    @Bean
    public S3Client s3Client(AwsCredentialsProvider awsCredentialsProvider) {
        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(awsCredentialsProvider)
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

}