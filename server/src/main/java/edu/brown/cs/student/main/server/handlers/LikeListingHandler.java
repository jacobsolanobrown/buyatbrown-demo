package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LikeListingHandler implements Route {

  private final StorageInterface storageHandler;

  public LikeListingHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the request to like a listing and store it under the user's liked listings.
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

      // Validate inputs
      if (uid == null || listingId == null) {
        throw new IllegalArgumentException("Both 'uid' and 'listingId' are required.");
      }
      Map<String, Object> listing = this.storageHandler.getListingForUser(uid, listingId);

      //      List<Map<String, Object>> allListings = this.storageHandler.getAllUsers();
      //
      //      Map<String, Object> listing = allListings.stream()
      //          .filter(listingMap -> listingMap.get("uid").toString().equalsIgnoreCase(uid)
      //              || uid.equalsIgnoreCase("ignore"))
      //          .findFirst()
      //          .orElseThrow(() -> new IllegalArgumentException("No listing found with the given
      // ID: " + uid));

      // Add the listing to the user's liked listings
      String likedListingId = "liked-" + listingId;
      this.storageHandler.addDocument(uid, "liked_listings", likedListingId, listing);

      // Log the operation
      System.out.println("User " + uid + " liked listing: " + listingId);

      // Prepare success response
      responseMap.put("response_type", "success");
      responseMap.put("uid", uid);
      responseMap.put("listingId", listingId);
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
