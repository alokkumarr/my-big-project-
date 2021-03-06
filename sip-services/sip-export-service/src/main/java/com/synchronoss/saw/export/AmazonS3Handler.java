package com.synchronoss.saw.export;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.synchronoss.sip.utils.SipCommonUtils;
import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.export.model.S3.S3Customer;
import com.synchronoss.saw.export.model.S3.S3Details;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonS3Handler {
  private final String bucket;
  private String region;

  private final String outputLocation;

  private final String accessKey;
  private final String secretKey;
  private final AmazonS3 s3Client;
  private final String cannedAcl;
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
    cannedAcl = config.getCannedAcl();

    AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

    s3Client =
        AmazonS3ClientBuilder.standard()
            .withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
  }

  public void uploadObject(File file) {
    DateTime currentTime = new DateTime();

    String currentFileName = file.getName();

    // Assumption is there will be extension for the file
    String extension = currentFileName.substring(currentFileName.lastIndexOf('.') + 1);

    String fileName = currentFileName.substring(0, currentFileName.lastIndexOf('.'));

    String timeStampStr = currentTime.toString("yyyy-MM-dd_HH:mm:ss.SSS");
    String finalFileName = fileName + "_" + timeStampStr + "." + extension;

    String s3Key = SipCommonUtils.normalizePath(outputLocation + "/" + finalFileName);

    logger.debug("Upload location = " + s3Key);

    PutObjectRequest request = new PutObjectRequest(bucket, s3Key, file);
    putObject(s3Client, request);
  }

  public void uploadObject(File file, ObjectMetadata metadata) {
    PutObjectRequest request = new PutObjectRequest(bucket, outputLocation, file);
    request.setMetadata(metadata);
    putObject(s3Client, request);
  }

  public void putObject(AmazonS3 s3, PutObjectRequest request) {
    try {
      logger.debug("S3 Bucket " + request.getBucketName());
      logger.debug("File = " + request.getFile().getName());
      if (cannedAcl != null && !cannedAcl.isEmpty()) {
        CannedAccessControlList cannedAccessControlList =
            CannedAccessControlList.valueOf(cannedAcl);
        if (cannedAccessControlList != null) {
          switch (cannedAccessControlList) {
            case BucketOwnerFullControl:
              request.withCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
              break;
            case PublicReadWrite:
              request.withCannedAcl(CannedAccessControlList.PublicReadWrite);
              break;
            case LogDeliveryWrite:
              request.withCannedAcl(CannedAccessControlList.LogDeliveryWrite);
            default:
              // Default access control policy for any new buckets or objects
              request.withCannedAcl(CannedAccessControlList.Private);
          }
        }
      }
      s3.putObject(request);
      logger.info("Success uploading to S3");
    } catch (AmazonServiceException e) {
      // The call was transmitted successfully, but Amazon S3 couldn't process
      // it, so it returned an error response.
      logger.error(
          "AmazonServiceException : The call was transmitted successfully,"
              + " but Amazon S3 couldn't process",
          e);
    } catch (SdkClientException e) {
      // Amazon S3 couldn't be contacted for a response, or the client
      // couldn't parse the response from Amazon S3.
      logger.error(
          "SdkClientException : Amazon S3 couldn't be contacted for a response," + " or the client",
          e);
    } catch (Exception e) {
      logger.error("Error dispatching to S3 : ", e);
    }
  }

}
