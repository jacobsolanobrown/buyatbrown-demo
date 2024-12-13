package edu.brown.cs.student.main.server.handlers.listingHandlers;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for adding a listing to the database */
public class AddListingHandler implements Route {

  public StorageInterface storageHandler;
  private static final String BUCKET_NAME = "buy-at-brown-listing-images";

  private String uploadImageToGCS(String base64Image, String imageName) throws Exception {
//    System.out.println("uploading image to Google Cloud Storage...");
//    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
//    Storage storage = StorageOptions.getDefaultInstance().getService();
//    BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
//    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCredentials(ServiceAccountCredentials.fromStream(
//      new FileInputStream("/path/to/my/key.json"))).setContentType("image/jpeg").build();
//    System.out.println("Connecting to storage...");
//    storage.create(blobInfo, imageBytes);
//    System.out.println("image built");
//    return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, imageName);
    System.out.println("Uploading image to Google Cloud Storage...");
    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

    String workingDirectory = System.getProperty("user.dir");
    Path googleCredentialsPath =
      Paths.get(workingDirectory, "/resources", "google_cred.json");
    // Initialize the Storage client with credentials
    Storage storage = StorageOptions.newBuilder()
      .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(
        String.valueOf(googleCredentialsPath))))
      .build()
      .getService();

    // Build the BlobInfo
    BlobId blobId = BlobId.of(BUCKET_NAME, imageName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
      .setContentType("image/jpeg")
      .build();

    // Upload the image
    System.out.println("Connecting to storage...");
    storage.create(blobInfo, imageBytes);
    System.out.println("Image uploaded successfully!");

    // Return the public URL
    return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, imageName);
  }

  public AddListingHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Count the number of words between commas (used to validate filterByTags input (i.e. if the
   * number of words per tag <= 2)
   *
   * @param text A String input of word(s) separated by commas
   * @return An int representing the number of words between commas
   */
  public static int countWordsBetweenCommas(String text) {
    int count = 1; // assume that there is at least one word
    boolean insideComma = false;

    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);

      if (ch == ',') {
        insideComma = !insideComma;
      } else if (Character.isWhitespace(ch) && insideComma) { // if reach space
        count++;
      }
    }

    return count;
  }

  /**
   * Checks for duplicate entries in a String that represents a list of strings (used to validate
   * filterByTags input (i.e. there are no duplicate tags)
   *
   * @param text A String input of word(s) separated by commas
   * @return A Boolean representing if there are duplicateEntries
   */
  public static boolean noDuplicateEntries(String text) {
    HashSet<String> noDuplicateEntries = new HashSet<String>(Arrays.asList(text.split(",")));
    System.out.println(noDuplicateEntries.size());
    ArrayList<String> duplicateEntries = new ArrayList<String>(Arrays.asList(text.split(",")));
    System.out.println(duplicateEntries.size());
    System.out.println(noDuplicateEntries.size() == duplicateEntries.size());
    return (noDuplicateEntries.size() == duplicateEntries.size());
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) {
    response.type("application/json");
    Map<String, Object> responseMap = new HashMap<>();
    try {
      System.out.println("AddListingHandler has received a request.");
      // Collect base64Image from raw body
      String base64Image = request.body();
      String uid = request.queryParams("uid");
      String username = request.queryParams("username");
      String price = request.queryParams("price");
      String title = request.queryParams("title");
      String category = request.queryParams("category");
      String tags = request.queryParams("tags");
      String condition = request.queryParams("condition");
      String description = request.queryParams("description");

      if (uid == null || username == null || price == null || title == null || category == null
        || tags == null || condition == null || description == null || base64Image == null) {
        responseMap.put("response_type", "error");
        responseMap.put("error", "Missing required parameters.");
        return responseMap;
      }

      String listingUUID = UUID.randomUUID().toString();
      String imageName = "listing-" + listingUUID + ".jpg";
      System.out.println("Processing image...");
      String imageUrl = uploadImageToGCS(base64Image, imageName);

      Map<String, Object> data = new HashMap<>();
      data.put("uid", uid);
      data.put("username", username);
      data.put("imageUrl", imageUrl);
      data.put("price", price);
      data.put("title", title);
      data.put("category", category);
      data.put("tags", tags);
      data.put("condition", condition);
      data.put("description", description);

      this.storageHandler.addDocument(uid, "listings", listingUUID, data);

      responseMap.put("response_type", "success");
      responseMap.put("message", "Listing added successfully.");
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "error");
      responseMap.put("error", "An error occurred while processing the request.");
    }
    return responseMap;
  }

}
