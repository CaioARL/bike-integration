package br.edu.ifsp.spo.bike_integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.common.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

@Configuration
public class AwsConfig {

    @Value("${aws.profile}")
    private String profile;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        AwsCredentialsProvider credentialsProvider;
        if (StringUtils.isBlank(profile) || "default".equals(profile)) {
            credentialsProvider = DefaultCredentialsProvider.create();
        } else {
            credentialsProvider = ProfileCredentialsProvider.create(this.profile);
        }

        return credentialsProvider;
    }
}