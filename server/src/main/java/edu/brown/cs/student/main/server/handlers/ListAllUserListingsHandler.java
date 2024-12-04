package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for the ListAllUserListings route, which lists all listings for all users */
public class ListAllUserListingsHandler implements Route {

  public StorageInterface storageHandler;

  public ListAllUserListingsHandler(StorageInterface storageHandler) {
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
      List<Map<String, Object>> allListings = this.storageHandler.getAllUsersListings();
      System.out.println("printing all user listings: " + allListings);
      responseMap.put("response_type", "success");
      responseMap.put("listings", allListings);
    } catch (Exception e) {
      // error likely occurred in the storage handler
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
