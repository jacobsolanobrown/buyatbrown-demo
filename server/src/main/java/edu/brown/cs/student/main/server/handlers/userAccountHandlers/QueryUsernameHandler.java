package edu.brown.cs.student.main.server.handlers.userAccountHandlers;

import edu.brown.cs.student.main.server.handlers.Utils;
import edu.brown.cs.student.main.server.storage.StorageInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class to query a username from the Firestore database. It will take in a userID and return a true
 * boolean along with the username associated with that userID. If the userID does not exist, it
 * will return a false boolean, and an empty string.
 */
public class QueryUsernameHandler implements Route {

  private final StorageInterface storageHandler;

  public QueryUsernameHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      String uid = request.queryParams("uid");

      if (uid == null || uid.isEmpty()) {
        throw new IllegalArgumentException("The user id is required.");
      }

      List<Map<String, Object>> allUsers = this.storageHandler.getAllUserDataMaps();
      System.out.println("all users in the database (count: " + allUsers.size() + "): " + allUsers);

      boolean userExists = allUsers.stream().anyMatch(user -> user.get("uid").equals(uid));

      if (userExists) {
        Map<String, Object> user =
            allUsers.stream().filter(u -> u.get("uid").equals(uid)).findFirst().get();
        String username = user.get("username").toString();
        System.out.println("Username (" + username + ") exists with uid (" + uid + ")");

        responseMap.put("response_type", "success");
        responseMap.put("uid", uid);
        responseMap.put("username", username);
        responseMap.put("exists", true);
      } else {
        responseMap.put("response_type", "success");
        responseMap.put("uid", uid);
        responseMap.put("username", "");
        responseMap.put("exists", false);
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    return Utils.toMoshiJson(responseMap);
  }
}
