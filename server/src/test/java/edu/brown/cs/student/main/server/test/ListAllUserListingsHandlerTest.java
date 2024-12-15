 package edu.brown.cs.student.main.server.test;

 import static org.junit.jupiter.api.Assertions.assertTrue;

 import edu.brown.cs.student.main.server.handlers.listingHandlers.ListAllUserListingsHandler;
 import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
 import edu.brown.cs.student.main.server.storage.StorageInterface;
 import java.util.HashMap;
 import java.util.Map;
 import org.junit.Before;
 import org.junit.Test;
 import spark.Request;
 import spark.Response;

 public class ListAllUserListingsHandlerTest {
  private StorageInterface mockStorage;
  private ListAllUserListingsHandler handler;

  @Before
  public void setUp() {
    // Initialize the mocked Firebase utility
    mockStorage = new MockedFirebaseUtilities();
    handler = new ListAllUserListingsHandler(mockStorage);
  }

  // Mocked test: testing success all listings
  @Test
  public void testSuccessfulListingsRetrieval() throws Exception {
    // Prepare and add mock listings for multiple users
    Map<String, Object> listing1 = createMockListing("user1", "listing1");
    Map<String, Object> listing2 = createMockListing("user2", "listing2");

    mockStorage.addListing(listing1);
    mockStorage.addListing(listing2);

    // Create mock request and response
    Request mockRequest = createMockRequest(new HashMap<>());
    Response mockResponse = createMockResponse();

    // Invoke handler
    String jsonResponse = (String) handler.handle(mockRequest, mockResponse);

    // Verify response
    assertTrue(jsonResponse.contains("\"response_type\":\"success\""));
    assertTrue(jsonResponse.contains("\"listings\":["));
    assertTrue(jsonResponse.contains("\"uid\":\"user1\""));
    assertTrue(jsonResponse.contains("\"uid\":\"user2\""));
  }

  // Mocked test: testing success empty listings
  @Test
  public void testEmptyListingsRetrieval() throws Exception {
    // Create mock request and response
    Request mockRequest = createMockRequest(new HashMap<>());
    Response mockResponse = createMockResponse();

    // Invoke handler
    String jsonResponse = (String) handler.handle(mockRequest, mockResponse);

    // Verify response
    assertTrue(jsonResponse.contains("\"response_type\":\"success\""));
    assertTrue(jsonResponse.contains("\"listings\":[]"));
  }

  // Mocked test: testing invalid params (edge case)
  @Test
  public void testListingsWithMissingFields() throws Exception {
    // Create listings with incomplete data
    Map<String, Object> incompleteListing1 = new HashMap<>();
    incompleteListing1.put("listingId", "listing1");

    Map<String, Object> incompleteListing2 = new HashMap<>();
    incompleteListing2.put("uid", "user2");

    mockStorage.addListing(incompleteListing1);
    mockStorage.addListing(incompleteListing2);

    Request mockRequest = createMockRequest(new HashMap<>());
    Response mockResponse = createMockResponse();

    String jsonResponse = (String) handler.handle(mockRequest, mockResponse);

    assertTrue(jsonResponse.contains("\"response_type\":\"success\""));
    assertTrue(jsonResponse.contains("\"listings\":["));
  }

  // Mocked test: testing empty params (edge case)
  @Test
  public void testListingsWithNullValues() throws Exception {
    Map<String, Object> nullValueListing = new HashMap<>();
    nullValueListing.put("uid", null);
    nullValueListing.put("listingId", null);
    nullValueListing.put("title", null);

    mockStorage.addListing(nullValueListing);

    Request mockRequest = createMockRequest(new HashMap<>());
    Response mockResponse = createMockResponse();

    String jsonResponse = (String) handler.handle(mockRequest, mockResponse);
    System.out.println(jsonResponse);

    assertTrue(jsonResponse.contains("\"response_type\":\"success\""));
    assertTrue(jsonResponse.contains("\"listings\":[{}]"));
  }

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
 }
