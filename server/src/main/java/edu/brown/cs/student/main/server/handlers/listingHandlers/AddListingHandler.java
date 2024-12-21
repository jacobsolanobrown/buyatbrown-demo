package edu.brown.cs.student.main.server.handlers.listingHandlers;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.GoogleCloudStorageInterface;
import edu.brown.cs.student.main.server.storage.GoogleCloudStorageUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.sound.midi.SysexMessage;
import spark.Request;
import spark.Response;
import spark.Route;

// import org.
/** Class for adding a listing to the database */
public class AddListingHandler implements Route {

  public StorageInterface storageHandler;
  public GoogleCloudStorageInterface gcsHandler;

  public AddListingHandler(
      StorageInterface storageHandler, GoogleCloudStorageInterface gcsHandler) {
    this.storageHandler = storageHandler;
    this.gcsHandler = gcsHandler;
  }

  /**
   * Count the number of words between commas (used to validate filterByTags input (i.e. if the
   * number of words per tag <= 2)
   *
   * @param text A String input of word(s) separated by commas
   * @return An int representing the number of words between commas
   */
  public static Integer countWordsBetweenCommas(String text) {
    // Split the string by commas
    String[] parts = text.split(",");
    ArrayList<Integer> totalWords = new ArrayList<>();
    int partWords = 0;

    for (String part : parts) {

      String[] words = part.trim().split("\\s+");
      int len_words = words.length;
      totalWords.add(len_words);
    }
    // Return the maximum count from the list
    return Collections.max(totalWords);
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
    ArrayList<String> duplicateEntries = new ArrayList<String>(Arrays.asList(text.split(",")));
    return (noDuplicateEntries.size() == duplicateEntries.size());
  }

  /**
   * Check if the input for the uid and username field is valid
   *
   * @param uid A String input of the uid
   * @param username A String input of the username
   */
  public void validateUser(String uid, String username)
    throws ExecutionException, InterruptedException {
    if (uid == null || uid.isBlank()  || username == null || username.isBlank()) {
      System.out.println("Please specify user. Input both the UID and Username.");
      throw new IllegalArgumentException("Please specify user. Input both the UID and Username.");
    }

    uid = uid.trim();
    username = username.trim();
    List<Map<String, Object>> userDatamaps = storageHandler.getAllUserDataMaps();
    for (Map<String, Object> datamap : userDatamaps) {
      if (datamap.get("uid") == uid) {
        if (datamap.get("username") != username) {
          System.out.println("Error validating user. Username does not match UID.");
          throw new IllegalArgumentException("Error validating user. Username does not match UID.");
        }
        return;
      }
    }

    System.out.println("Error validating user. UID and user are not in database");
    throw new IllegalArgumentException("Error validating user. UID and user are not in database.");
  }

  /**
   * Check if the input for the image field is valid
   *
   * @param base64Image A String input representing a base64image
   */
  public void validateImage(String base64Image) {
    if (base64Image == null || base64Image.isBlank()) {
      System.out.println("All listings arguments are required (title, tags, price, "
        + "image, category, condition, description)");
      throw new IllegalArgumentException(
        "All listings arguments are required "
          + "(title, tags, price, image, category, condition, description)");
    }
  }

  /**
   * Check if the input for the tags field is valid
   *
   * @param tags A String input of word(s) separated by commas representing the tags field
   */
  public static void validateTags(String tags) {
    if (tags == null || tags.isBlank()) {
      System.out.println("All listings arguments are required (title, tags, price, "
        + "image, category, condition, description)");
      throw new IllegalArgumentException(
        "All listings arguments are required "
          + "(title, tags, price, image, category, condition, description)");
    }

    tags = tags.trim();
    // Remove whitespace greater than 1 between words and before/after commas
    tags = tags.replaceAll("\\s{2,}", " ");

    // there should be no extra spaces and  tags are in the form "tag1,tag2,tag3, two wordtag"
    if (tags.length() - tags.replace("  ", "").replace(" ,", ",").replace(", ", ",").length() > 0) {
      System.out.println(
          "Each tag should only have NO space between words and non before and after commas.");
      throw new IllegalArgumentException(
          "Each tag should only have NO space between words and non before and after commas.");
    }

    // each tag should be less than or equal to 2 words
    if (countWordsBetweenCommas(tags) > 3) {
      System.out.println("Each tag should be less than or equal to 3 words.");
      throw new IllegalArgumentException("Each tag should be less than or equal to 3 words.");
    }

    // tags are in the form "tag1,tag2,tag3, two wordtag"
    if (tags.length() - tags.replace(",  ,", "").replace(",,", "").replace(", ,", "").length() > 0) {
      System.out.println("Each tag should have a value.");
      throw new IllegalArgumentException("Each tag should have a value.");
    }

    // if there are more than 5 tags, error

    if (tags.length() - tags.replace(",", "").length() > 4) {
      System.out.println("Please input less than or equal to 5 tags.");
      throw new IllegalArgumentException("Please input less than or equal to 5 tags.");
    }

    // if there are repeated tags, error
    if (!noDuplicateEntries(tags)) {
      System.out.println("Please make sure all tags are unique.");
      throw new IllegalArgumentException("Please make sure all tags are unique.");
    }
  }

  /**
   * Check if the input for the price field is valid
   *
   * @param price A String input of a number representing the price field
   */
  public static void validatePrice(String price) {
    if (price == null || price.isBlank()) {
      System.out.println("All listings arguments are required (title, tags, price, "
        + "image, category, condition, description)");
      throw new IllegalArgumentException(
        "All listings arguments are required "
          + "(title, tags, price, image, category, condition, description)");
    }
    double value = 0;
    // check if price is a number
    try {
      value = Double.parseDouble(price);
      if (Double.isNaN(value)) {
        throw new NumberFormatException(price);
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid value for price: " + price);
      throw new NumberFormatException("Invalid value for price: " + price);
    }

    // check if price is negative value
    if ((value < 0) || (value > 999999999)) {
      System.out.println("Price cannot be negative or larger than 999999999.");
      throw new IllegalArgumentException("Price cannot be negative or larger than 999999999.");
    }

    // Check for more than 2 decimal places
    if (Math.floor(value * 100) != value * 100) {
      System.out.println("Price has more than two decimal points.");
      throw new IllegalArgumentException("Price has more than two decimal points.");
    }
  }

  /**
   * Check if the input for the title field is valid
   *
   * @param title A String input of word(s) representing the title field
   */
  public static void validateTitle(String title) {
    if (title == null || title.isBlank()) {
      System.out.println("All listings arguments are required (title, tags, price, "
        + "image, category, condition, description)");
      throw new IllegalArgumentException(
        "All listings arguments are required "
          + "(title, tags, price, image, category, condition, description)");
    }

    title = title.trim();
    // check if title is less than 40 characters
    if (title.length() > 40) {
      System.out.println("Title must be less than or equal to 40 characters.");
      throw new IllegalArgumentException("Title must be less than or equal to 40 characters.");
    }
  }

  /**
   * Check if the input for the condition field is valid
   *
   * @param condition A String input of word(s) representing the condition field
   */
  public static void validateCondition(String condition) {
    if (condition == null || condition.isBlank()) {
      System.out.println("All listings arguments are required (title, tags, price, "
        + "image, category, condition, description)");
      throw new IllegalArgumentException(
        "All listings arguments are required "
          + "(title, tags, price, image, category, condition, description)");
    }

    // check if condition option is one of the valid options
    condition = condition.trim().toLowerCase();
    if (!(condition.equals("new") || condition.equals("like new") || condition.equals("used"))) {
      System.out.println(
        "Please choose from valid condition inputs (i.e. new, like new, or Used).");
      throw new IllegalArgumentException(
        "Please choose from valid condition inputs (i.e. New, Like New, or Used).");
    }
  }

  /**
   * Check if the input for the category field is valid
   *
   * @param category A String input of word(s) representing the category field
   */
  public static void validateCategory(String category) {
    if (category == null || category.isBlank()) {
      System.out.println("All listings arguments are required (title, tags, price, "
        + "image, category, condition, description)");
      throw new IllegalArgumentException(
        "All listings arguments are required "
          + "(title, tags, price, image, category, condition, description)");
    }


    if (category.contains(",")) {
      System.out.println("Category can only have one value (i.e.value must not contain commas).");
      throw new IllegalArgumentException(
        "Category can only have one value (i.e.value must not contain commas).");
    }

    // check if category option is one of the valid options
    category = category.trim().toLowerCase();
    List<String> categoryList = new ArrayList<String>();
    // check if its in list
//    categoryList.add("clothes").add("tech"), "school", "furniture", "kitchen", "bathroom");
    if (!(category.equals("clothes") || category.equals("tech") || category.equals("school") ||
      category.equals("furniture") || category.equals("kitchen") || category.equals("bathroom") ||
      category.equals("misc"))) {
      System.out.println(
        "Please choose from valid condition inputs (i.e. Clothes, Tech, School, Furniture, Kitchen, "
          + "or Bathroom).");
      throw new IllegalArgumentException(
        "Please choose from valid condition inputs (i.e. Clothes, Tech, School, Furniture, Kitchen, "
          + "or Bathroom).");
    }
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
      System.out.println("AddListingHandler has received a request.");
      // Collect base64Image from raw body
      System.out.println(request);
      String base64Image = request.body();
      String uid = request.queryParams("uid");
      String username = request.queryParams("username");
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

      validateUser(uid, username);
      validateTitle(title);
      validateTags(tags);
      validatePrice(price);
      validateCondition(condition);
      validateCategory(category);
      validateImage(base64Image);

      String listingUUID = UUID.randomUUID().toString();
      String imageName = "listing-" + listingUUID + ".jpg";
      String imageUrl;
      try {
        imageUrl = gcsHandler.uploadImageToGCS(base64Image, imageName);
      } catch (Exception e) {
        System.out.println("Error loading image: " + e.getMessage());
        throw new IllegalArgumentException("Error loading image: " + e.getMessage());
      }

      System.out.println("Valid inputs recieved");
      data.put("uid", uid);
      data.put("username", username);
      data.put("imageUrl", imageUrl);
      data.put("tags", tags);
      data.put("condition", condition);
      data.put("price", price);
      data.put("title", title);
      data.put("description", description);
      data.put("category", category);

      String listingId = "listing-" + listingUUID;
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
