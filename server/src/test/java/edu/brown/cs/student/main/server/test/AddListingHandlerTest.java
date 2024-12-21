package edu.brown.cs.student.main.server.test;

import static edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler.countWordsBetweenCommas;
import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.handlers.listingHandlers.AddListingHandler;
import edu.brown.cs.student.main.server.storage.FirebaseUtilities;
import edu.brown.cs.student.main.server.storage.GoogleCloudStorageUtilities;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import edu.brown.cs.student.main.server.storage.MockedGoogleCloudStorageUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
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
    assertEquals(
        "I bought some Muji black pens in high school and brought them to Brown. "
            + "Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!",
        responseMap.get("description"));
    assertTrue(responseMap.get("imageUrl").toString().contains("http://mock-storage/"));
  }

  @Test
  void testAddListingGoodRequest() throws ExecutionException, InterruptedException, IOException {
    Map<String, String> queryParams = new HashMap<>();

    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    // 40 character title after trimming
    queryParams.put("title", "Elegant Wooden Table for Campus Dorms   ");
    // maximum tags
    queryParams.put("tags", "Tables,Organization,Dressers & storage,Kids,Decorations");
    // different casing
    queryParams.put("category", "furniture");
    // maximum price
    queryParams.put("price", "999999999.0");
    queryParams.put("condition", "like new");
    queryParams.put("description", "A really cool table.");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
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
    assertEquals("Elegant Wooden Table for Campus Dorms   ", responseMap.get("title"));
    assertEquals(
        "Tables,Organization,Dressers & storage,Kids,Decorations", responseMap.get("tags"));
    assertEquals("furniture", responseMap.get("category"));
    assertEquals("999999999.0", responseMap.get("price"));
    assertEquals("like new", responseMap.get("condition"));
    assertEquals("A really cool table.", responseMap.get("description"));
    assertTrue(responseMap.get("imageUrl").toString().contains("http://mock-storage/"));
  }

  @Test
  void testCountWordsBetweenCommas() {
    assertEquals(2, countWordsBetweenCommas("more, than 2, words, here"));
    assertEquals(1, countWordsBetweenCommas("    more, than"));
  }

  @Test
  void testInvalidTags() throws ExecutionException, InterruptedException, IOException {
    Map<String, String> queryParams = new HashMap<>();

    // ################### Test for null tags ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", null);
    queryParams.put("category", "School");
    queryParams.put("price", "10.99");
    queryParams.put("condition", "New");
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
            "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest nullTagMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullTagResult = mockAddListingHandler.handle(nullTagMockRequest, mockResponse);
    Map<String, Object> nullTagResponseMap = Utils.fromMoshiJson(nullTagResult.toString());
    assertEquals("failure", nullTagResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        nullTagResponseMap.get("error"));

    // ################### Check for Blank Tag ###################
    queryParams.put("tags", "");
    MockRequest emptyTagMockRequest = new MockRequest(queryParams, body);
    Object emptyTagResult = mockAddListingHandler.handle(emptyTagMockRequest, mockResponse);
    Map<String, Object> emptyTagResponseMap = Utils.fromMoshiJson(emptyTagResult.toString());
    assertEquals("failure", emptyTagResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        emptyTagResponseMap.get("error"));

    // ################### Check for Empty Tag ###################
    queryParams.put("tags", "   ");
    MockRequest blankTagMockRequest = new MockRequest(queryParams, body);
    Object blankTagResult = mockAddListingHandler.handle(blankTagMockRequest, mockResponse);
    Map<String, Object> blankTagResponseMap = Utils.fromMoshiJson(blankTagResult.toString());
    assertEquals("failure", blankTagResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        blankTagResponseMap.get("error"));

    // ################### Check for ill-formed Tag field ###################
    // spaces between commas
    queryParams.put("tags", "tag1, tag2");
    MockRequest illFormatedTagMockRequest = new MockRequest(queryParams, body);
    Object illFormatedTagResult =
        mockAddListingHandler.handle(illFormatedTagMockRequest, mockResponse);
    Map<String, Object> illFormatedResponseMap =
        Utils.fromMoshiJson(illFormatedTagResult.toString());
    assertEquals("failure", illFormatedResponseMap.get("response_type"));
    assertEquals(
        "Each tag should only have NO space between words and non before and " + "after commas.",
        illFormatedResponseMap.get("error"));

    // another potential invalid input
    queryParams.put("tags", "tag1 ,tag2");
    MockRequest illFormatedTag2MockRequest = new MockRequest(queryParams, body);
    Object illFormatedTag2Result =
        mockAddListingHandler.handle(illFormatedTag2MockRequest, mockResponse);
    Map<String, Object> illFormated2ResponseMap =
        Utils.fromMoshiJson(illFormatedTag2Result.toString());
    assertEquals("failure", illFormatedResponseMap.get("response_type"));
    assertEquals(
        "Each tag should only have NO space between words and non before and " + "after commas.",
        illFormatedResponseMap.get("error"));

    // double commas
    queryParams.put("tags", "tag1,,tag2");
    MockRequest wrongTagNumberMockRequest = new MockRequest(queryParams, body);
    Object wrongTagNumberResult =
        mockAddListingHandler.handle(wrongTagNumberMockRequest, mockResponse);
    Map<String, Object> wrongTagNumberResponseMap =
        Utils.fromMoshiJson(wrongTagNumberResult.toString());
    assertEquals("failure", wrongTagNumberResponseMap.get("response_type"));
    assertEquals("Each tag should have a value.", wrongTagNumberResponseMap.get("error"));

    // double commas
    queryParams.put("tags", "tag1,   ,tag2");
    MockRequest wrongTagNumber2MockRequest = new MockRequest(queryParams, body);
    Object wrongTagNumber2Result =
        mockAddListingHandler.handle(wrongTagNumber2MockRequest, mockResponse);
    Map<String, Object> wrongTagNumber2ResponseMap =
        Utils.fromMoshiJson(wrongTagNumber2Result.toString());
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
    Map<String, Object> tooManyTags2ResponseMap =
        Utils.fromMoshiJson(tooManyTags2Result.toString());
    assertEquals("failure", tooManyTags2ResponseMap.get("response_type"));
    assertEquals("Each tag should have a value.", tooManyTags2ResponseMap.get("error"));

    // more than 2 words in tag
    queryParams.put("tags", "more than 3 word tag");
    MockRequest tooManyWordsInTagMockRequest = new MockRequest(queryParams, body);
    Object tooManyWordsInTagResult =
        mockAddListingHandler.handle(tooManyWordsInTagMockRequest, mockResponse);
    Map<String, Object> tooManyWordsInTagResponseMap =
        Utils.fromMoshiJson(tooManyWordsInTagResult.toString());
    assertEquals("failure", tooManyWordsInTagResponseMap.get("response_type"));
    assertEquals(
        "Each tag should be less than or equal to 3 words.",
        tooManyWordsInTagResponseMap.get("error"));

    // duplicate entry tag
    queryParams.put("tags", "tag1,tag1");
    MockRequest duplicateTagMockRequest = new MockRequest(queryParams, body);
    Object duplicateTagResult = mockAddListingHandler.handle(duplicateTagMockRequest, mockResponse);
    Map<String, Object> duplicateWordsTagResponseMap =
        Utils.fromMoshiJson(duplicateTagResult.toString());
    assertEquals("failure", duplicateWordsTagResponseMap.get("response_type"));
    assertEquals(
        "Please make sure all tags are unique.", duplicateWordsTagResponseMap.get("error"));
  }

  @Test
  void testInvalidTitle() throws IOException {
    Map<String, String> queryParams = new HashMap<>();

    // ################### Test for null titles ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", null);
    queryParams.put("tags", "Stationary,Art Supplies");
    queryParams.put("category", "School");
    queryParams.put("price", "10.99");
    queryParams.put("condition", "New");
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
            "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest nullTitleMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullTitleResult = mockAddListingHandler.handle(nullTitleMockRequest, mockResponse);
    Map<String, Object> nullTitleResponseMap = Utils.fromMoshiJson(nullTitleResult.toString());
    assertEquals("failure", nullTitleResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        nullTitleResponseMap.get("error"));

    // ################### Check for Blank Title ###################
    queryParams.put("title", "");
    MockRequest blankTitleMockRequest = new MockRequest(queryParams, body);
    Object blankTitleResult = mockAddListingHandler.handle(blankTitleMockRequest, mockResponse);
    Map<String, Object> blankTitleResponseMap = Utils.fromMoshiJson(blankTitleResult.toString());
    assertEquals("failure", blankTitleResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        blankTitleResponseMap.get("error"));

    // ################### Check for Empty Title ###################
    queryParams.put("title", "    ");
    MockRequest emptyTitleMockRequest = new MockRequest(queryParams, body);
    Object emptyTitleResult = mockAddListingHandler.handle(emptyTitleMockRequest, mockResponse);
    Map<String, Object> emptyTitleResponseMap = Utils.fromMoshiJson(emptyTitleResult.toString());
    assertEquals("failure", emptyTitleResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        emptyTitleResponseMap.get("error"));

    // ################### Check for Title field being too long###################
    queryParams.put("title", "wayyyyyyyyyyyyyyyyyyyyyyyyy too long of a title!");
    MockRequest tooLongTitleMockRequest = new MockRequest(queryParams, body);
    Object tooLongTitleResult = mockAddListingHandler.handle(tooLongTitleMockRequest, mockResponse);
    Map<String, Object> tooLongTitleResponseMap =
        Utils.fromMoshiJson(tooLongTitleResult.toString());
    assertEquals("failure", tooLongTitleResponseMap.get("response_type"));
    assertEquals(
        "Title must be less than or equal to 40 characters.", tooLongTitleResponseMap.get("error"));
  }

  @Test
  void testInvalidPrice() throws IOException {
    Map<String, String> queryParams = new HashMap<>();
    // ################### Test for null price ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", "Stationary,Art Supplies");
    queryParams.put("category", "School");
    queryParams.put("price", null);
    queryParams.put("condition", "New");
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
            "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest nullMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullResult = mockAddListingHandler.handle(nullMockRequest, mockResponse);
    Map<String, Object> nullResponseMap = Utils.fromMoshiJson(nullResult.toString());
    assertEquals("failure", nullResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        nullResponseMap.get("error"));

    // ################### Check for Blank Price ###################
    queryParams.put("price", "");
    MockRequest blankMockRequest = new MockRequest(queryParams, body);
    Object blankResult = mockAddListingHandler.handle(blankMockRequest, mockResponse);
    Map<String, Object> blankResponseMap = Utils.fromMoshiJson(blankResult.toString());
    assertEquals("failure", blankResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        blankResponseMap.get("error"));

    // ################### Check for Empty Price ###################
    queryParams.put("price", "    ");
    MockRequest emptyMockRequest = new MockRequest(queryParams, body);
    Object emptyResult = mockAddListingHandler.handle(emptyMockRequest, mockResponse);
    Map<String, Object> emptyResponseMap = Utils.fromMoshiJson(emptyResult.toString());
    assertEquals("failure", emptyResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        emptyResponseMap.get("error"));

    // ################### Check for Invalid Prices###################
    // CHECK FOR NOT A NUMBER
    queryParams.put("price", "not a number");
    MockRequest notNumberMockRequest = new MockRequest(queryParams, body);
    Object notNumberResult = mockAddListingHandler.handle(notNumberMockRequest, mockResponse);
    Map<String, Object> notNumberResponseMap = Utils.fromMoshiJson(notNumberResult.toString());
    assertEquals("failure", notNumberResponseMap.get("response_type"));
    assertEquals("Invalid value for price: not a number", notNumberResponseMap.get("error"));

    // CHECK FOR NEGATIVE NUMBER
    queryParams.put("price", "-1.00");
    MockRequest negNumberMockRequest = new MockRequest(queryParams, body);
    Object negNumberResult = mockAddListingHandler.handle(negNumberMockRequest, mockResponse);
    Map<String, Object> negNumberResponseMap = Utils.fromMoshiJson(negNumberResult.toString());
    assertEquals("failure", negNumberResponseMap.get("response_type"));
    assertEquals(
        "Price cannot be negative or larger than 999999999.", negNumberResponseMap.get("error"));

    // CHECK FOR TOO LARGE OF A NUMBER
    queryParams.put("price", "1000000000");
    MockRequest largeNumberMockRequest = new MockRequest(queryParams, body);
    Object largeNumberResult = mockAddListingHandler.handle(largeNumberMockRequest, mockResponse);
    Map<String, Object> largeNumberResponseMap = Utils.fromMoshiJson(largeNumberResult.toString());
    assertEquals("failure", largeNumberResponseMap.get("response_type"));
    assertEquals(
        "Price cannot be negative or larger than 999999999.", largeNumberResponseMap.get("error"));

    // CHECK FOR TOO MANY DECIMAL POINTS NUMBER
    queryParams.put("price", "1.001");
    MockRequest decimalNumberMockRequest = new MockRequest(queryParams, body);
    Object decimalNumberResult =
        mockAddListingHandler.handle(decimalNumberMockRequest, mockResponse);
    Map<String, Object> decimalNumberResponseMap =
        Utils.fromMoshiJson(decimalNumberResult.toString());
    assertEquals("failure", decimalNumberResponseMap.get("response_type"));
    assertEquals("Price has more than two decimal points.", decimalNumberResponseMap.get("error"));
  }

  @Test
  void testInvalidCondition() throws IOException {
    Map<String, String> queryParams = new HashMap<>();

    // ################### Test for null condition ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", "Stationary,Art Supplies");
    queryParams.put("category", "School");
    queryParams.put("price", "10.99");
    queryParams.put("condition", null);
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
            "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest nullMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullResult = mockAddListingHandler.handle(nullMockRequest, mockResponse);
    Map<String, Object> nullResponseMap = Utils.fromMoshiJson(nullResult.toString());
    assertEquals("failure", nullResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        nullResponseMap.get("error"));

    // ################### Check for Blank Conditions ###################
    queryParams.put("condition", "");
    MockRequest blankMockRequest = new MockRequest(queryParams, body);
    Object blankResult = mockAddListingHandler.handle(blankMockRequest, mockResponse);
    Map<String, Object> blankResponseMap = Utils.fromMoshiJson(blankResult.toString());
    assertEquals("failure", blankResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        blankResponseMap.get("error"));

    // ################### Check for Empty Condition ###################
    queryParams.put("condition", "    ");
    MockRequest emptyMockRequest = new MockRequest(queryParams, body);
    Object emptyResult = mockAddListingHandler.handle(emptyMockRequest, mockResponse);
    Map<String, Object> emptyResponseMap = Utils.fromMoshiJson(emptyResult.toString());
    assertEquals("failure", emptyResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        emptyResponseMap.get("error"));

    // ################### Check for Condition not being one of the permitted
    // entries###################
    queryParams.put("condition", "good");
    MockRequest unavailableConditionOptionMockRequest = new MockRequest(queryParams, body);
    Object unavailableConditionOptionResult =
        mockAddListingHandler.handle(unavailableConditionOptionMockRequest, mockResponse);
    Map<String, Object> unavailableConditionResponseMap =
        Utils.fromMoshiJson(unavailableConditionOptionResult.toString());
    assertEquals("failure", unavailableConditionResponseMap.get("response_type"));
    assertEquals(
        "Please choose from valid condition inputs (i.e. New, Like New, or Used).",
        unavailableConditionResponseMap.get("error"));
  }

  @Test
  void testInvalidCategory() throws IOException {
    Map<String, String> queryParams = new HashMap<>();

    // ################### Test for null condition ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", "Stationary,Art Supplies");
    queryParams.put("category", null);
    queryParams.put("price", "10.99");
    queryParams.put("condition", "new");
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String workingDirectory = System.getProperty("user.dir");
    Path base64TestPath =
        Paths.get(
            workingDirectory,
            "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
    String body = new String(Files.readAllBytes(base64TestPath));
    MockRequest nullMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullResult = mockAddListingHandler.handle(nullMockRequest, mockResponse);
    Map<String, Object> nullResponseMap = Utils.fromMoshiJson(nullResult.toString());
    assertEquals("failure", nullResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        nullResponseMap.get("error"));

    // ################### Check for Blank Conditions ###################
    queryParams.put("category", "");
    MockRequest blankMockRequest = new MockRequest(queryParams, body);
    Object blankResult = mockAddListingHandler.handle(blankMockRequest, mockResponse);
    Map<String, Object> blankResponseMap = Utils.fromMoshiJson(blankResult.toString());
    assertEquals("failure", blankResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        blankResponseMap.get("error"));

    // ################### Check for Empty Condition ###################
    queryParams.put("category", "    ");
    MockRequest emptyMockRequest = new MockRequest(queryParams, body);
    Object emptyResult = mockAddListingHandler.handle(emptyMockRequest, mockResponse);
    Map<String, Object> emptyResponseMap = Utils.fromMoshiJson(emptyResult.toString());
    assertEquals("failure", emptyResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        emptyResponseMap.get("error"));

    // ################### Check for Condition not being one of the permitted
    // entries###################
    queryParams.put("category", "idk");
    MockRequest unavailableConditionOptionMockRequest = new MockRequest(queryParams, body);
    Object unavailableConditionOptionResult =
        mockAddListingHandler.handle(unavailableConditionOptionMockRequest, mockResponse);
    Map<String, Object> unavailableConditionResponseMap =
        Utils.fromMoshiJson(unavailableConditionOptionResult.toString());
    assertEquals("failure", unavailableConditionResponseMap.get("response_type"));
    assertEquals(
        "Please choose from valid category inputs (i.e. Clothes, Tech, School, "
            + "Furniture, Kitchen, or Bathroom).",
        unavailableConditionResponseMap.get("error"));
  }

  @Test
  void testInvalidImage() throws IOException {
    Map<String, String> queryParams = new HashMap<>();

    // ################### Test for null condition ###################
    queryParams.put("uid", "user123");
    queryParams.put("username", "testuser");
    queryParams.put("title", "Muji black pens");
    queryParams.put("tags", "Stationary,Art Supplies");
    queryParams.put("category", "School");
    queryParams.put("price", "10.99");
    queryParams.put("condition", "new");
    queryParams.put(
        "description",
        "I bought some Muji black pens in high school and brought them to Brown."
            + " Since coming here though, I only take notes using my ipad. I would hate for these pens"
            + " to go to worst. They are really nice. Let me know if you would like any!");

    String body = null;
    MockRequest nullMockRequest = new MockRequest(queryParams, body);

    MockResponse mockResponse = new MockResponse();
    Object nullResult = mockAddListingHandler.handle(nullMockRequest, mockResponse);
    Map<String, Object> nullResponseMap = Utils.fromMoshiJson(nullResult.toString());
    assertEquals("failure", nullResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        nullResponseMap.get("error"));

    // ################### Check for Blank Conditions ###################
    body = "";
    MockRequest blankMockRequest = new MockRequest(queryParams, body);
    Object blankResult = mockAddListingHandler.handle(blankMockRequest, mockResponse);
    Map<String, Object> blankResponseMap = Utils.fromMoshiJson(blankResult.toString());
    assertEquals("failure", blankResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        blankResponseMap.get("error"));

    // ################### Check for Empty Condition ###################
    body = "   ";
    MockRequest emptyMockRequest = new MockRequest(queryParams, body);
    Object emptyResult = mockAddListingHandler.handle(emptyMockRequest, mockResponse);
    Map<String, Object> emptyResponseMap = Utils.fromMoshiJson(emptyResult.toString());
    assertEquals("failure", emptyResponseMap.get("response_type"));
    assertEquals(
        "All listings arguments are required (title, tags, price, "
            + "image, category, condition, description)",
        emptyResponseMap.get("error"));
  }
}
