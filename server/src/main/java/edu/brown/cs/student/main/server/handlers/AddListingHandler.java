package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for adding a listing to the database */
public class AddListingHandler implements Route {

  public StorageInterface storageHandler;

  public AddListingHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

//  /**
//   * Capitalizes the first letter of the input word(s)
//   * credit: GeeksForGeeks
//   *
//   * @param input A String input of word(s) that will be capitalized
//   * @return The words where each first letter is capitalized
//   */
//  public static String capitalizeWords(String input) {
//    // split the input string into an array of words
//    String[] words = input.split("\\s");
//
//    // StringBuilder to store the result
//    StringBuilder result = new StringBuilder();
//
//    // iterate through each word
//    for (String word : words) {
//      word = word.toLowerCase();
//      // capitalize the first letter, append the rest of the word, and add a space
//      result.append(Character.toTitleCase(word.charAt(0)))
//        .append(word.substring(1))
//        .append(" ");
//    }
//
//    // convert StringBuilder to String and trim leading/trailing spaces
//    return result.toString().trim();
//  }

  /**
   * Count the number of words between commas
   * (used to validate filterByTags input (i.e. if the number of words per tag <= 2)
   *
   * @param text A String input of word(s) separated by commas
   * @return An int representing the number of words between commas
   */
  public static int countWordsBetweenCommas(String text) {
    int count = 0;
    boolean insideComma = false;

    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);

      if (ch == ',') {
        insideComma = !insideComma;
      } else if (Character.isWhitespace(ch) && insideComma) {
        count++;
      }
    }

    return count;
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
      String imageUrl = request.queryParams("imageUrl");
      String price = request.queryParams("price");
      String title = request.queryParams("title");
      String tags = request.queryParams("tags");
      String condition = request.queryParams("condition");
      String description = request.queryParams("description");

      // create new listing with collected parameters
      // Listing listing = new Listing(username, title, imageUrl, price, description);

      Map<String, Object> data = new HashMap<>();
      System.out.println("Validating parameter values for search");
      if (uid == null || username == null || imageUrl == null || price == null ||
        condition == null || description == null || title == null || tags == null) {
        System.out.println("All listings arguments are required "
          + "(uid, username, title, tags, price, imageUrl, condition, description)");
        throw new IllegalArgumentException("All listings arguments are required "
          + "(uid, username, title, tags, price, imageUrl, condition, description)");
      }

      if (title.length() >= 40) {
        System.out.println("Title must be less than or equal to 40 characters");
        throw new IllegalArgumentException("Title must be less than or equal to 40 characters");
      }

      if (Double.parseDouble(price) <= 0) {
        System.out.println("Price must be greater than or equal to 0");
        throw new IllegalArgumentException("Price must be greater than or equal to 0");
      }

      condition = condition.toLowerCase();
      if (!(condition.equals("new") || condition.equals("like new") || condition.equals("used"))) {
        System.out.println("Please choose from valid condition inputs (i.e. new, like new, or used");
        throw new IllegalArgumentException("Please choose from valid condition inputs (i.e. New, Like New, or Used");
      }

      if (countWordsBetweenCommas(tags) > 2) {
        System.out.println("Each tag should be less than or equal to 2 words");
        throw new IllegalArgumentException("Each tag should be less than or equal to 2 words");
      }

      // if there are more than 5 tags, error
      if (tags.length() - tags.replace(",", "").length() > 5) {
        System.out.println("Please input less than or equal to 5 tags");
        throw new IllegalArgumentException("Please input less than or equal to 5 tags");
      }

      System.out.println("Valid inputs recieved");
      data.put("uid", uid);
      data.put("username", username);
      data.put("imageUrl", imageUrl);
      data.put("tags", tags);
      data.put("condition", condition);
      data.put("price", price);
      data.put("title", condition);
      data.put("description", description);

      System.out.println(
        "addded listing for username: "
          + username
          + ", title: "
          + condition
          + ", tags: "
          + tags
          + ", imageUrl: "
          + imageUrl
          + ", price: "
          + price
          + ", description: "
          + description
          + ", for user: "
          + uid);
//        System.err.println("Skipping incomplete entry: username: "
//            + username
//            + ", title: "
//            + condition
//            + ", tags: "
//            + tags
//            + ", imageUrl: "
//            + imageUrl
//            + ", price: "
//            + price
//            + ", description: "
//            + description
//            + ", for user: "
//            + uid);
//      throw new IllegalArgumentException("All listings arguments are required "
//        + "(uid, username, title, tags, price, imageUrl, condition, description)");
//      data.put("item", listing);


      // get the current word count to make a unique word_id by index.
      int listingCount = this.storageHandler.getCollection(uid, "listings").size();
      String listingId = "listing-" + listingCount;

      // use the storage handler to add the document to the database
      this.storageHandler.addDocument(uid, "listings", listingId, data);

      responseMap.put("response_type", "success");
      responseMap.put("title", condition);
      responseMap.put("imageUrl", imageUrl);
      responseMap.put("tags", tags);
      responseMap.put("price", price);
      responseMap.put("description", description);
      responseMap.put("username", username);
      responseMap.put("condition", condition);
      responseMap.put("uid", uid);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
