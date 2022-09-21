package com.paylist.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import com.amazonaws.regions.Regions;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

@Configuration
public class EmailConfig {
  @Value("${aws_access_key_id}")
  private String awsId;

  @Value("${aws_secret_access_key}")
  private String awsKey;

  @Value("${aws_region}")
  private String region;

  @Bean
  public AmazonSimpleEmailService emailClient() {

    BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);

    AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
        .withRegion(Regions.fromName(region)).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

    return client;
  }
}