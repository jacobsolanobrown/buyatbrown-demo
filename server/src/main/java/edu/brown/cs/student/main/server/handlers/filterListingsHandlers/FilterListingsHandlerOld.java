// package edu.brown.cs.student.main.server.handlers;
//
// import edu.brown.cs.student.main.server.storage.StorageInterface;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import spark.Request;
// import spark.Response;
// import spark.Route;
//
// public class FilterListingsHandlerOld implements Route {
//
//  private final StorageInterface storageHandler;
//
//  public FilterListingsHandlerOld(StorageInterface storageHandler) {
//    this.storageHandler = storageHandler;
//  }
//
//  public Object heuristic(Map<String, Object> listings, ListingFields type) {
//    int value = 0;
//    if (type == ListingFields.TAGS) {
//      String matching_tag_count = listings.get("tags").toString().length() -
//    }
//  }
//  /**
//   * Handles requests for filtering listings.
//   *
//   * @param request  The HTTP request object.
//   * @param response The HTTP response object.
//   * @return The filtered listings as a JSON string.
//   */
//  @Override
//  public Object handle(Request request, Response response) {
//    Map<String, Object> responseMap = new HashMap<>();
//    try {
////      String uid = request.queryParams("uid");
//      String condition = request.queryParams("condition");
//      String tag = request.queryParams("tag");
//      String description = request.queryParams("description");
//
//      if (condition == null || tag == null || description == null) {
//        System.out.println("Cannot have null filter parameters, "
//          + "if want to exclude filter condition use 'ignore'");
//        throw new IllegalArgumentException("Cannot have null filter parameters, "
//          + "if want to exclude filter condition use 'ignore'");
//      }
//
//      List<Map<String, Object>> allListings = this.storageHandler.getAllUsersListings();
//
//      // Apply filters
//      List<Map<String, Object>> conditionFilteredListings = allListings.stream().filter(listing ->
//        (listing.get("condition").toString().toLowerCase().contains(condition.toLowerCase()) ||
//          condition.equalsIgnoreCase("ignore"))).toList();
//      List<Map<String, Object>> descriptionFilteredListings =
// conditionFilteredListings.stream().filter(listing ->
//        (listing.get("description").toString().toLowerCase().contains(description.toLowerCase())
// ||
//          description.equalsIgnoreCase("ignore"))).toList();
//      List<Map<String, Object>> tagFilteredListings =
// descriptionFilteredListings.stream().filter(listing ->
//        (listing.get("tags").toString().toLowerCase().contains(tag.toLowerCase()) ||
//          tag.equalsIgnoreCase("ignore"))).toList();
//
//      List<Map<String, Object>> filteredListings = tagFilteredListings;
//
//      responseMap.put("response_type", "success");
//      responseMap.put("filtered_listings", filteredListings);
//    } catch (Exception e) {
//      e.printStackTrace();
//      responseMap.put("response_type", "failure");
//      responseMap.put("error", e.getMessage());
//    }
//
//    return Utils.toMoshiJson(responseMap);
//  }
// }
