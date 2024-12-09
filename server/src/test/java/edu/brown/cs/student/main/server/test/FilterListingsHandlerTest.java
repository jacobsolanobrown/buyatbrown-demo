package edu.brown.cs.student.main.server.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.handlers.FilterListingsHandler;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

public class FilterListingsHandlerTest {

  private MockedFirebaseUtilities mockedStorage;
  private FilterListingsHandler handler;

  public FilterListingsHandlerTest() {
    // Initialize the storage and handler in the constructor
    this.mockedStorage = new MockedFirebaseUtilities();
    this.handler = new FilterListingsHandler(mockedStorage);
  }

  @Before
  public void setup() {
    prepareSampleListings();
  }

  private Request createMockRequest(Map<String, String> params) {
    return new Request() {
      @Override
      public String queryParams(String key) {
        return params.get(key);
      }
    };
  }

  private Response createMockResponse() {
    return new Response() {
      // Implement as needed (e.g., to capture status codes or headers)
    };
  }

  private void prepareSampleListings() {
    Map<String, Object> listing1 =
        createListing(
            "user1",
            "Vintage Camera for Sale",
            "Great condition vintage camera",
            "excellent",
            "photography");
    Map<String, Object> listing2 =
        createListing(
            "user2", "Modern Digital Camera", "High-quality digital camera", "good", "electronics");

    Map<String, Object> listing3 =
        createListing(
            "user3",
            "Old Film Camera",
            "Vintage film camera in working condition",
            "fair",
            "vintage");
    Map<String, Object> listing4 =
        createListing(
            "user4",
            "Blue bike for sale",
            "A blue bike in great condition",
            "good",
            "blue bicycle");

    Map<String, Object> listing5 =
        createListing("user5", "Red car for sale", "A fast red car", "excellent", "sports car");

    mockedStorage.addListing(listing1);
    mockedStorage.addListing(listing2);
    mockedStorage.addListing(listing3);
    mockedStorage.addListing(listing4);
    mockedStorage.addListing(listing5);
    System.out.println("Current storage size: " + mockedStorage.getAllUsersListings().size());
  }

  private Map<String, Object> createListing(
      String uid, String title, String description, String condition, String tag) {
    Map<String, Object> listing = new HashMap<>();
    listing.put("uid", uid);
    listing.put("title", title);
    listing.put("description", description);
    listing.put("condition", condition);
    listing.put("tag", tag);
    return listing;
  }

  // Mocked test: testing success title search
  @Test
  public void testSuccessfulFilterByTitle() {
    // Prepare request parameters
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "Camera");
            put("filterByTitle", "true");
            put("filterByCondition", "false");
            put("filterByTag", "false");
            put("filterByDescription", "false");
          }
        };

    // Create mock request and response
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Call the handler
    Object result = handler.handle(mockRequest, mockResponse);

    // Verify the result
    assertTrue(result.toString().contains("Vintage Camera for Sale"));
    assertTrue(result.toString().contains("Modern Digital Camera"));
    assertTrue(result.toString().contains("Old Film Camera"));
  }

  // Mocked test: testing success condition search
  @Test
  public void testFilterByCondition() {
    // Prepare request parameters
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "excellent");
            put("filterByTitle", "false");
            put("filterByCondition", "true");
            put("filterByTag", "false");
            put("filterByDescription", "false");
          }
        };

    // Create mock request and response
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Call the handler
    Object result = handler.handle(mockRequest, mockResponse);

    // Verify the result
    assertTrue(result.toString().contains("Vintage Camera for Sale"));
    assertFalse(result.toString().contains("Modern Digital Camera"));
    assertFalse(result.toString().contains("Old Film Camera"));
  }

  // Mocked test: testing success description search
  @Test
  public void testFilterByDescription() {
    // Prepare request parameters
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "working");
            put("filterByTitle", "false");
            put("filterByCondition", "false");
            put("filterByTag", "false");
            put("filterByDescription", "true");
          }
        };

    // Create mock request and response
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Call the handler
    Object result = handler.handle(mockRequest, mockResponse);

    // Verify the result
    assertFalse(result.toString().contains("Vintage Camera for Sale"));
    assertFalse(result.toString().contains("Modern Digital Camera"));
    assertTrue(result.toString().contains("Old Film Camera"));
  }

  // Test filtering by multiple fields
  // Mocked test: testing success multiple
  @Test
  public void testMultiFieldFiltering() {
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "blue");
            put("filterByTitle", "true");
            put("filterByCondition", "true");
            put("filterByTag", "true");
            put("filterByDescription", "true");
          }
        };

    // Create mock request and response
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    Object result = handler.handle(mockRequest, mockResponse);

    // Assert multiple field matches
    assertTrue(result.toString().contains("blue bike"));
  }

  // Mocked test: testing failure
  @Test
  public void testHandleNoFilterFieldsEnabled() {
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "blue");
            put("filterByTitle", "false");
            put("filterByCondition", "false");
            put("filterByTag", "false");
            put("filterByDescription", "false");
          }
        };

    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    String result = (String) handler.handle(mockRequest, mockResponse);
    System.out.println(result);
    assertTrue(result.contains("\"response_type\":\"failure\""));
    assertTrue(
        result.contains(
            "Cannot have blank filter parameters. Please ensure that title, condition, tag, and description are non-null and non-empty values."));
  }

  // Mocked test: testing success tag search
  @Test
  public void testFilterByTag() {
    // Prepare request parameters
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "photography");
            put("filterByTitle", "false");
            put("filterByCondition", "false");
            put("filterByTag", "true");
            put("filterByDescription", "false");
          }
        };

    // Create mock request and response
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Call the handler
    Object result = handler.handle(mockRequest, mockResponse);

    // Verify the result
    assertTrue(result.toString().contains("Vintage Camera for Sale"));
    assertFalse(result.toString().contains("Modern Digital Camera"));
    assertFalse(result.toString().contains("Old Film Camera"));
  }

  // Mocked test: testing empty
  @Test
  public void testHandleNoMatchingListings() {
    Map<String, String> params =
        new HashMap<>() {
          {
            put("keyword", "notreal");
            put("filterByTitle", "true");
            put("filterByCondition", "true");
            put("filterByTag", "true");
            put("filterByDescription", "true");
          }
        };

    // Create mock request and response
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Call the handler
    Object result = handler.handle(mockRequest, mockResponse);
    Map<String, Object> responseMap = parseJsonResponse(result);
    assertEquals("success", responseMap.get("response_type"));
    List<Map<String, Object>> filteredListings =
        (List<Map<String, Object>>) responseMap.get("filtered_listings");
    assertTrue(filteredListings.isEmpty());
  }

  // Helper function to parse Json
  private Map<String, Object> parseJsonResponse(Object result) {
    // Ensure result is a valid JSON string
    assertTrue(result instanceof String, "Result should be a JSON string");

    // Use Moshi for JSON parsing
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map> adapter = moshi.adapter(Map.class);

    try {
      // Deserialize JSON into a map
      Map<String, Object> responseMap = adapter.fromJson((String) result);
      assertNotNull(responseMap, "Response map should not be null.");

      // Ensure the required keys exist
      assertTrue(
          responseMap.containsKey("response_type"), "Response map should contain 'response_type'.");
      assertTrue(
          responseMap.containsKey("filtered_listings"),
          "Response map should contain 'filtered_listings'.");

      return responseMap;
    } catch (IOException e) {
      fail("Failed to parse JSON response: " + e.getMessage());
      return null; // This line is unreachable due to fail(), but added for clarity
    }
  }
}
