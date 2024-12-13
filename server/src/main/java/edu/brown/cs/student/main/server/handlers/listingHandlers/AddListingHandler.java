package edu.brown.cs.student.main.server.handlers.listingHandlers;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

// import org.
/** Class for adding a listing to the database */
public class AddListingHandler implements Route {

  public StorageInterface storageHandler;
  private static final String BUCKET_NAME = "buy-at-brown-listing-images";

  public AddListingHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Uploads a base64 image to Google Cloud Storage (GCS)
   *
   * @param base64Image A string that represents the base64 encoding of an image
   * @param imageName A string that represents the name of an image
   * @return An imageUrl to where the image was stored in GCS
   */
  private String uploadImageToGCS(String base64Image, String imageName) throws Exception {
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
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // collect parameters from the request
      String uid = request.queryParams("uid");
      String username = request.queryParams("username");
//      String imageUrl = request.queryParams("imageUrl");
      String base64Image = request.body();
      String price = request.queryParams("price");
      String title = request.queryParams("title");
      String category = request.queryParams("category");
      String tags = request.queryParams("tags");
      String condition = request.queryParams("condition");
      String description = request.queryParams("description");


      // create new listing with collected parameters
      // Listing listing = new Listing(username, title, imageUrl, price, description);

      Map<String, Object> data = new HashMap<>();
      System.out.println("Validating parameter values for search");
      if (uid == null
        || username == null
        || base64Image == null
        || price == null
        || condition == null
        || description == null
        || title == null
        || tags == null
        || category == null) {
        System.out.println(
          "All listings arguments are required "
            + "(uid, username, title, tags, price, imageUrl, category, condition, description)");
        throw new IllegalArgumentException(
          "All listings arguments are required "
            + "(uid, username, title, tags, price, imageUrl, category, condition, description)");
      }

      // check if title is less than 40 characters
      if (title.length() > 40) {
        System.out.println("Title must be less than or equal to 40 characters");
        throw new IllegalArgumentException("Title must be less than or equal to 40 characters");
      }

      // check if price is negative value
      if (Double.parseDouble(price) < 0) {
        System.out.println("Price cannot be negative");
        throw new IllegalArgumentException("Price cannot be negative");
      }

      // check if category option is one of the valid options
      condition = condition.toLowerCase();
      if (!(condition.equals("new") || condition.equals("like new") || condition.equals("used"))) {
        System.out.println(
          "Please choose from valid condition inputs (i.e. new, like new, or used");
        throw new IllegalArgumentException(
          "Please choose from valid condition inputs (i.e. New, Like New, or Used");
      }

      if (category.contains(",")) {
        System.out.println("Category can only have one value (i.e.value  must not contain commas)");
        throw new IllegalArgumentException(
          "Category can only have one value (i.e.value  must not contain commas)");
      }

      // there should be no extra spaces and  tags are in the form "tag1,tag2,tag3, two wordtag"
      if (tags.length() - tags.replace("  ", "").replace(" ,", ",").replace(", ", ",").length()
        > 0) {
        System.out.println(
          "Each tag should only have ONE space between words and non before and after commas");
        throw new IllegalArgumentException(
          "Each tag should only have ONE space between words and non before and after commas");
      }

      if (countWordsBetweenCommas(tags) > 2) {
        System.out.println("Each tag should be less than or equal to 2 words");
        throw new IllegalArgumentException("Each tag should be less than or equal to 2 words");
      }

      // tags are in the form "tag1,tag2,tag3, two wordtag"
      if (tags.length() - tags.replace(",,", "").replace(", ,", "").length() > 0) {
        System.out.println("Each tag should have a value");
        throw new IllegalArgumentException("Each tag should have a value");
      }

      // if there are more than 5 tags, error
      if (tags.length() - tags.replace(",", "").length() > 4) {
        System.out.println("Please input less than or equal to 5 tags");
        throw new IllegalArgumentException("Please input less than or equal to 5 tags");
      }

      // if there are repeated tags, error
      if (!noDuplicateEntries(tags)) {
        System.out.println("Please make sure all tags are unique");
        throw new IllegalArgumentException("Please make sure all tags are unique");
      }

      String listingUUID = UUID.randomUUID().toString();
      String imageName = "listing-" + listingUUID + ".jpg";
      System.out.println("Processing image...");
      String imageUrl = uploadImageToGCS(base64Image, imageName);

      System.out.println("Valid inputs recieved");
      data.put("uid", uid);
      data.put("username", username);
      data.put("imageUrl", imageUrl);
      data.put("tags", tags);
      data.put("condition", condition);
      data.put("price", price);
      data.put("title", title);
      data.put("description", description);

      String listingId = "listing-" + listingUUID;

      // WHAT IF WE DELEte LISTING1 OUT OF 5 LISTINGS? THEN LISTING COUNT IS LOWER BUT
      // THERE ARE STILL 5 LISTINGS?
      // INSTEAD... IS THERE A WAY TO ATOMICALLY ADD TO THE LISTING WITHOUT HAVINF DUPLICATES?

      // use the storage handler to add the document to the database
      this.storageHandler.addDocument(uid, "listings", listingId, data);

      System.out.println(
        "addded listing for username: "
          + username
          + ", title: "
          + condition
          + ", tags: "
          + tags
          + ", imageUrl: "
          + imageUrl
          + ", category"
          + category
          + ", price: "
          + price
          + ", description: "
          + description
          + ", for user: "
          + uid);

      responseMap.put("response_type", "success");
      responseMap.putAll(data);
      responseMap.put("listingId", listingId);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}