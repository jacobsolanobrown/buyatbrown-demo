package edu.brown.cs.student.main.server.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface CloudInterface {
  void uploadObject(String bucketName, String objectName, byte[] data);
  byte[] downloadObject(String bucketName, String objectName);
  boolean deleteObject(String bucketName, String objectName);
  String generateSignedUrl(String bucketName, String objectName);
  List<String> listObjects (String bucketName);

  }