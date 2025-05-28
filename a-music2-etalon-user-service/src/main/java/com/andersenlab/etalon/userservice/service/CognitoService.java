package com.andersenlab.etalon.userservice.service;

public interface CognitoService {
  void updateUserEmail(String userId, String newEmail);

  String registerUser(String email, String password);

  boolean userExists(String email);

  boolean updateUserPassword(String email, String newPassword);
}
