package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.andersenlab.etalon.infoservice.config.S3TestConfig;
import com.andersenlab.etalon.infoservice.service.impl.S3ServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@Import(S3TestConfig.class)
class S3ServiceImplTest {

  @Mock private AmazonS3 amazonS3;

  @InjectMocks private S3ServiceImpl s3Service;

  private final String fileName = "test.txt";
  private final byte[] fileContent = "Mocked file content".getBytes();

  @BeforeEach
  void setUp() {
    String bucketName = "test-bucket";
    ReflectionTestUtils.setField(s3Service, "bucketName", bucketName);
  }

  @Test
  void testDownloadFile() throws IOException {
    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new ByteArrayInputStream(fileContent));

    when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

    byte[] result = s3Service.downloadFile(fileName);

    assertArrayEquals(fileContent, result);

    verify(amazonS3, times(1)).getObject(any(GetObjectRequest.class));
  }

  @Test
  void testDownloadFile_FileNotFound() {
    when(amazonS3.getObject(any(GetObjectRequest.class)))
        .thenThrow(new AmazonS3Exception("File not found"));

    AmazonS3Exception thrown =
        assertThrows(AmazonS3Exception.class, () -> s3Service.downloadFile(fileName));
    assertEquals("File not found", thrown.getErrorMessage());

    verify(amazonS3, times(1)).getObject(any(GetObjectRequest.class));
  }

  @Test
  void testDownloadFile_EmptyFile() throws IOException {
    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(new ByteArrayInputStream(new byte[0]));

    when(amazonS3.getObject(any(GetObjectRequest.class))).thenReturn(s3Object);

    byte[] result = s3Service.downloadFile(fileName);

    assertNotNull(result);
    assertEquals(0, result.length);
    verify(amazonS3, times(1)).getObject(any(GetObjectRequest.class));
  }
}
