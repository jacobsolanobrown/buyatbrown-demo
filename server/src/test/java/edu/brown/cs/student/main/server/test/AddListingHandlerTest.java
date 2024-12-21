package edu.brown.cs.student.main.server.test;

import static edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler.countWordsBetweenCommas;
import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler;
import edu.brown.cs.student.main.server.handlers.userAccountHandlers.CreateUserHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.GoogleCloudStorageUtilities;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import edu.brown.cs.student.main.server.storage.MockedGoogleCloudStorageUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


class AddListingHandlerTest {

  private static AddListingHandler mockAddListingHandler;
  private static AddListingHandler addListingHandler;


  @BeforeAll
  public static void setUp() throws IOException {
    Map<String, Object> listing = new HashMap<>();

    // Set up mocking
    MockedFirebaseUtilities mockedFirebaseStorage = new MockedFirebaseUtilities();
    MockedGoogleCloudStorageUtilities mockedGCStorage = new MockedGoogleCloudStorageUtilities();
    mockAddListingHandler = new AddListingHandler(mockedFirebaseStorage, mockedGCStorage);
    try {
      mockedFirebaseStorage.createUser("user123", "testuser", "testemail@brown.edu");
    } catch (IllegalArgumentException e) {
      System.out.println("User already exists in mocked database. Skipping user creation...");
    } catch (ExecutionException | InterruptedException e) {
      System.out.println("Error creating mock user");
    }

    // For testing without mocking
    StorageInterface firebaseStorage = new FirebaseUtilities();
    GoogleCloudStorageUtilities gcsStorage = new GoogleCloudStorageUtilities();
    addListingHandler = new AddListingHandler(firebaseStorage, gcsStorage);
  }

  @Test
  void testAddListingPerfectRequest() throws ExecutionException, InterruptedException, IOException {
    Map<String, String> queryParams = new HashMap<>();

    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", "Stationary,Art Supplies");
    queryParams.put("category", "School");
    queryParams.put("price", "10.99");
    queryParams.put("condition", "New");
    queryParams.put("description",
      "I bought some Muji black pens in high school and brought them to Brown."
        + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
        + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath = Paths.get(workingDirectory,
      "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest mockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object result = mockAddListingHandler.handle(mockRequest, mockResponse);

    // Verify the result
    assertNotNull(result, "Result should not be null");

    // Check for correct ResponseMap
    Map<String, Object> responseMap = Utils.fromMoshiJson(result.toString());
    assertEquals("success", responseMap.get("response_type"));
    assertTrue(responseMap.get("listingId").toString().contains("listing-"));
    assertEquals("user123", responseMap.get("uid"));
    assertEquals("testuser", responseMap.get("username"));
    assertEquals("Muji black pens", responseMap.get("title"));
    assertEquals("Stationary,Art Supplies", responseMap.get("tags"));
    assertEquals("School", responseMap.get("category"));
    assertEquals("10.99", responseMap.get("price"));
    assertEquals("New", responseMap.get("condition"));
    assertEquals("I bought some Muji black pens in high school and brought them to Brown. "
        + "Since coming here though, I only take notes using my ipad. I would hate for these pens"
        + " to go to worst. They are really nice. Let me know if you would like any!",
      responseMap.get("description"));
    assertTrue(responseMap.get("imageUrl").toString().contains("http://mock-storage/"));
  }

  @Test
  void testCountWordsBetweenCommas() {
    assertEquals(2, countWordsBetweenCommas("more, than 2, words, here"));
  }


  @Test
  void testInvalidTags() throws ExecutionException, InterruptedException, IOException {
    Map<String, String> queryParams = new HashMap<>();

    //################### Test for null tags ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", null);
    queryParams.put("category", "School");
    queryParams.put("price", "10.99");
    queryParams.put("condition", "New");
    queryParams.put("description",
      "I bought some Muji black pens in high school and brought them to Brown."
        + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
        + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath = Paths.get(workingDirectory,
      "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest nullTagMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullTagResult = mockAddListingHandler.handle(nullTagMockRequest, mockResponse);
    Map<String, Object> nullTagResponseMap = Utils.fromMoshiJson(nullTagResult.toString());
    assertEquals("failure", nullTagResponseMap.get("response_type"));
    assertEquals("All listings arguments are required (title, tags, price, "
      + "image, category, condition, description)", nullTagResponseMap.get("error"));

    //################### Check for Blank Tag ###################
    queryParams.put("tags", "");
    MockRequest emptyTagMockRequest = new MockRequest(queryParams, body);
    Object emptyTagResult = mockAddListingHandler.handle(emptyTagMockRequest, mockResponse);
    Map<String, Object> emptyTagResponseMap = Utils.fromMoshiJson(emptyTagResult.toString());
    assertEquals("failure", emptyTagResponseMap.get("response_type"));
    assertEquals("All listings arguments are required (title, tags, price, "
      + "image, category, condition, description)", emptyTagResponseMap.get("error"));

    //################### Check for Empty Tag ###################
    queryParams.put("tags", "   ");
    MockRequest blankTagMockRequest = new MockRequest(queryParams, body);
    Object blankTagResult = mockAddListingHandler.handle(blankTagMockRequest, mockResponse);
    Map<String, Object> blankTagResponseMap = Utils.fromMoshiJson(blankTagResult.toString());
    assertEquals("failure", blankTagResponseMap.get("response_type"));
    assertEquals("All listings arguments are required (title, tags, price, "
      + "image, category, condition, description)", blankTagResponseMap.get("error"));

    //################### Check for ill-formed Tag field ###################
    // spaces between commas
    queryParams.put("tags", "tag1, tag2");
    MockRequest illFormatedTagMockRequest = new MockRequest(queryParams, body);
    Object illFormatedTagResult = mockAddListingHandler.handle(illFormatedTagMockRequest,
      mockResponse);
    Map<String, Object> illFormatedResponseMap = Utils.fromMoshiJson(
      illFormatedTagResult.toString());
    assertEquals("failure", illFormatedResponseMap.get("response_type"));
    assertEquals("Each tag should only have NO space between words and non before and "
      + "after commas.", illFormatedResponseMap.get("error"));

    // another potential invalid input
    queryParams.put("tags", "tag1 ,tag2");
    MockRequest illFormatedTag2MockRequest = new MockRequest(queryParams, body);
    Object illFormatedTag2Result = mockAddListingHandler.handle(illFormatedTag2MockRequest,
      mockResponse);
    Map<String, Object> illFormated2ResponseMap = Utils.fromMoshiJson(
      illFormatedTag2Result.toString());
    assertEquals("failure", illFormatedResponseMap.get("response_type"));
    assertEquals("Each tag should only have NO space between words and non before and "
      + "after commas.", illFormatedResponseMap.get("error"));

    // double commas
    queryParams.put("tags", "tag1,,tag2");
    MockRequest wrongTagNumberMockRequest = new MockRequest(queryParams, body);
    Object wrongTagNumberResult = mockAddListingHandler.handle(wrongTagNumberMockRequest,
      mockResponse);
    Map<String, Object> wrongTagNumberResponseMap = Utils.fromMoshiJson(
      wrongTagNumberResult.toString());
    assertEquals("failure", wrongTagNumberResponseMap.get("response_type"));
    assertEquals("Each tag should have a value.", wrongTagNumberResponseMap.get("error"));

    // double commas
    queryParams.put("tags", "tag1,   ,tag2");
    MockRequest wrongTagNumber2MockRequest = new MockRequest(queryParams, body);
    Object wrongTagNumber2Result = mockAddListingHandler.handle(wrongTagNumber2MockRequest,
      mockResponse);
    Map<String, Object> wrongTagNumber2ResponseMap = Utils.fromMoshiJson(
      wrongTagNumber2Result.toString());
    assertEquals("failure", wrongTagNumber2ResponseMap.get("response_type"));
    assertEquals(
      "Each tag should only have NO space between words and non before and after commas.",
      wrongTagNumber2ResponseMap.get("error"));

    // more than 5 tags
    queryParams.put("tags", "tag1,tag2,tag3,tag4,tag5,tag6");
    MockRequest tooManyTagsMockRequest = new MockRequest(queryParams, body);
    Object tooManyTagsResult = mockAddListingHandler.handle(tooManyTagsMockRequest, mockResponse);
    Map<String, Object> tooManyTagsResponseMap = Utils.fromMoshiJson(tooManyTagsResult.toString());
    assertEquals("failure", tooManyTagsResponseMap.get("response_type"));
    assertEquals("Please input less than or equal to 5 tags.", tooManyTagsResponseMap.get("error"));

    // more than 5 tags
    queryParams.put("tags", "tag1,tag2,tag3,tag4,tag5,,");
    MockRequest tooManyTags2MockRequest = new MockRequest(queryParams, body);
    Object tooManyTags2Result = mockAddListingHandler.handle(tooManyTags2MockRequest, mockResponse);
    Map<String, Object> tooManyTags2ResponseMap = Utils.fromMoshiJson(
      tooManyTags2Result.toString());
    assertEquals("failure", tooManyTags2ResponseMap.get("response_type"));
    assertEquals("Each tag should have a value.", tooManyTags2ResponseMap.get("error"));

    // more than 2 words in tag
    queryParams.put("tags", "more than 2 word tag");
    MockRequest tooManyWordsInTagMockRequest = new MockRequest(queryParams, body);
    Object tooManyWordsInTagResult = mockAddListingHandler.handle(tooManyWordsInTagMockRequest, mockResponse);
    Map<String, Object> tooManyWordsInTagResponseMap = Utils.fromMoshiJson(tooManyWordsInTagResult.toString());
    assertEquals("failure", tooManyWordsInTagResponseMap.get("response_type"));
    assertEquals("Each tag should be less than or equal to 2 words.", tooManyWordsInTagResponseMap.get("error"));

    // duplicate entry tag
    queryParams.put("tags", "tag1,tag1");
    MockRequest duplicateTagMockRequest = new MockRequest(queryParams, body);
    Object duplicateTagResult = mockAddListingHandler.handle(duplicateTagMockRequest, mockResponse);
    Map<String, Object> duplicateWordsTagResponseMap = Utils.fromMoshiJson(duplicateTagResult.toString());
    assertEquals("failure", duplicateWordsTagResponseMap.get("response_type"));
    assertEquals("Please make sure all tags are unique.", duplicateWordsTagResponseMap.get("error"));
  }
  }

//
//  @Test
//  void testInvalidTags() throws ExecutionException, InterruptedException, IOException {
//    Map<String, String> queryParams = new HashMap<>();
//
//    // test null tag
//    queryParams.put("uid", "user123");
//    queryParams.put("username", "testuser");
//    queryParams.put("title", "Muji black pens");
//    queryParams.put("tags", null);
//    queryParams.put("category", "School");
//    queryParams.put("price", "10.99");
//    queryParams.put("condition", "New");
//    queryParams.put("description",
//      "I bought some Muji black pens in high school and brought them to Brown."
//        + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
//        + " to go to worst. They are really nice. Let me know if you would like any!");
//
//    String workingDirectory = System.getProperty("user.dir");
//    Path base64TestPath = Paths.get(workingDirectory, "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
//    String body = new String(Files.readAllBytes(base64TestPath));
//    MockRequest mockRequest = new MockRequest(queryParams, body);
//
//    MockResponse mockResponse = new MockResponse();
//    Object result = mockAddListingHandler.handle(mockRequest, mockResponse);
//
//    // Verify the result
//    assertNotNull(result, "Result should not be null");
//
//    // Check for correct ResponseMap
//    Map<String, Object> responseMap = Utils.fromMoshiJson(result.toString());
//    assertEquals("failure", responseMap.get("response_type"));
//  }
//
//  @Test
//  void testInvalidTags() throws ExecutionException, InterruptedException, IOException {
//    Map<String, String> queryParams = new HashMap<>();
//
//    // test null tag
//    queryParams.put("uid", "user123");
//    queryParams.put("username", "testuser");
//    queryParams.put("title", "Muji black pens");
//    queryParams.put("tags", null);
//    queryParams.put("category", "School");
//    queryParams.put("price", "10.99");
//    queryParams.put("condition", "New");
//    queryParams.put("description",
//      "I bought some Muji black pens in high school and brought them to Brown."
//        + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
//        + " to go to worst. They are really nice. Let me know if you would like any!");
//
//    String workingDirectory = System.getProperty("user.dir");
//    Path base64TestPath = Paths.get(workingDirectory, "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
//    String body = new String(Files.readAllBytes(base64TestPath));
//    MockRequest mockRequest = new MockRequest(queryParams, body);
//
//    MockResponse mockResponse = new MockResponse();
//    Object result = mockAddListingHandler.handle(mockRequest, mockResponse);
//
//    // Verify the result
//    assertNotNull(result, "Result should not be null");
//
//    // Check for correct ResponseMap
//    Map<String, Object> responseMap = Utils.fromMoshiJson(result.toString());
//    assertEquals("failure", responseMap.get("response_type"));
//  }
//
//  @Test
//  void testInvalidTags() throws ExecutionException, InterruptedException, IOException {
//    Map<String, String> queryParams = new HashMap<>();
//
//    // test null tag
//    queryParams.put("uid", "user123");
//    queryParams.put("username", "testuser");
//    queryParams.put("title", "Muji black pens");
//    queryParams.put("tags", null);
//    queryParams.put("category", "School");
//    queryParams.put("price", "10.99");
//    queryParams.put("condition", "New");
//    queryParams.put("description",
//      "I bought some Muji black pens in high school and brought them to Brown."
//        + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
//        + " to go to worst. They are really nice. Let me know if you would like any!");
//
//    String workingDirectory = System.getProperty("user.dir");
//    Path base64TestPath = Paths.get(workingDirectory, "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
//    String body = new String(Files.readAllBytes(base64TestPath));
//    MockRequest mockRequest = new MockRequest(queryParams, body);
//
//    MockResponse mockResponse = new MockResponse();
//    Object result = mockAddListingHandler.handle(mockRequest, mockResponse);
//
//    // Verify the result
//    assertNotNull(result, "Result should not be null");
//
//    // Check for correct ResponseMap
//    Map<String, Object> responseMap = Utils.fromMoshiJson(result.toString());
//    assertEquals("failure", responseMap.get("response_type"));
//  }
//
//  @Test
//  void testInvalidTags() throws ExecutionException, InterruptedException, IOException {
//    Map<String, String> queryParams = new HashMap<>();
//
//    // test null tag
//    queryParams.put("uid", "user123");
//    queryParams.put("username", "testuser");
//    queryParams.put("title", "Muji black pens");
//    queryParams.put("tags", null);
//    queryParams.put("category", "School");
//    queryParams.put("price", "10.99");
//    queryParams.put("condition", "New");
//    queryParams.put("description",
//      "I bought some Muji black pens in high school and brought them to Brown."
//        + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
//        + " to go to worst. They are really nice. Let me know if you would like any!");
//
//    String workingDirectory = System.getProperty("user.dir");
//    Path base64TestPath = Paths.get(workingDirectory, "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
//    String body = new String(Files.readAllBytes(base64TestPath));
//    MockRequest mockRequest = new MockRequest(queryParams, body);
//
//    MockResponse mockResponse = new MockResponse();
//    Object result = mockAddListingHandler.handle(mockRequest, mockResponse);
//
//    // Verify the result
//    assertNotNull(result, "Result should not be null");
//
//    // Check for correct ResponseMap
//    Map<String, Object> responseMap = Utils.fromMoshiJson(result.toString());
//    assertEquals("failure", responseMap.get("response_type"));
//  }
//}