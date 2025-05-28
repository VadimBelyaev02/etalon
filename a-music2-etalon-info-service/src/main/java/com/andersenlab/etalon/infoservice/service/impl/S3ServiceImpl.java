package com.andersenlab.etalon.infoservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.andersenlab.etalon.infoservice.service.S3Service;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3ServiceImpl implements S3Service {
  @Value("${aws.s3.bucket.name}")
  private String bucketName;

  private final AmazonS3 amazonS3;

  @Autowired
  public S3ServiceImpl(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  @Override
  public byte[] downloadFile(String fileName) throws IOException {
    GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
    S3Object s3Object = amazonS3.getObject(getObjectRequest);
    return s3Object.getObjectContent().readAllBytes();
  }
}
