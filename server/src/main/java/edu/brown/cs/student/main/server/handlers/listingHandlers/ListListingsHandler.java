package edu.brown.cs.student.main.server.handlers.listingHandlers;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListListingsHandler implements Route {

  public StorageInterface storageHandler;

  public ListListingsHandler(StorageInterface storageHandler) {
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
      String uid = request.queryParams("uid");

      System.out.println("listing listings for user: " + uid);

      // get all the listings for the user
      List<Map<String, Object>> vals = this.storageHandler.getCollection(uid, "listings");

      // convert the key,value map to just a list of the listings.
      System.out.println(vals);
      List<String> listings = vals.stream().map(Object::toString).toList();
      System.out.println("listings: " + listings);
      //      System.out.println(vals.stream().map(listing -> listing.get("item")));

      responseMap.put("response_type", "success");
      responseMap.put("listings", listings);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
