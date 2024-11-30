package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.parserParameterizedTypes.ListingsCollection.Listing;
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
      String description = request.queryParams("description");

      // create new listing with collected parameters
      Listing listing = new Listing(username, title, imageUrl, price, description);

      Map<String, Object> data = new HashMap<>();
//      data.put("item", listing);
      data.put("uid", uid);
      data.put("username", username);
      data.put("imageUrl", imageUrl);
      data.put("price", price);
      data.put("title", title);
      data.put("description", description);

      System.out.println(
          "addded listing for username: "
              + username
              + ", title: "
              + title
              + ", imageUrl: "
              + imageUrl
              + ", price: "
              + price
              + ", description: "
              + description
              + ", for user: "
              + uid);

      // get the current word count to make a unique word_id by index.
      int listingCount = this.storageHandler.getCollection(uid, "listings").size();
      String listingId = "listing-" + listingCount;

      // use the storage handler to add the document to the database
      this.storageHandler.addDocument(uid, "listings", listingId, data);

      responseMap.put("response_type", "success");
      responseMap.put("title", title);
      responseMap.put("imageUrl", imageUrl);
      responseMap.put("price", price);
      responseMap.put("description", description);
      responseMap.put("username", username);
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
