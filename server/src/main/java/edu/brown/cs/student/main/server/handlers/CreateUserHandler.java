package edu.brown.cs.student.main.server.handlers;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.HashMap;
import java.util.Map;

public class CreateUserHandler implements Route {

  private final StorageInterface storageHandler;

  public CreateUserHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  /**
   * Handles the request to create a new user.
   *
   * @param request  The HTTP request object
   * @param response The HTTP response object
   * @return A JSON response indicating success or failure
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Collect parameters from the request
      String uid = request.queryParams("uid");
      String username = request.queryParams("username");
      String email = request.queryParams("email");



      // Validate required parameters
      if (uid == null || username == null) {
        throw new IllegalArgumentException("Both 'uid' and 'username' are required.");
      }

      // Check if user already exists
      List<Map<String, Object>> allUsers = this.storageHandler.getAllUsers();

      Map<String, Object> user = allUsers.stream()
          .filter(listingMap -> listingMap.get("username").toString().equalsIgnoreCase(username)
              || username.equalsIgnoreCase("ignore"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("No listing found with the given ID: " + username));

      // Create user data
      Map<String, Object> userData = new HashMap<>();
      userData.put("uid", uid);
      userData.put("username", username);
      if (email != null) userData.put("email", email);

      // Store the new user in the database
      storageHandler.addDocument(uid, "users", uid, userData);

      // Log the operation
      System.out.println("Created new user: " + username + " (UID: " + uid + ")");

      // Prepare success response
      responseMap.put("response_type", "success");
      responseMap.put("uid", uid);
      responseMap.put("username", username);
    } catch (Exception e) {
      // Handle errors gracefully
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }

    // Return the response as JSON
    return Utils.toMoshiJson(responseMap);
  }
}
