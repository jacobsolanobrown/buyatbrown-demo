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
      if (uid == null || username == null || email == null) {
        System.out.println("UID: " + uid + ", username: " + username + ", email: " + email);
        throw new IllegalArgumentException("Both 'uid', 'username', and 'email' are required.");
      }
      // TOOD: Maybe add more validation for emails - such as the format includes an @ symbol and brown address
      System.out.println("Creating user with UID: " + uid + ", username: " + username + ", email: " + email);
      // Check if the user already exists
      //List<Map<String, Object>> allUsers = this.storageHandler.getAllUsers();

      List<Map<String, Object>> allUsers = this.storageHandler.getAllUserDataMaps();
      // THIS IS THE LIST OF ALL USERS
      System.out.println("all users in the database (count: " + allUsers.size() + "): " + allUsers);

      boolean usernameExists = allUsers.stream()
        .anyMatch(user -> user.get("username").equals(username));

      if (usernameExists) {
        System.out.println("Username (" + username + ") already exists!");
        responseMap.put("response_type", "failure");
        responseMap.put("error", "User with the username \"" + username + "\" already exists.");
        return Utils.toMoshiJson(responseMap);
      } else {
        System.out.println("Username is available.");
      }

//      boolean userExists = allUsers.stream()
//          .anyMatch(userMap -> {
//            // Navigate through the nested structure to find our users
//            Map<String, Object> collections = (Map<String, Object>) userMap.get("collections");
//            if (collections != null) {
//              Map<String, Object> users = (Map<String, Object>) collections.get("users");
//              // if there are users in the database
//              if (users != null) {
//                // get the values from the {user: email, uid, username} values
//                return users.values().stream()
//                    // check if the username already exists
//                    .anyMatch(userDetails -> {
//                      // get the username from the userDetails
//                      Map<String, Object> userDetailsMap = (Map<String, Object>) userDetails;
//                      System.out.println("Checking user details: " + userDetailsMap);
//                      // check if the username is the same as the one we are trying to create
//                      Object existingUsername = userDetailsMap.get("username");
//                      return existingUsername != null && existingUsername.toString().equalsIgnoreCase(username);
//                    });
//              }
//            }
//            return false;
//          });

//      if (userExists) {
//        // User already exists, send an error response
//        responseMap.put("response_type", "failure");
//        responseMap.put("error", "User with the username \"" + username + "\" already exists.");
//        return Utils.toMoshiJson(responseMap);
//      }


      // Create user data
      Map<String, Object> userData = new HashMap<>();
      userData.put("uid", uid);
      userData.put("username", username);
      if (email != null) userData.put("email", email);

      // Store the new user in the database
      storageHandler.addDocument(uid, "users", uid, userData);

      // Log the operation
      System.out.println("Created new user: " + username + " (UID: " + uid + ", EMAIL: " + email + ")");

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
