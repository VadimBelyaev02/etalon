package com.andersenlab.etalon.infoservice.service;

import java.io.IOException;

public interface S3Service {

  byte[] downloadFile(String key) throws IOException;
}
