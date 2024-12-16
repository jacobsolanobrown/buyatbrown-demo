package edu.brown.cs.student.main.server.storage;

import java.io.IOException;
import java.util.*;

/**
 * This class is a mock implementation of Google Cloud Storage utilities. It simulates operations
 * for testing purposes without connecting to the actual Google Cloud Storage API.
 */
public class MockedGoogleCloudStorageUtilities extends GoogleCloudStorageUtilities {

  // Map to simulate a bucket storage structure: bucketName -> objectName -> objectData
  private final Map<String, Map<String, byte[]>> storage = new HashMap<>();

  public MockedGoogleCloudStorageUtilities() throws IOException {}

  /**
   * Simulates uploading an object to a bucket.
   *
   * @param bucketName The name of the bucket.
   * @param objectName The name of the object.
   * @param data The byte array representing the object data.
   */
  public void uploadObject(String bucketName, String objectName, byte[] data) {
    if (bucketName == null || objectName == null || data == null) {
      throw new IllegalArgumentException("Bucket name, object name, and data cannot be null.");
    }

    // Simulate storing the object in the bucket
    storage
        .computeIfAbsent(bucketName, k -> new HashMap<>())
        .put(objectName, Arrays.copyOf(data, data.length));
  }

  /**
   * Simulates downloading an object from a bucket.
   *
   * @param bucketName The name of the bucket.
   * @param objectName The name of the object.
   * @return The byte array representing the object data, or null if the object doesn't exist.
   */
  public byte[] downloadObject(String bucketName, String objectName) {
    if (bucketName == null || objectName == null) {
      throw new IllegalArgumentException("Bucket name and object name cannot be null.");
    }

    // Simulate fetching the object data
    Map<String, byte[]> bucket = storage.get(bucketName);
    if (bucket == null) {
      return null;
    }
    return bucket.get(objectName);
  }

  /**
   * Simulates deleting an object from a bucket.
   *
   * @param bucketName The name of the bucket.
   * @param objectName The name of the object.
   * @return True if the object was successfully deleted, false if it didn't exist.
   */
  public boolean deleteObject(String bucketName, String objectName) {
    if (bucketName == null || objectName == null) {
      throw new IllegalArgumentException("Bucket name and object name cannot be null.");
    }

    // Simulate deleting the object
    Map<String, byte[]> bucket = storage.get(bucketName);
    if (bucket != null) {
      return bucket.remove(objectName) != null;
    }
    return false;
  }

  /**
   * Simulates generating a signed URL for an object in a bucket.
   *
   * @param bucketName The name of the bucket.
   * @param objectName The name of the object.
   * @return A dummy signed URL.
   */
  public String generateSignedUrl(String bucketName, String objectName) {
    if (bucketName == null || objectName == null) {
      throw new IllegalArgumentException("Bucket name and object name cannot be null.");
    }

    // Simulate generating a signed URL
    return String.format("https://mocked-storage/%s/%s", bucketName, objectName);
  }

  /**
   * Simulates listing all objects in a bucket.
   *
   * @param bucketName The name of the bucket.
   * @return A list of object names in the bucket, or an empty list if the bucket doesn't exist.
   */
  public List<String> listObjects(String bucketName) {
    if (bucketName == null) {
      throw new IllegalArgumentException("Bucket name cannot be null.");
    }

    // Simulate listing all objects in the bucket
    Map<String, byte[]> bucket = storage.get(bucketName);
    if (bucket == null) {
      return Collections.emptyList();
    }
    return new ArrayList<>(bucket.keySet());
  }

  // Optional: Add other methods to simulate additional Google Cloud Storage operations as needed
}
