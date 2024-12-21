// package edu.brown.cs.student.main.server.test;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler;
// import edu.brown.cs.student.main.server.handlers.userAccountHandlers.CreateUserHandler;
// import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
// import edu.brown.cs.student.main.server.storage.StorageInterface;
// import java.util.Collection;
// import java.util.Map;
// import java.util.concurrent.ExecutionException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import spark.Request;
// import spark.Response;
//
// public class OGAddListingHandlerTest {
//  private StorageInterface mockedstorage;
//  private AddListingHandler addListingHandler;
//
//  @BeforeEach
//  void setUp() {
//    // Initialize the mocked Firebase utility
//    mockedstorage = new MockedFirebaseUtilities();
//    addListingHandler = new AddListingHandler();
//  }
//
//  private Request createMockRequest(Map<String, String> params) {
//    return new Request() {
//      @Override
//      public String queryParams(String key) {
//        return params.get(key);
//      }
//    };
//  }
//
//  // Mocked test: testing adding listing successfully to database
//  @Test
//  void testHandle_ValidInput() throws ExecutionException, InterruptedException {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "title", "Cool Item",
//                "tags", "tag1,tag2",
//                "price", "10.99",
//                "imageUrl", "http://example.com/image.jpg",
//                "condition", "new",
//                "description", "A very cool item"));
//
//    Response mockResponse = createMockResponse();
//    CreateUserHandler createUserHandler = new CreateUserHandler(mockedstorage);
//    Request mockRequest2 =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "email", "user123@brown.edu"));
//    String createUserResult = (String) createUserHandler.handle(mockRequest2, mockResponse);
//    System.out.println(createUserResult);
//    assertTrue(createUserResult.contains("\"response_type\":\"success\""));
//    System.out.println("users are " + mockedstorage.getAllUsers());
//
//    // Execute AddListingHandler to add a listing
//    String addListingResult = (String) addListingHandler.handle(mockRequest, mockResponse);
//    //    System.out.println(addListingResult);
//    assertTrue(addListingResult.contains("\"response_type\":\"success\""));
//    // Retrieve the added listing from mocked storage
//    Collection<Map<String, Object>> listings = mockedstorage.getCollection("user123", "listings");
//    assertFalse(listings.isEmpty(), "Listings collection should not be empty");
//
//    // Get the most recently added listing (assuming the last one is the newest)
//    Map<String, Object> listing = listings.stream().reduce((first, second) ->
// second).orElseThrow();
//
//    assertNotNull(listing, "Listing should not be null");
//    System.out.println(listings);
//    System.out.println(listing);
//
//    // Debug output for verification
//    System.out.println("Retrieved Listing: " + listing);
//
//    // Assertions to verify listing fields
//    assertEquals("Cool Item", listing.get("title"), "Title does not match expected value");
//    assertEquals("10.99", listing.get("price"), "Price does not match expected value");
//    assertEquals(
//        "A very cool item",
//        listing.get("description"),
//        "Description does not match expected value");
//    assertEquals("new", listing.get("condition"), "Condition does not match expected value");
//  }
//
//  // Mocked test: testing missing params edge case
//  @Test
//  void testHandle_MissingParameters() {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "imageUrl", "http://example.com/image.jpg",
//                "price", "10.99"
//                // Missing title, tags, condition, description
//                ));
//    Response mockResponse = createMockResponse();
//    CreateUserHandler createUserHandler = new CreateUserHandler(mockedstorage);
//    Request mockRequest2 =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "email", "user123@brown.edu"));
//    String createUserResult = (String) createUserHandler.handle(mockRequest2, mockResponse);
//    assertTrue(createUserResult.contains("\"response_type\":\"success\""));
//
//    // Execute AddListingHandler to add a listing
//    String result = (String) addListingHandler.handle(mockRequest, mockResponse);
//    System.out.println(result);
//
//    assertTrue(result.contains("\"response_type\":\"failure\""));
//    assertTrue(result.contains("All listings arguments are required"));
//  }
//
//  // Mocked test: testing title format (edge case)
//  @Test
//  void testHandle_TitleTooLong() {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "imageUrl", "http://example.com/image.jpg",
//                "price", "10.99",
//                "title", "This title is definitely way too long for the allowed limit",
//                "tags", "tag1,tag2",
//                "condition", "new",
//                "description", "A very cool item"));
//    Response mockResponse = createMockResponse();
//
//    String result = (String) addListingHandler.handle(mockRequest, mockResponse);
//
//    assertTrue(result.contains("\"response_type\":\"failure\""));
//    assertTrue(result.contains("Title must be less than or equal to 40 characters"));
//  }
//
//  // Mocked test: testing price format (edge case)
//  @Test
//  void testHandle_NegativePrice() {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "imageUrl", "http://example.com/image.jpg",
//                "price", "-5.00",
//                "title", "Cool Item",
//                "tags", "tag1,tag2",
//                "condition", "new",
//                "description", "A very cool item"));
//    Response mockResponse = createMockResponse();
//
//    String result = (String) addListingHandler.handle(mockRequest, mockResponse);
//
//    assertTrue(result.contains("\"response_type\":\"failure\""));
//    assertTrue(result.contains("Price cannot be negative"));
//  }
//
//  // Mocked test: testing condition format (edge case)
//  @Test
//  void testHandle_InvalidCondition() {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "imageUrl", "http://example.com/image.jpg",
//                "price", "10.99",
//                "title", "Cool Item",
//                "tags", "tag1,tag2",
//                "condition", "old",
//                "description", "A very cool item"));
//    Response mockResponse = createMockResponse();
//
//    String result = (String) addListingHandler.handle(mockRequest, mockResponse);
//
//    assertTrue(result.contains("\"response_type\":\"failure\""));
//    assertTrue(result.contains("Please choose from valid condition inputs"));
//  }
//
//  // Mocked test: testing tag format (edge case)
//  @Test
//  void testHandle_DuplicateTags() {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "imageUrl", "http://example.com/image.jpg",
//                "price", "10.99",
//                "title", "Cool Item",
//                "tags", "tag1,tag1",
//                "condition", "new",
//                "description", "A very cool item"));
//    Response mockResponse = createMockResponse();
//
//    String result = (String) addListingHandler.handle(mockRequest, mockResponse);
//
//    assertTrue(result.contains("\"response_type\":\"failure\""));
//    assertTrue(result.contains("Please make sure all tags are unique"));
//  }
//
//  // Mocked test: testing tag format (edge case)
//  @Test
//  void testHandle_TooManyTags() {
//    Request mockRequest =
//        createMockRequest(
//            Map.of(
//                "uid", "user123",
//                "username", "testuser",
//                "imageUrl", "http://example.com/image.jpg",
//                "price", "10.99",
//                "title", "Cool Item",
//                "tags", "tag1,tag2,tag3,tag4,tag5,tag6",
//                "condition", "new",
//                "description", "A very cool item"));
//    Response mockResponse = createMockResponse();
//
//    String result = (String) addListingHandler.handle(mockRequest, mockResponse);
//
//    assertTrue(result.contains("\"response_type\":\"failure\""));
//    assertTrue(result.contains("Please input less than or equal to 5 tags"));
//  }
//
//  private Response createMockResponse() {
//    return new Response() {
//      // Implement as needed (e.g., to capture status codes or headers)
//    };
//  }
// }
