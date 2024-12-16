package edu.brown.cs.student.main.server.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GoogleCloudStorageUtilities {

  public String bucketName;

  public GoogleCloudStorageUtilities() throws IOException {
    this.bucketName = "buy-at-brown-listing-images";
  }

  /**
   * Makes a Storage client with credentials to access the GoogleCloudStorage
   *
   * @return Storage client with credentials to access the GoogleCloudStorage
   */
  public Storage makeStorage() throws IOException {
    String workingDirectory = System.getProperty("user.dir");
    Path googleCredentialsPath =
        Paths.get(workingDirectory, "server/resources", "google_cred.json");
    // Initialize the Storage client with credentials
    return StorageOptions.newBuilder()
        .setCredentials(
            ServiceAccountCredentials.fromStream(
                new FileInputStream(String.valueOf(googleCredentialsPath))))
        .build()
        .getService();
  }
}
