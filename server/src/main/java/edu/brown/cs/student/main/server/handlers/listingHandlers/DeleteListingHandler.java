package edu.brown.cs.student.main.server.handlers.listingHandlers;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for deleting a listing from the database */
public class DeleteListingHandler implements Route {
  public StorageInterface storageHandler;

  public DeleteListingHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // collect parameters from the request
      String uid = request.queryParams("uid");
      String listingId = request.queryParams("listingId");

      if (uid == null || listingId == null) {
        throw new IllegalArgumentException("Both 'uid' and 'listingId' are required.");
      }

      List<Map<String, List<String>>> allUsers = this.storageHandler.getAllUserFavoritesIds();
      System.out.println("All users: " + allUsers);
      System.out.println("size, " + allUsers.size());

      // Retrieve the user's listings from the database
      // Retrieve the listing for the user from the database
      Map<String, Object> listing = this.storageHandler.getListingForUser(uid, listingId);
      System.out.println("Retrieved listing: " + listing);

      // Check if the listing exists
      if (listing == null) {
        throw new IllegalArgumentException("Listing with ID " + listingId + " does not exist.");
      }

      // Remove the listing from the database
      this.storageHandler.removeDocument(uid, "listings", listingId);

      // Remove the listing from the user's favorites as well if it exists there

      // For each user in the database
      for (Map<String, List<String>> user : allUsers) {
        for (Map.Entry<String, List<String>> entry : user.entrySet()) {
          String userId = entry.getKey();
          System.out.println("key: " + userId);
          List<String> favorites = entry.getValue();
          System.out.println("value: " + favorites);
          if (favorites.contains("liked-" + listingId)) {
            System.out.println("found listing in favorites");
            this.storageHandler.removeDocument(userId, "liked_listings", "liked-" + listingId);
          }
        }
      }

      // Log success
      System.out.println("Deleted listing with ID: " + listingId + " for user: " + uid);

      // Prepare the success response
      responseMap.put("response_type", "success");
      responseMap.put("listingId", listingId);
      responseMap.put("uid", uid);
    } catch (Exception e) {
      // Handle errors gracefully
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    // Return the response as a JSON object
    return Utils.toMoshiJson(responseMap);
  }
}
