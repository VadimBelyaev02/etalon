package authorizers.entity;

import authorizers.enums.Scope;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiInfo {
  private Scope scope;
  private String method;
  private String service;
  private String resource;
  private String arn;
}
