 package edu.brown.cs.student.main.server.test;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.junit.jupiter.api.Assertions.assertFalse;
 import static org.junit.jupiter.api.Assertions.assertNotNull;
 import static org.junit.jupiter.api.Assertions.assertTrue;

 import com.google.gson.Gson;
 import edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler;
 import edu.brown.cs.student.main.server.handlers.userAccountHandlers.CreateUserHandler;
 import edu.brown.cs.student.main.server.storage.GoogleCloudStorageUtilities;
 import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
 import edu.brown.cs.student.main.server.storage.MockedGoogleCloudStorageUtilities;
 import edu.brown.cs.student.main.server.storage.StorageInterface;
 import java.io.IOException;
 import java.util.Collection;
 import java.util.Map;
 import java.util.concurrent.ExecutionException;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import spark.Request;
 import spark.Response;

 public class AddListingHandlerTest {

   private StorageInterface mockedstorage;
   private AddListingHandler addListingHandler;
   private MockedGoogleCloudStorageUtilities mockedGcsHandler;

   @BeforeEach
   void setUp() throws IOException {
     // Initialize the mocked Firebase utility and Google Cloud Storage utility
     mockedstorage = new MockedFirebaseUtilities();
     mockedGcsHandler = new MockedGoogleCloudStorageUtilities();
     addListingHandler = new AddListingHandler(mockedstorage, mockedGcsHandler);
   }

   private Request createMockRequest(Map<String, String> queryParams, String body) {
     return new Request() {
       @Override
       public String queryParams(String key) {
         return queryParams.get(key);
       }

       @Override
       public String body() {
         return body;
       }
     };
   }

   // Mocked test: testing adding listing successfully to database
   @Test
   void testHandle_ValidInput() throws ExecutionException, InterruptedException {
     // Prepare base64 encoded image
     String base64Image = "base64encodedImageString";

     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "title", "Cool Item",
         "tags", "tagitems",
         "price", "10.99",
         "category", "Electronics",
         "condition", "new",
         "description", "A very cool item"
     );

     Request mockRequest = createMockRequest(queryParams, base64Image);
     Response mockResponse = createMockResponse();

     // AddListingHandler to add a listing
     String addListingResult = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(addListingResult, Map.class);
     assertEquals("success", resultMap.get("response_type"));

     // Retrieve the added listing from mocked storage
     Collection<Map<String, Object>> listings = mockedstorage.getCollection("user123", "listings");
     assertFalse(listings.isEmpty(), "Listings collection should not be empty");

     // most recently added listing
     Map<String, Object> listing = listings.stream().reduce((first, second) -> second)
         .orElseThrow();

     assertNotNull(listing, "Listing should not be null");
     assertEquals("Cool Item", listing.get("title"), "Title does not match expected value");
     assertEquals("10.99", listing.get("price"), "Price does not match expected value");
     assertEquals("A very cool item", listing.get("description"),
         "Description does not match expected value");
     assertEquals("new", listing.get("condition"), "Condition does not match expected value");
   }

   // Mocked test: testing missing params edge case
   @Test
   void testHandle_MissingParameters() {
     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "price", "10.99"
         //  title, tags, condition, description, category
     );

     Request mockRequest = createMockRequest(queryParams, "");
     Response mockResponse = createMockResponse();

     // Execute AddListingHandler to add a listing
     String result = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
     assertEquals("failure", resultMap.get("response_type"));
     assertTrue(resultMap.get("error").toString().contains("All listings arguments are required"));
   }

   // Mocked test: testing title format (edge case)
   @Test
   void testHandle_TitleTooLong() {
     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "price", "10.99",
         "title", "This title is definitely way too long for the allowed limit",
         "tags", "tag1,tag2",
         "category", "Electronics",
         "condition", "new",
         "description", "A very cool item"
     );

     Request mockRequest = createMockRequest(queryParams, "base64encodedImageString");
     Response mockResponse = createMockResponse();

     String result = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
     assertEquals("failure", resultMap.get("response_type"));
     assertTrue(resultMap.get("error").toString()
         .contains("Title must be less than or equal to 40 characters"));
   }

   // Mocked test: testing price format (edge case)
   @Test
   void testHandle_NegativePrice() {
     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "price", "-5.00",
         "title", "Cool Item",
         "tags", "tag1,tag2",
         "category", "Electronics",
         "condition", "new",
         "description", "A very cool item"
     );

     Request mockRequest = createMockRequest(queryParams, "base64encodedImageString");
     Response mockResponse = createMockResponse();

     String result = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
     assertEquals("failure", resultMap.get("response_type"));
     assertTrue(resultMap.get("error").toString().contains("Price cannot be negative"));
   }

   // Mocked test: testing condition format (edge case)
   @Test
   void testHandle_InvalidCondition() {
     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "price", "10.99",
         "title", "Cool Item",
         "tags", "tag1,tag2",
         "category", "Electronics",
         "condition", "old",
         "description", "A very cool item"
     );

     Request mockRequest = createMockRequest(queryParams, "base64encodedImageString");
     Response mockResponse = createMockResponse();

     String result = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
     assertEquals("failure", resultMap.get("response_type"));
     assertTrue(
         resultMap.get("error").toString().contains("Please choose from valid condition inputs"));
   }

   // Mocked test: testing tag format (edge case)
   @Test
   void testHandle_DuplicateTags() {
     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "price", "10.99",
         "title", "Cool Item",
         "tags", "tag1,tag1",
         "category", "Electronics",
         "condition", "new",
         "description", "A very cool item"
     );

     Request mockRequest = createMockRequest(queryParams, "base64encodedImageString");
     Response mockResponse = createMockResponse();

     String result = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
     assertEquals("failure", resultMap.get("response_type"));
     assertTrue(resultMap.get("error").toString().contains("Please make sure all tags are unique"));
   }

   // Mocked test: testing tag format (edge case)
   @Test
   void testHandle_TooManyTags() {
     Map<String, String> queryParams = Map.of(
         "uid", "user123",
         "username", "testuser",
         "price", "10.99",
         "title", "Cool Item",
         "tags", "tag1,tag2,tag3,tag4,tag5,tag6",
         "category", "Electronics",
         "condition", "new",
         "description", "A very cool item"
     );

     Request mockRequest = createMockRequest(queryParams, "base64encodedImageString");
     Response mockResponse = createMockResponse();

     String result = (String) addListingHandler.handle(mockRequest, mockResponse);

     Map<String, Object> resultMap = new Gson().fromJson(result, Map.class);
     assertEquals("failure", resultMap.get("response_type"));
     assertTrue(
         resultMap.get("error").toString().contains("Please input less than or equal to 5 tags"));
   }

   private Response createMockResponse() {
     return new Response() {
       // Implement as needed (e.g., to capture status codes or headers)
     };
   }
 }
