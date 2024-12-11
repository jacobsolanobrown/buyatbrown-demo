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

public class UpdateListingHandler implements Route {

  private final StorageInterface storageHandler;

  public UpdateListingHandler(StorageInterface storageHandler) {
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
   * Handles the request to update an existing listing.
   *
   * @param request The HTTP request object
   * @param response The HTTP response object
   * @return A JSON response indicating success or failure
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Collect parameters from the request
      String uid = request.queryParams("uid");
      String listingId = request.queryParams("listingId");

      if (uid == null || listingId == null) {
        throw new IllegalArgumentException("Both 'uid' and 'listingId' are required.");
      }
      //      List<Map<String, Object>> allListings = this.storageHandler.getAllUsersListings();
      //
      //      Map<String, Object> listing = allListings.stream()
      //          .filter(listingMap ->
      // listingMap.get("listingId").toString().equalsIgnoreCase(listingId)
      //              || listingId.equalsIgnoreCase("ignore"))
      //          .findFirst()
      //          .orElseThrow(() -> new IllegalArgumentException("No listing found with the given
      // ID: " + listingId));

      //    System.out.println(listing);
      //      System.out.println(allListings);
      Map<String, Object> listing = this.storageHandler.getListingForUser(uid, listingId);
      // Collect new parameters to update
      String title = request.queryParams("title");
      String price = request.queryParams("price");
      String description = request.queryParams("description");
      String imageUrl = request.queryParams("imageUrl");
      String category = request.queryParams("category");
      String condition = request.queryParams("condition");
      String tags = request.queryParams("tags");

      // Update the fields only if new values are provided
      //      if (title != null) listing.put("title", title);
      if (title != null) {
        if (title.length() > 40) {
          System.out.println("Title must be less than or equal to 40 characters");
          throw new IllegalArgumentException("Title must be less than or equal to 40 characters");
        }
        listing.put("title", title);
      }
      if (price != null) {
        // check if price is negative value
        if (Double.parseDouble(price) < 0) {
          System.out.println("Price cannot be negative");
          throw new IllegalArgumentException("Price cannot be negative");
        }
        listing.put("price", price);
      }
      ;
      if (description != null) listing.put("description", description);
      if (imageUrl != null) listing.put("imageUrl", imageUrl);

      if (condition != null) {
        // check if condition option is one of the three valid options
        condition = condition.toLowerCase();
        if (!(condition.equals("new")
          || condition.equals("like new")
          || condition.equals("used"))) {
          System.out.println(
            "Please choose from valid condition inputs (i.e. new, like new, or used");
          throw new IllegalArgumentException(
            "Please choose from valid condition inputs (i.e. New, Like New, or Used");
        }
        listing.put("condition", condition);
      }

      if (category != null) {
        if (category.contains(",")) {
          System.out.println(
            "Category can only have one value (i.e.value  must not contain commas)");
          throw new IllegalArgumentException(
            "Category can only have one value (i.e.value  must not contain commas)");
        }
        listing.put("category", category);
      }

      if (tags != null) {
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
        listing.put("tags", tags);
      }

      // Save the updated listing back to the storage
      storageHandler.addDocument(uid, "listings", listingId, listing);

      // Log the operation
      System.out.println("Updated listing: " + listingId + " for user: " + uid);

      // Prepare success response
      responseMap.put("response_type", "success");
      responseMap.put("uid", uid);
      responseMap.put("listingId", listingId);
      responseMap.put("updated_listing", listing);
    } catch (Exception e) {
      // Handle errors gracefully
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    // Return the response as JSON
    return Utils.toMoshiJson(responseMap);
  }
}
