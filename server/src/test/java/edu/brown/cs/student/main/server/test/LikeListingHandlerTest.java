// package edu.brown.cs.student.main.server.test;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import edu.brown.cs.student.main.server.handlers.filterListingsHandlers.LikeListingHandler;
// import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ExecutionException;
// import org.junit.Before;
// import org.junit.Test;
// import spark.Request;
// import spark.Response;
//
// public class LikeListingHandlerTest {
//  private LikeListingHandler handler;
//  private MockedFirebaseUtilities mockStorage;
//
//  @Before
//  public void setUp() {
//    // Create mock storage implementation
//    mockStorage = new MockedFirebaseUtilities();
//    handler = new LikeListingHandler(mockStorage);
//  }
//
//  // Mocked test: testing like listing successfully to database
//  @Test
//  public void testSuccessfulLikeListing() throws InterruptedException, ExecutionException {
//    // Arrange
//    String uid = "user123";
//    String listingId = "listing456";
//    Map<String, Object> mockListing = createMockListing(uid, listingId);
//
//    // Prepare storage by adding the initial listing
//    mockStorage.addListing(uid, "listing", listingId, mockListing);
//
//    // Create mock request with parameters
//    Map<String, String> params = new HashMap<>();
//    params.put("uid", uid);
//    params.put("listingId", listingId);
//    Request mockRequest = createMockRequest(params);
//    Response mockResponse = createMockResponse();
//
//    // Act
//    Object result = handler.handle(mockRequest, mockResponse);
//
//    // Assert
//    assertNotNull(result);
//    assertTrue(result instanceof String);
//
//    // Verify the JSON response
//    String jsonResponse = (String) result;
//    assertTrue(jsonResponse.contains("\"response_type\":\"success\""));
//    assertTrue(jsonResponse.contains("\"uid\":\"" + uid + "\""));
//    assertTrue(jsonResponse.contains("\"listingId\":\"" + listingId + "\""));
//
//    // Verify storage interactions
//    List<Map<String, Object>> likedListings = mockStorage.getCollection(uid, "liked_listings");
//    assertFalse(likedListings.isEmpty());
//
//    // Safely check for the liked listing
//    boolean foundLikedListing =
//        likedListings.stream()
//            .anyMatch(
//                listing -> {
//                  // Check that the original listing data is contained in the stored listing
//                  return mockListing.entrySet().stream()
//                          .allMatch(
//                              entry ->
//                                  listing.containsKey(entry.getKey())
//                                      && listing.get(entry.getKey()).equals(entry.getValue()))
//                      ||
//                      // Also check using the doc_id naming convention
//                      ("liked-" + listingId).equals(listing.get("doc_id"));
//                });
//
//    assertTrue(foundLikedListing, "Liked listing not found in liked_listings");
//  }
//
//  // Mocked test: testing missing parameters
//  @Test
//  public void testMissingUid() {
//    // Arrange
//    String listingId = "listing456";
//    Map<String, String> params = new HashMap<>();
//    params.put("listingId", listingId);
//    Request mockRequest = createMockRequest(params);
//    Response mockResponse = createMockResponse();
//
//    // Act
//    Object result = handler.handle(mockRequest, mockResponse);
//
//    // Assert
//    assertNotNull(result);
//    assertTrue(result instanceof String);
//
//    // Verify failure response
//    String jsonResponse = (String) result;
//    assertTrue(jsonResponse.contains("\"response_type\":\"failure\""));
//    assertTrue(jsonResponse.contains("Both 'uid' and 'listingId' are required"));
//  }
//
//  // Mocked test: testing missing params (edge case)
//  @Test
//  public void testMissingListingId() {
//    // Arrange
//    String uid = "user123";
//    Map<String, String> params = new HashMap<>();
//    params.put("uid", uid);
//    Request mockRequest = createMockRequest(params);
//    Response mockResponse = createMockResponse();
//
//    // Act
//    Object result = handler.handle(mockRequest, mockResponse);
//
//    // Assert
//    assertNotNull(result);
//    assertTrue(result instanceof String);
//
//    // Verify failure response
//    String jsonResponse = (String) result;
//    assertTrue(jsonResponse.contains("\"response_type\":\"failure\""));
//    assertTrue(jsonResponse.contains("Both 'uid' and 'listingId' are required"));
//  }
//
//  // Helper method to create a mock request
//  private Request createMockRequest(Map<String, String> params) {
//    return new Request() {
//      @Override
//      public String queryParams(String key) {
//        return params.get(key);
//      }
//    };
//  }
//
//  //
//  // Helper method to create a mock response
//  private Response createMockResponse() {
//    return new Response() {
//      // Implement as needed (e.g., to capture status codes or headers)
//    };
//  }
//
//  // Helper method to create a mock listing
//  private Map<String, Object> createMockListing(String uid, String listingId) {
//    Map<String, Object> listing = new HashMap<>();
//    listing.put("uid", uid);
//    listing.put("listingId", listingId);
//    listing.put("title", "Test Listing");
//    return listing;
//  }
// }
