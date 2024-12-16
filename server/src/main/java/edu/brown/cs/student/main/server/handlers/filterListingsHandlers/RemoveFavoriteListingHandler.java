package edu.brown.cs.student.main.server.handlers.filterListingsHandlers;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class RemoveFavoriteListingHandler implements Route {

  private final StorageInterface storageHandler;

  public RemoveFavoriteListingHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Collect parameters from the request
      String uid = request.queryParams("uid");
      String listingId = request.queryParams("listingId");

      // Validate inputs
      if (uid == null || listingId == null) {
        throw new IllegalArgumentException("Both 'uid' and 'listingId' are required.");
      }

      // The document ID in the liked_listings collection follows the same pattern as in
      // LikeListingHandler
      String likedListingId = "liked-" + listingId;

      // Remove the listing from the user's liked listings
      this.storageHandler.removeDocument(uid, "liked_listings", likedListingId);

      // Log the operation
      System.out.println("User " + uid + " unliked listing: " + listingId);

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
