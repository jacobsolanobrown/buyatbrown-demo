package edu.brown.cs.student.main.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.brown.cs.student.main.server.handlers.CreateUserHandler;
import edu.brown.cs.student.main.server.storage.MockedFirebaseUtilities;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

public class CreateUserHandlerMockedFirebaseTest {

  private StorageInterface mockedFirebaseStorage;
  private CreateUserHandler createUserHandler;

  @BeforeEach
  void setUp() {
    // Initialize the mocked Firebase utility
    mockedFirebaseStorage = new MockedFirebaseUtilities();
    createUserHandler = new CreateUserHandler(mockedFirebaseStorage);
  }

  // Mocked test: testing success case
  @Test
  void testHandle_SuccessfulUserCreation() throws ExecutionException, InterruptedException {
    // Simulate HTTP request
    Request mockRequest = createMockRequest("mocker-1", "mockuser1", "mockuser@brown.edu");
    Response mockResponse = createMockResponse();

    // Execute handler
    String resultJson = (String) createUserHandler.handle(mockRequest, mockResponse);

    // Verify the data stored in MockedFirebaseUtilities
    List<Map<String, Object>> storedUsers =
        mockedFirebaseStorage.getCollection("mocker-1", "users");
    Map<String, Object> storedUser =
        storedUsers.stream()
            .filter(user -> "mocker-1".equals(user.get("uid")))
            .findFirst()
            .orElseThrow(() -> new AssertionError("User not found"));

    assertEquals("mockuser1", storedUser.get("username"));
    assertEquals("mockuser@brown.edu", storedUser.get("email"));

    // Verify the JSON response
    assertEquals(
        "{\"uid\":\"mocker-1\",\"response_type\":\"success\",\"username\":\"mockuser1\"}",
        resultJson);
  }

  // Mocked test: testing existing user (edge case)
  @Test
  void testHandle_ExistingUser() throws ExecutionException, InterruptedException {
    // First, add user to mock database
    Map<String, Object> existingUser =
        Map.of(
            "uid", "mocker-1",
            "username", "mockuser1",
            "email", "mockuser@brown.edu");

    // Add the existing user to the mock storage
    // Ensure you're using "users" as the collection name
    mockedFirebaseStorage.addDocument("mocker-1", "users", "mocker-1", existingUser);

    // Debugging: Print out all users after adding
    List<Map<String, Object>> allUsers = mockedFirebaseStorage.getAllUserDataMaps();
    System.out.println("DEBUG: All users after adding: " + allUsers);
    System.out.println("DEBUG: User count: " + allUsers.size());

    // Attempt to create a user with the same username
    Request mockRequest = createMockRequest("mocker-new-id", "mockuser1", "mock101@brown.edu");
    Response mockResponse = createMockResponse();

    // Execute handler
    String resultJson = (String) createUserHandler.handle(mockRequest, mockResponse);

    // Verify the response
    assertEquals(
        "{\"response_type\":\"failure\",\"error\":\"User with the username \\\"mockuser1\\\" already exists.\"}",
        resultJson);
  }

  // Mocked test: testing invalid params (edge case)
  @Test
  void testinvalidparams() throws ExecutionException, InterruptedException {
    Request mockRequest = createMockRequest("mocker-3", null, null);
    Response mockResponse = createMockResponse();

    System.out.println("users are " + mockedFirebaseStorage.getAllUsers());
    mockedFirebaseStorage.addDocument(
        "mocker-1",
        "users",
        "12345",
        Map.of(
            "uid", "mocker-1",
            "username", "mockuser1",
            "email", "mockuser@brown.edu"));

    // Execute handler
    String resultJson = (String) createUserHandler.handle(mockRequest, mockResponse);

    // Verify the response
    assertEquals(
        "{\"response_type\":\"failure\",\"error\":\"Both 'uid', 'username', and 'email' are required.\"}",
        resultJson);
  }

  private Request createMockRequest(String uid, String username, String email) {
    return new Request() {
      @Override
      public String queryParams(String key) {
        return switch (key) {
          case "uid" -> uid;
          case "username" -> username;
          case "email" -> email;
          default -> null;
        };
      }

      // Implement other methods if needed
    };
  }

  private Response createMockResponse() {
    return new Response() {
      // Implement methods if you need to test HTTP status codes or headers
    };
  }
}
