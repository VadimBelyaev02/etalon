package com.andersenlab.etalon.infoservice.mapper;

import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlMapperHelper {

  @Value("${aws.s3.bucket.image-country-code-url}")
  String imageBaseUrl;

  @Named("mapImageUrl")
  protected String mapImageUrl(String imageKey) {
    return imageBaseUrl + imageKey;
  }
}
