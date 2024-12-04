package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.*;
import java.util.stream.Collectors;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterListingsHandler implements Route {

  private final StorageInterface storageHandler;

  public FilterListingsHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Count the number of times the keyword appears in a listing field
   *
   * @param listing   A listing represented as a mapping from field name (e.g. title) to field value for the listing
   * @param keyword   The keyword for which the function counts the number of appearances for
   * @param fieldToSearch   The field in the listing for which the function should count the number of appearances
   * @return An int representing number of times the keyword appears in the specified listing field
   */
  public int countKeywordAppearances(Map<String, Object> listing, String keyword, String fieldToSearch) {
    assert !keyword.isEmpty();

    String fieldString = listing.get(fieldToSearch).toString();
    return (fieldString.length() -
      fieldString.replace(keyword, "").length()) / keyword.length();
  }

  /**
   * Converts the string to a Boolean (True if string is "true", False if "false")
   *
   * @param booleanString   The string that is being converted into a Boolean
   * @return A Boolean representing the Boolean value of a string
   */
  public Boolean stringToBoolean(String booleanString) {
    if (booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("false")) {
      return Boolean.valueOf(booleanString);
    } else {
      throw new IllegalArgumentException("filterByTitle, filterByCondition, filterByTag, and "
        + "filterByDescription should not be null");
    }
  }

  /**
   * Handles requests for filtering listings.
   *
   * @param request  The HTTP request object.
   * @param response The HTTP response object.
   * @return The filtered listings as a JSON string.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // get the request values
      String keyword =  request.queryParams("keyword");
      String filterByTitle = request.queryParams("filterByTitle");
      String filterByCondition = request.queryParams("filterByCondition");
      String filterByTag = request.queryParams("filterByTag");
      String filterByDescription = request.queryParams("filterByDescription");

      // validate the inputs of the request
      System.out.println("Validating parameter values for search");
      if (filterByTitle == null || filterByTitle.isEmpty() ||
        filterByCondition == null || filterByCondition.isEmpty() ||
        filterByTag == null || filterByTag.isEmpty() ||
        filterByDescription == null || filterByDescription.isEmpty()) {
        System.out.println("Cannot have blank filter parameters. Please ensure that filterByTitle, "
          + "filterByCondition, filterByTag, and filterByDescription are non-null and non-empty values.");
        throw new IllegalArgumentException("Cannot have blank filter parameters. "
          + "Please ensure that filterByTitle, filterByCondition, filterByTag, and "
          + "filterByDescription are non-null and non-empty values.");
      }

      Boolean filterByTitleBoolean = stringToBoolean(filterByTitle);
      Boolean filterByConditionBoolean = stringToBoolean(filterByCondition);
      Boolean filterByTagBoolean = stringToBoolean(filterByTag);
      Boolean filterByDescriptionBoolean = stringToBoolean(filterByDescription);

      if (!filterByTitleBoolean && !filterByConditionBoolean && !filterByTagBoolean && !filterByDescriptionBoolean) {
        System.out.println("Please provide at least field value of a listing to search. " + 
        "i.e. Either filterByTitle, filterByCondition, filterByTag, or filterByDescription must be 'true'.");
        throw new IllegalArgumentException("Cannot have blank filter parameters. "
          + "Please ensure that title, condition, tag, and description are non-null and non-empty values.");
      }

      List<Map<String, Object>> allListings = this.storageHandler.getAllUsersListings();
      List<Map<String, Object>> filteredListings = allListings;

      System.out.println("Searching...");
      List<AbstractMap.SimpleEntry<Map<String, Object>, Integer>> sortedListings = new ArrayList<>();
      // Apply filters
      // List of Pairs(<Map<String, Object> Listing(String field name, Object field value))
      for (Map<String, Object> listing : allListings) {
        Integer keywordInTitleCount = 0;
        if (filterByTitleBoolean) {
          keywordInTitleCount = countKeywordAppearances(listing, keyword, "title");
        }

        Integer keywordInConditionCount = 0;
        if (filterByConditionBoolean && keyword.toLowerCase().equals(listing.get("condition"))) {
          keywordInConditionCount = 1;
        }

        Integer keywordInTagCount = 0;
        if (filterByTagBoolean) {
          keywordInTagCount = countKeywordAppearances(listing, keyword, "tag");
        }

        Integer keywordInDescriptionCount = 0;
        if (filterByDescriptionBoolean) {
          keywordInDescriptionCount = countKeywordAppearances(listing, keyword,
            "description");
        }

        // get value for listing (based on how many times the keyword appears) while setting 
        // weights for each field
        Integer heuristicValueForListing = keywordInDescriptionCount +
          (3 * keywordInTagCount) + (4 * keywordInConditionCount) + (7 * keywordInTitleCount);

        // skip listing if there are no appearances of keyword in the listing
        if (heuristicValueForListing > 0) {
          // if there is an appearance of the keyword, then add an entry to sortedListings
          sortedListings.add(new AbstractMap.SimpleEntry<>(listing, heuristicValueForListing));
        }

        // Sort listings by heuristic value in descending order
        sortedListings.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        filteredListings = sortedListings.stream()
          .map(AbstractMap.SimpleEntry::getKey)
          .toList();
      }

      responseMap.put("response_type", "success");
      responseMap.put("filtered_listings", filteredListings);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    return Utils.toMoshiJson(responseMap);
  }
}
