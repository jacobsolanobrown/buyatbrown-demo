package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.*;
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
   * @param listing A listing represented as a mapping from field name (e.g. title) to field value
   *     for the listing
   * @param keyword The keyword for which the function counts the number of appearances for
   * @param fieldToSearch The field in the listing for which the function should count the number of
   *     appearances
   * @return An int representing number of times the keyword appears in the specified listing field
   */
  public static int countKeywordAppearances(
    Map<String, Object> listing, String keyword, String fieldToSearch) {
    assert !keyword.isEmpty();

    String fieldString = listing.get(fieldToSearch).toString();
    return (fieldString.length() - fieldString.replace(keyword, "").length()) / keyword.length();
  }

  /**
   * Count the number of times the keywords appear in a listing field
   *
   * @param listing A listing represented as a mapping from field name (e.g. title) to field value
   *                for the listing
   * @param keywords A String input of keywords separated by commas
   * @param fieldToSearch The field in the listing for which the function should count the number of
   *    appearances
   * @return An Integer representing the total number of times the keywords appear in the fieldToSearch
   */
  public static Integer countManyKeywordsAppearances(Map<String, Object> listing,
    String keywords, String fieldToSearch) {
    ArrayList<String> keywordList = new ArrayList<String>(Arrays.asList(keywords.split(",")));
    Integer totalNumAppearances = 0;
    for (String keyword : keywordList) {
      totalNumAppearances += countKeywordAppearances(listing, keyword, fieldToSearch);
    }
    return totalNumAppearances;
  }

  /**
   * Converts the string to a Boolean (True if string is "true", False if "false")
   *
   * @param booleanString The string that is being converted into a Boolean
   * @return A Boolean representing the Boolean value of a string
   */
  public Boolean stringToBoolean(String booleanString) {
    if (booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("false")) {
      return Boolean.valueOf(booleanString);
    } else {
      throw new IllegalArgumentException(
        "filterByTitle, filterByCondition, filterByTag, and "
          + "filterByDescription should not be null");
    }
  }

  /**
   * Handles requests for filtering listings.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return The filtered listings as a JSON string.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // get the request values
      String titleDescriptionKeyword = request.queryParams("titleDescriptionKeyword");
      String categoryKeyword = request.queryParams("categoryKeyword");
      String tagKeywords = request.queryParams("tagKeywords");
      String conditionKeywords = request.queryParams("conditionKeywords");


      // validate the inputs of the request
      System.out.println("Validating parameter values for search");
      if (titleDescriptionKeyword == null
        || titleDescriptionKeyword.isEmpty()
        || categoryKeyword == null
        || categoryKeyword.isEmpty()
        || tagKeywords == null
        || tagKeywords.isEmpty()
        || conditionKeywords == null
        || conditionKeywords.isEmpty()) {
        System.out.println(
          "Cannot have blank filter parameters. Please ensure that titleDescriptionKeyword, "
            + "categoryKeyword, tagKeywords, and conditionKeywords are non-null and non-empty values.");
        throw new IllegalArgumentException(
          "Cannot have blank filter parameters. "
            + "Please ensure that titleDescriptionKeyword, categoryKeyword, tagKeywords, and "
            + "conditionKeywords are non-null and non-empty values.");
      }

      List<Map<String, Object>> allListings = this.storageHandler.getAllUsersListings();
      List<Map<String, Object>> filteredListings = allListings;

      System.out.println("Searching...");
      List<AbstractMap.SimpleEntry<Map<String, Object>, Integer>> sortedListings =
        new ArrayList<>();
      // Apply filters
      // List of Pairs(<Map<String, Object> Listing(String field name, Object field value))
      for (Map<String, Object> listing : allListings) {

        // Search bar filtering: Filter by Title and Description
        Integer titleDescriptionValue = -500;
        if (!titleDescriptionKeyword.equalsIgnoreCase("ignore")) {
          Integer titleValue =
            (countKeywordAppearances(listing, titleDescriptionKeyword, "title") * 10);
          Integer descriptionValue = countKeywordAppearances(listing, titleDescriptionKeyword, "description");
          titleDescriptionValue = titleValue + descriptionValue;
        }

        Integer categoryValue = -500;
        if (!categoryKeyword.equalsIgnoreCase("ignore")) {
          categoryValue = countKeywordAppearances(listing, categoryKeyword, "category");
        }

        Integer conditionValue = -500;
        if (!conditionKeywords.toLowerCase().contains("ignore")) {
          conditionValue = countManyKeywordsAppearances(listing, conditionKeywords, "condition");
        }

        Integer tagValue = -500;
        if (!tagKeywords.toLowerCase().contains("ignore")) {
          tagValue = countManyKeywordsAppearances(listing, tagKeywords, "tags");
        }

        // get value for listing (based on how many times the keyword appears) while setting
        // weights for each field
        Integer heuristicValueForListing = titleDescriptionValue + categoryValue + conditionValue + tagValue;

        // skip listing if there are no appearances of keyword in the listing
        if (heuristicValueForListing > 0) {
          // if there is an appearance of the keyword, then add an entry to sortedListings
          sortedListings.add(new AbstractMap.SimpleEntry<>(listing, heuristicValueForListing));
        }

        // Sort listings by heuristic value in descending order
        sortedListings.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        filteredListings = sortedListings.stream().map(AbstractMap.SimpleEntry::getKey).toList();
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
