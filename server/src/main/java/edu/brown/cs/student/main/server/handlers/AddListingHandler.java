package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for adding a listing to the database */
public class AddListingHandler implements Route {

  public StorageInterface storageHandler;

  public AddMarkerHandler(StorageInterface storageHandler) {
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
      
      // List<String> latLong = List.of(latitude, longitude);

      Map<String, Object> data = new HashMap<>();
      data.put("latLong", latLong);

      System.out.println(
          "adding marker at: "
              + latitude
              + ", "
              + longitude
              + ", storing in database as: "
              + latLong
              + ", for user: "
              + uid);

      // get the current word count to make a unique word_id by index.
      int markerCount = this.storageHandler.getCollection(uid, "markers").size();
      String markerId = "marker-" + markerCount;

      // use the storage handler to add the document to the database
      this.storageHandler.addDocument(uid, "markers", markerId, data);

      responseMap.put("response_type", "success");
      responseMap.put("lat", latitude);
      responseMap.put("long", longitude);
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
