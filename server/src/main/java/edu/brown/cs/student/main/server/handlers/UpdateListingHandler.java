package edu.brown.cs.student.main.server.handlers;
import edu.brown.cs.student.main.server.parserParameterizedTypes.ListingsCollection.Listing;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
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
   * Handles the request to update an existing listing.
   *
   * @param request  The HTTP request object
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
//          .filter(listingMap -> listingMap.get("listingId").toString().equalsIgnoreCase(listingId)
//              || listingId.equalsIgnoreCase("ignore"))
//          .findFirst()
//          .orElseThrow(() -> new IllegalArgumentException("No listing found with the given ID: " + listingId));

//    System.out.println(listing);
//      System.out.println(allListings);
      Map<String, Object> listing = this.storageHandler.getListingForUser(uid, listingId);
      // Collect new parameters to update
      String title = request.queryParams("title");
      String price = request.queryParams("price");
      String description = request.queryParams("description");
      String imageUrl = request.queryParams("imageUrl");

      // Update the fields only if new values are provided
      if (title != null) listing.put("title", title);
      if (price != null) listing.put("price", price);
      if (description != null) listing.put("description", description);
      if (imageUrl != null) listing.put("imageUrl", imageUrl);

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
