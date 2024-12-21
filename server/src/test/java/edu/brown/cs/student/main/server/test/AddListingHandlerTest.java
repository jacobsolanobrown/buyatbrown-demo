package edu.brown.cs.student.main.server.test;

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

  @BeforeEach
  public void setUp() throws IOException {
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
    Path base64TestPath = Paths.get(workingDirectory, "src/test/java/edu/brown/cs/student/main/server/test/data/base64_testing_image.txt");
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
      + " to go to worst. They are really nice. Let me know if you would like any!", responseMap.get("description"));
    assertTrue(responseMap.get("imageUrl").toString().contains("http://mock-storage/"));

    // Check if the listing is in the database
  }

}