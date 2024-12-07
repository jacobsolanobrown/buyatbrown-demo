package edu.brown.cs.student.main.server.test;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.server.handlers.CreateUserHandler;
import edu.brown.cs.student.main.server.handlers.ListListingsHandler;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import org.junit.jupiter.api.BeforeEach;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;


public class ListListingsHandlerTest {
  private ListListingsHandler handler;
  private MockedFirebaseUtilities mockedStorageHandler;

  // Helper method to create a mock request
  private Request createMockRequest(Map<String, String> params) {
    return new Request() {
      @Override
      public String queryParams(String key) {
        return params.get(key);
      }
    };
  }

  // Helper method to create a mock response
  private Response createMockResponse() {
    return new Response() {
      // Minimal implementation for testing
      private int status;

      @Override
      public void status(int statusCode) {
        this.status = statusCode;
      }

      public int status() {
        return this.status;
      }
    };
  }

  // Helper method to create a mock listing
  private Map<String, Object> createMockListing(String uid, String listingId) {
    Map<String, Object> listing = new HashMap<>();
    listing.put("uid", uid);
    listing.put("listingId", listingId);
    listing.put("title", "Test Listing");
    return listing;
  }

  @BeforeEach
  public void setUp() {
    mockedStorageHandler = new MockedFirebaseUtilities();
    handler = new ListListingsHandler(mockedStorageHandler);

    Map<String, Object> listing = createMockListing("user123", "listing1");
    mockedStorageHandler.addListing(listing);
  }
  //Mocked test: testing success in user listing
  @Test
  public void testSuccessfulListingsRetrieval() throws Exception {
    // Arrange
    String testUid = "user123";

    // Prepare mock listing using addListing method
    Map<String, Object> listing = createMockListing(testUid, "listing1");

    // Detailed debugging
    System.out.println("Listing to be added: " + listing);

    // Add listing
//    mockedStorageHandler.addListing(listing);
    mockedStorageHandler.addListing(testUid, "listings", listing.get("listingId").toString(), listing);


    // Debug: Inspect internal database structure
    printDatabaseStructure(mockedStorageHandler);
    // Prepare mock request
    Map<String, String> params = Map.of("uid", testUid);
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Act
    Object result = handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    assertTrue(result instanceof String);
    String jsonResult = (String) result;

    System.out.println("JSON Result: " + jsonResult);

    assertTrue(jsonResult.contains("\"response_type\":\"success\""), "Response should indicate success");
    assertTrue(jsonResult.contains("\"listings\":["), "Should have a listings array");
    assertTrue(jsonResult.contains("Test Listing"), "Should contain the test listing title");
  }

//    // Retrieve listings for specific user
//    List<Map<String, Object>> userListings = mockedStorageHandler.getCollection(testUid, "listing");
//    System.out.println("User Listings after retrieval: " + userListings);
//
//    // Get all users' listings
//    List<Map<String, Object>> allListings = mockedStorageHandler.getAllUsersListings();
//    System.out.println("All Users Listings: " + allListings);
//
//    Map<String, String> params = new HashMap<>();
//    params.put("uid", testUid);
//    Request mockRequest = createMockRequest(params);
//    Response mockResponse = createMockResponse();
//
//    // Act
//    Object result = handler.handle(mockRequest, mockResponse);
//
//    // Assert
//    assertNotNull(result);
//    assertTrue(result instanceof String);
//    String jsonResult = (String) result;
//    System.out.println("JSON Result: " + jsonResult);
//
//    assertTrue(jsonResult.contains("\"response_type\":\"success\""), "Response should indicate success");
//    assertTrue(jsonResult.contains("\"listings\":["), "Should have a listings array");
//    assertTrue(jsonResult.contains("Test Listing"), "Should contain the test listing title");
//  }

  // Helper method to print out the internal database structure
  private void printDatabaseStructure(MockedFirebaseUtilities storage) {
    // Use reflection or toString to print out the entire database structure
    System.out.println("Database Structure: " + storage);

    // If you have access to the private database field, you could use reflection to print it
    try {
      java.lang.reflect.Field databaseField = MockedFirebaseUtilities.class.getDeclaredField("database");
      databaseField.setAccessible(true);
      Map<String, ?> database = (Map<String, ?>) databaseField.get(storage);
      System.out.println("Detailed Database Contents: " + database);
    } catch (Exception e) {
      System.out.println("Could not access database field: " + e.getMessage());
    }
  }
  //Mocked test: testing no listing but user (edge case)
  @Test
  public void testUserWithNoListings() throws Exception {
    // Arrange
    String testUid = "user456";

    Map<String, String> params = new HashMap<>();
    params.put("uid", testUid);
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Act
    Object result = handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    String jsonResult = (String) result;
    assertTrue(jsonResult.contains("\"response_type\":\"success\""));
    assertTrue(jsonResult.contains("\"listings\":[]"));
  }
  //Mocked test: testing invalid params (edge case)
  @Test
  public void testMissingUidParameter() {
    // Arrange
    Map<String, String> params = new HashMap<>();
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    // Act
    Object result = handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    String jsonResult = (String) result;
    assertTrue(jsonResult.contains("\"response_type\":\"failure\""));
    assertTrue(jsonResult.contains("\"error\""));
  }
  //Mocked test: testing success multiple listings
  @Test
  public void testMultipleListingsRetrieval() throws Exception {
    String testUid = "user789";

    // Add multiple mock listings
    for (int i = 1; i <= 5; i++) {
      Map<String, Object> listing = createMockListing(testUid, "listing" + i);
      listing.put("description", "Listing description " + i);
      mockedStorageHandler.addListing(testUid, "listings", "listing" + i, listing);
    }

    // Act
    Map<String, String> params = Map.of("uid", testUid);
    Request mockRequest = createMockRequest(params);
    Response mockResponse = createMockResponse();

    Object result = handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result, "Result should not be null");
    String jsonResult = (String) result;

    // Verify the response
    Map<String, Object> responseMap = parseJsonResponse(jsonResult);
    assertEquals("success", responseMap.get("response_type"));
    List<String> listings = (List<String>) responseMap.get("listings");
    assertEquals(5, listings.size(), "Should contain 5 listings");

    // Verify each listing contains expected content
    listings.forEach(listing -> {
      assertTrue(listing.contains("Test Listing"), "Each listing should contain the title");
      assertTrue(listing.contains("listing"), "Each listing should have a listingId");
      assertTrue(listing.contains("doc_id"), "Each listing should have a document ID");
    });
  }
//Helper method to parse response
  private Map<String, Object> parseJsonResponse(String jsonString) {
    // Parse JSON string using Moshi or any other JSON library
    JsonAdapter<Map> adapter =
        new Builder().build().adapter(Map.class);

    try {
      return adapter.fromJson(jsonString);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }

}
