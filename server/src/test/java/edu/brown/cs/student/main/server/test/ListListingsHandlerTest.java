package edu.brown.cs.student.main.server.test;

import static org.junit.jupiter.api.Assertions.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi.Builder;
import edu.brown.cs.student.main.server.handlers.listingHandlers.ListListingsHandler;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

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
    listing.put("title", "Test Listing 3");
    return listing;
  }

  @BeforeEach
  public void setUp() {
    mockedStorageHandler = new MockedFirebaseUtilities();
    handler = new ListListingsHandler(mockedStorageHandler);

    Map<String, Object> listing = createMockListing("user123", "listing1");
    mockedStorageHandler.addListing(listing);
  }

  // Mocked Test Success Case
  @Test
  public void testSuccessfulListingsRetrieval() throws Exception {
    // Arrange
    String testUid = "user123";

    // Add test listings
    Map<String, Object> listing1 = new HashMap<>();
    listing1.put("title", "Test Listing 1");
    listing1.put("description", "Description 1");
    listing1.put("price", 100);

    Map<String, Object> listing2 = new HashMap<>();
    listing2.put("title", "Test Listing 2");
    listing2.put("description", "Description 2");
    listing2.put("price", 200);

    mockedStorageHandler.addListing(testUid, "listings", "listing1", listing1);
    mockedStorageHandler.addListing(testUid, "listings", "listing2", listing2);

    // Create mock request with required parameters
    Request mockRequest = createMockRequest(Map.of("uid", testUid));
    Response mockResponse = createMockResponse();

    // Act
    String result = (String) handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    Map<String, Object> responseMap = parseJsonResponse(result);

    assertEquals("success", responseMap.get("response_type"));
    List<Map<String, Object>> listings = (List<Map<String, Object>>) responseMap.get("listings");

    assertEquals(2, listings.size());
    assertTrue(
        listings.stream().anyMatch(listing -> "Test Listing 1".equals(listing.get("title"))));
    assertTrue(
        listings.stream().anyMatch(listing -> "Test Listing 2".equals(listing.get("title"))));
  }

  // Mocked Test Success Case but empty
  @Test
  public void testUserWithNoListings() throws Exception {
    // Arrange
    String testUid = "user456";
    Request mockRequest = createMockRequest(Map.of("uid", testUid));
    Response mockResponse = createMockResponse();

    // Act
    String result = (String) handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    Map<String, Object> responseMap = parseJsonResponse(result);

    assertEquals("success", responseMap.get("response_type"));
    List<Map<String, Object>> listings = (List<Map<String, Object>>) responseMap.get("listings");
    assertTrue(listings.isEmpty());
  }

  // Mocked Test: edge case error
  @Test
  public void testMissingUidParameter() {
    // Arrange
    Request mockRequest = createMockRequest(new HashMap<>());
    Response mockResponse = createMockResponse();

    // Act
    String result = (String) handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    Map<String, Object> responseMap = parseJsonResponse(result);

    assertEquals("failure", responseMap.get("response_type"));
    assertNotNull(responseMap.get("error"));
  }

  // Mocked Test Success Case multiple
  @Test
  public void testMultipleListingsRetrieval() throws Exception {
    // Arrange
    String testUid = "user789";

    // Add multiple listings
    for (int i = 1; i <= 5; i++) {
      Map<String, Object> listing = new HashMap<>();
      listing.put("title", "Test Listing " + i);
      listing.put("description", "Description " + i);
      listing.put("price", i * 100);

      mockedStorageHandler.addListing(testUid, "listings", "listing" + i, listing);
    }

    // Act
    Request mockRequest = createMockRequest(Map.of("uid", testUid));
    Response mockResponse = createMockResponse();
    String result = (String) handler.handle(mockRequest, mockResponse);

    // Assert
    assertNotNull(result);
    Map<String, Object> responseMap = parseJsonResponse(result);

    assertEquals("success", responseMap.get("response_type"));
    List<Map<String, Object>> listings = (List<Map<String, Object>>) responseMap.get("listings");

    assertEquals(5, listings.size());
    for (int i = 1; i <= 5; i++) {
      final int index = i;
      assertTrue(
          listings.stream()
              .anyMatch(listing -> ("Test Listing " + index).equals(listing.get("title"))));
    }
  }

  // Helper method to print out the internal database structure
  private void printDatabaseStructure(MockedFirebaseUtilities storage) {
    // Use reflection or toString to print out the entire database structure
    System.out.println("Database Structure: " + storage);

    // If you have access to the private database field, you could use reflection to print it
    try {
      java.lang.reflect.Field databaseField =
          MockedFirebaseUtilities.class.getDeclaredField("database");
      databaseField.setAccessible(true);
      Map<String, ?> database = (Map<String, ?>) databaseField.get(storage);
      System.out.println("Detailed Database Contents: " + database);
    } catch (Exception e) {
      System.out.println("Could not access database field: " + e.getMessage());
    }
  }

  // Helper method to parse response
  private Map<String, Object> parseJsonResponse(String jsonString) {
    // Parse JSON string using Moshi or any other JSON library
    JsonAdapter<Map> adapter = new Builder().build().adapter(Map.class);

    try {
      return adapter.fromJson(jsonString);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }
}
