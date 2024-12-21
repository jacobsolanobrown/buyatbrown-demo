package edu.brown.cs.student.main.server.storage;

import com.google.cloud.storage.Storage;
import java.util.HashMap;
import java.util.Map;

// Mock class
public class MockedGoogleCloudStorageUtilities implements GoogleCloudStorageInterface {

  // A Map representing the gcs bucket with the images
  private final Map<String, String> storage;

  // mock accessing gcsStorage using imageUrls
  private final Map<String, String> imageUrls;

  public MockedGoogleCloudStorageUtilities() {
    this.storage = new HashMap<>();
    this.imageUrls = new HashMap<>();
  }

  @Override
  public Storage makeStorage() {
    // Return null or a mocked Storage object since this is for testing.
    return null;
  }

  @Override
  public String uploadImageToGCS(String image, String imageName) {
    // Store the image bytes in the fake storage
    storage.put(imageName, image);

    // Generate a fake URL for the uploaded image
    String fakeUrl = "http://mock-storage/" + imageName;
    imageUrls.put(imageName, fakeUrl);

    System.out.println("Image " + imageName + " uploaded to mock storage successfully!");
    return fakeUrl;
  }

  public String getImage(String imageName) {
    // Retrieve image bytes from mock storage
    if (!storage.containsKey(imageName)) {
      throw new IllegalArgumentException(
          "Image with name " + imageName + " does not exist in mock storage.");
    }
    return storage.get(imageName);
  }

  public String getImageUrl(String imageName) {
    // Retrieve fake image URL
    if (!imageUrls.containsKey(imageName)) {
      throw new IllegalArgumentException(
          "Image URL for " + imageName + " does not exist in mock storage.");
    }
    return imageUrls.get(imageName);
  }

  public void clearStorage() {
    // Clear all mock data
    storage.clear();
    imageUrls.clear();
    System.out.println("Mock storage cleared.");
  }
}
