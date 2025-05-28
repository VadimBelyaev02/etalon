package com.andersenlab.etalon.depositservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "pagination")
public class PaginationProperties {
  private Integer defaultPageNumber;
  private Integer defaultPageSize;
  private String defaultSortBy;
  private String defaultOrderBy;
}
