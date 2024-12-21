package edu.brown.cs.student.main.server.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class GoogleCloudStorageUtilities implements GoogleCloudStorageInterface {

  public String bucketName;

  public GoogleCloudStorageUtilities() throws IOException {
    this.bucketName = "buy-at-brown-listing-images";
  }

  /**
   * Makes a Storage client with credentials to access the GoogleCloudStorage
   *
   * @return Storage client with credentials to access the GoogleCloudStorage
   */
  @Override
  public Storage makeStorage() throws IOException {
    String workingDirectory = System.getProperty("user.dir");
    Path googleCredentialsPath = Paths.get(workingDirectory, "/resources", "google_cred.json");
    // Initialize the Storage client with credentials
    return StorageOptions.newBuilder()
        .setCredentials(
            ServiceAccountCredentials.fromStream(
                new FileInputStream(String.valueOf(googleCredentialsPath))))
        .build()
        .getService();
  }

  /**
   * Uploads a base64 image to Google Cloud Storage (GCS)
   *
   * @param base64Image A string that represents the base64 encoding of an image
   * @param imageName A string that represents the name of an image
   * @return An imageUrl to where the image was stored in GCS
   */
  @Override
  public String uploadImageToGCS(String base64Image, String imageName) throws IOException {
    System.out.println("Uploading image to Google Cloud Storage...");
    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

    // Build the BlobInfo
    BlobId blobId = BlobId.of(bucketName, imageName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();

    // Upload the image
    Storage gcsStorage = makeStorage();
    gcsStorage.create(blobInfo, imageBytes);
    System.out.println("Image uploaded successfully!");

    URL signedUrl =
        gcsStorage.signUrl(blobInfo, 365, TimeUnit.DAYS, Storage.SignUrlOption.withV2Signature());
    return signedUrl.toString();
  }
}
