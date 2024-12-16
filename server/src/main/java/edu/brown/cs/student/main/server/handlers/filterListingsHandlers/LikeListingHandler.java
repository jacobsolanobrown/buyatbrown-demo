package edu.brown.cs.student.main.server.handlers.filterListingsHandlers;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
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

      // Fetch all listings
      List<Map<String, Object>> allListings = this.storageHandler.getAllUsersListings();

      // Find the specific listing by listingId
      Map<String, Object> targetListing = allListings.stream()
          .filter(listing -> listingId.equals(listing.get("listingId")))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Listing not found for the given listingId."));

      // Add the listing to the user's liked listings
      String likedListingId = "liked-" + listingId;
      this.storageHandler.addDocument(uid, "liked_listings", likedListingId, targetListing);

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
