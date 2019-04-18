package com.synchronoss.saw.export;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonS3Handler {
  private final String bucket;
  private String region;

  private final String outputLocation;

  private final String accessKey;
  private final String secretKey;
  private final AmazonS3 s3Client;
  private static final Logger logger = LoggerFactory.getLogger(AmazonS3Handler.class);

  public AmazonS3Handler(S3Config config) throws Exception {
    if (config == null) {
      throw new Exception("Invalid configuration");
    }

    if (config.getBucket() == null) {
      throw new Exception("Bucket Name is not specified");
    }

    if (config.getAccessKey() == null) {
      throw new Exception("Access key is not specified");
    }

    if (config.getSecretKey() == null) {
      throw new Exception("Secret key is not specified");
    }

    bucket = config.getBucket();
    accessKey = config.getAccessKey();
    secretKey = config.getSecretKey();
    region = config.getRegion();
    outputLocation = config.getOutputLocation();

    AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

    s3Client =
        AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
  }

  public void uploadObject(File file) {
    PutObjectRequest request = new PutObjectRequest(bucket, outputLocation+"PrabhuTest.txt", file);
    putObject(s3Client, request);
    logger.info("Success uploading to S3");
  }

  public void uploadObject(File file, ObjectMetadata metadata) {
    PutObjectRequest request = new PutObjectRequest(bucket, outputLocation, file);
    request.setMetadata(metadata);
    putObject(s3Client, request);
  }

  public void putObject(AmazonS3 s3, PutObjectRequest request) {
    try {
      s3.putObject(request);
    } catch (AmazonServiceException e) {
      // The call was transmitted successfully, but Amazon S3 couldn't process
      // it, so it returned an error response.
      e.printStackTrace();
    } catch (SdkClientException e) {
      // Amazon S3 couldn't be contacted for a response, or the client
      // couldn't parse the response from Amazon S3.
      e.printStackTrace();
    }
  }
}
